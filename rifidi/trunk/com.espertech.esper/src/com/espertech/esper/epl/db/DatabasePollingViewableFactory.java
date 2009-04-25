/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.db;

import com.espertech.esper.antlr.NoCaseSensitiveStream;
import com.espertech.esper.client.ConfigurationDBRef;
import com.espertech.esper.client.EventType;
import com.espertech.esper.core.EPStatementHandle;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.generated.EsperEPL2GrammarLexer;
import com.espertech.esper.epl.spec.DBStatementStreamSpec;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.util.*;
import com.espertech.esper.view.HistoricalEventViewable;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.*;
import java.util.*;

/**
 * Factory for a view onto historical data via SQL statement.
 */
public class DatabasePollingViewableFactory
{
    private static final String SAMPLE_WHERECLAUSE_PLACEHOLDER = "$ESPER-SAMPLE-WHERE";

    /**
     * Creates the viewable for polling via database SQL query.
     * @param streamNumber is the stream number of the view
     * @param databaseStreamSpec provides the SQL statement, database name and additional info
     * @param databaseConfigService for getting database connection and settings
     * @param eventAdapterService for generating event beans from database information
     * @param epStatementHandle is the statements-own handle for use in registering callbacks with services
     * @return viewable providing poll functionality
     * @throws ExprValidationException if the validation failed
     */
    public static HistoricalEventViewable createDBStatementView(int streamNumber,
                                                 DBStatementStreamSpec databaseStreamSpec,
                                                 DatabaseConfigService databaseConfigService,
                                                 EventAdapterService eventAdapterService,
                                                 EPStatementHandle epStatementHandle)
            throws ExprValidationException
    {
        // Parse the SQL for placeholders and text fragments
        List<PlaceholderParser.Fragment> sqlFragments = null;
        try
        {
            sqlFragments = PlaceholderParser.parsePlaceholder(databaseStreamSpec.getSqlWithSubsParams());
        }
        catch (PlaceholderParseException ex)
        {
            String text = "Error parsing SQL";
            throw new ExprValidationException(text + ", reason: " + ex.getMessage());
        }

        // Assemble a PreparedStatement and parameter list
        String preparedStatementText = createPreparedStatement(sqlFragments);
        SQLParameterDesc parameterDesc = getParameters(sqlFragments);
        if (log.isDebugEnabled())
        {
            log.debug(".createDBEventStream preparedStatementText=" + preparedStatementText +
                " parameterDesc=" + parameterDesc);
        }

        // Get a database connection
        String databaseName = databaseStreamSpec.getDatabaseName();
        DatabaseConnectionFactory databaseConnectionFactory = null;
        ColumnSettings metadataSetting = null;
        try
        {
            databaseConnectionFactory = databaseConfigService.getConnectionFactory(databaseName);
            metadataSetting = databaseConfigService.getQuerySetting(databaseName);
        }
        catch (DatabaseConfigException ex)
        {
            String text = "Error connecting to database '" + databaseName + '\'';
            log.error(text, ex);
            throw new ExprValidationException(text + ", reason: " + ex.getMessage());
        }

        Connection connection = null;
        try
        {
            connection = databaseConnectionFactory.getConnection();
        }
        catch (DatabaseConfigException ex)
        {
            String text = "Error connecting to database '" + databaseName + '\'';
            log.error(text, ex);
            throw new ExprValidationException(text + ", reason: " + ex.getMessage());
        }

        // On default setting, if we detect Oracle in the connection then don't query metadata from prepared statement
        ConfigurationDBRef.MetadataOriginEnum metaOriginPolicy = metadataSetting.getMetadataRetrievalEnum();
        if (metaOriginPolicy == ConfigurationDBRef.MetadataOriginEnum.DEFAULT)
        {
            String connectionClass = connection.getClass().getName();
            if (connectionClass.toLowerCase().contains("oracle"))
            {
                // switch to sample statement if we are dealing with an oracle connection
                metaOriginPolicy = ConfigurationDBRef.MetadataOriginEnum.SAMPLE;
            }
        }

        QueryMetaData queryMetaData;
        try
        {
            if ((metaOriginPolicy == ConfigurationDBRef.MetadataOriginEnum.METADATA) || (metaOriginPolicy == ConfigurationDBRef.MetadataOriginEnum.DEFAULT))
            {
                queryMetaData = getPreparedStmtMetadata(connection, parameterDesc.getParameters(), preparedStatementText, metadataSetting);
            }
            else
            {
                String sampleSQL;
                boolean isGivenMetadataSQL = true;
                if (databaseStreamSpec.getMetadataSQL() != null)
                {
                    sampleSQL = databaseStreamSpec.getMetadataSQL();
                    isGivenMetadataSQL = true;
                    if (log.isInfoEnabled())
                    {
                        log.info(".createDBStatementView Using provided sample SQL '" + sampleSQL + "'");
                    }
                }
                else
                {
                    // Create the sample SQL by replacing placeholders with null and
                    // SAMPLE_WHERECLAUSE_PLACEHOLDER with a "where 1=0" clause
                    sampleSQL = createSamplePlaceholderStatement(sqlFragments);

                    if (log.isInfoEnabled())
                    {
                        log.info(".createDBStatementView Using un-lexed sample SQL '" + sampleSQL + "'");
                    }

                    // If there is no SAMPLE_WHERECLAUSE_PLACEHOLDER, lexical analyse the SQL
                    // adding a "where 1=0" clause.
                    if (parameterDesc.getBuiltinIdentifiers().length != 1)
                    {
                        sampleSQL = lexSampleSQL(sampleSQL);
                        if (log.isInfoEnabled())
                        {
                            log.info(".createDBStatementView Using lexed sample SQL '" + sampleSQL + "'");
                        }
                    }
                }

                // finally get the metadata by firing the sample SQL
                queryMetaData = getExampleQueryMetaData(connection, parameterDesc.getParameters(), sampleSQL, metadataSetting, isGivenMetadataSQL);
            }
        }
        catch (ExprValidationException ex)
        {
            try
            {
                connection.close();
            }
            catch (SQLException e)
            {
                // don't handle
            }
            throw ex;
        }

        // Close connection
        try
        {
            connection.close();
        }
        catch (SQLException e)
        {
            String text = "Error closing connection";
            log.error(text, e);
            throw new ExprValidationException(text + ", reason: " + e.getMessage());
        }

        // Create event type
        // Construct an event type from SQL query result metadata
        Map<String, Object> eventTypeFields = new HashMap<String, Object>();
        for (Map.Entry<String, DBOutputTypeDesc> entry : queryMetaData.getOutputParameters().entrySet())
        {
            String name = entry.getKey();
            DBOutputTypeDesc dbOutputDesc = entry.getValue();

            Class clazz;
            if (dbOutputDesc.getOptionalBinding() != null)
            {
                clazz = dbOutputDesc.getOptionalBinding().getType();
            }
            else
            {
                clazz = SQLTypeMapUtil.sqlTypeToClass(dbOutputDesc.getSqlType(), dbOutputDesc.getClassName());
            }
            eventTypeFields.put(name, clazz);
        }
        EventType eventType = eventAdapterService.createAnonymousMapType(eventTypeFields);

        // Get a proper connection and data cache
        ConnectionCache connectionCache;
        DataCache dataCache;
        try
        {
            connectionCache = databaseConfigService.getConnectionCache(databaseName, preparedStatementText);
            dataCache = databaseConfigService.getDataCache(databaseName, epStatementHandle);
        }
        catch (DatabaseConfigException e)
        {
            String text = "Error obtaining cache configuration";
            log.error(text, e);
            throw new ExprValidationException(text + ", reason: " + e.getMessage());
        }

        PollExecStrategyDBQuery dbPollStrategy = new PollExecStrategyDBQuery(eventAdapterService,
                eventType, connectionCache, preparedStatementText, queryMetaData.getOutputParameters());

        return new DatabasePollingViewable(streamNumber, queryMetaData.getInputParameters(), dbPollStrategy, dataCache, eventType);
    }

    private static QueryMetaData getExampleQueryMetaData(Connection connection, String[] parameters, String sampleSQL, ColumnSettings metadataSetting, boolean isUsingMetadataSQL)
            throws ExprValidationException
    {
        // Simply add up all input parameters
        List<String> inputParameters = new LinkedList<String>();
        inputParameters.addAll(Arrays.asList(parameters));

        Statement statement;
        try
        {
            statement = connection.createStatement();
        }
        catch (SQLException ex)
        {
            String text = "Error creating statement";
            log.error(text, ex);
            throw new ExprValidationException(text + ", reason: " + ex.getMessage());
        }

        ResultSet result;
        try
        {
            result = statement.executeQuery(sampleSQL);
        }
        catch (SQLException ex)
        {
            String text;
            if (isUsingMetadataSQL)
            {
                text = "Error compiling metadata SQL to retrieve statement metadata, using sql text '" + sampleSQL + "'";
            }
            else
            {
                text = "Error compiling metadata SQL to retrieve statement metadata, consider using the 'metadatasql' syntax, using sql text '" + sampleSQL + "'";
            }

            log.error(text, ex);
            throw new ExprValidationException(text + ", reason: " + ex.getMessage());
        }

        Map<String, DBOutputTypeDesc> outputProperties;
        try
        {
            outputProperties = compileResultMetaData(result.getMetaData(), metadataSetting);
        }
        catch (SQLException ex)
        {
            try
            {
                statement.close();
            }
            catch (SQLException e)
            {
                // don't handle
            }
            String text = "Error in statement '" + sampleSQL + "', failed to obtain result metadata";
            log.error(text, ex);
            throw new ExprValidationException(text + ", please check the statement, reason: " + ex.getMessage());
        }

        return new QueryMetaData(inputParameters, outputProperties);
    }

    /**
     * Lexes the sample SQL and inserts a "where 1=0" where-clause.
     * @param querySQL to inspect using lexer
     * @return sample SQL with where-clause inserted
     * @throws ExprValidationException to indicate a lexer problem
     */
    protected static String lexSampleSQL(String querySQL)
                throws ExprValidationException
    {
        querySQL = querySQL.replaceAll("\\s\\s+|\\n|\\r", " ");
        StringReader reader = new StringReader(querySQL);
        CharStream input;
        try
        {
            input = new NoCaseSensitiveStream(reader);
        }
        catch (IOException ex)
        {
            throw new ExprValidationException("IOException lexing query SQL '" + querySQL + '\'', ex);
        }

        int whereIndex = -1;
        int groupbyIndex = -1;
        int havingIndex = -1;
        int orderByIndex = -1;
        List<Integer> unionIndexes = new ArrayList<Integer>();

        EsperEPL2GrammarLexer lex = new EsperEPL2GrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        List tokenList = tokens.getTokens();

        for (int i = 0; i < tokenList.size(); i++)
        {
            Token token = (Token) tokenList.get(i);
            if ((token == null) || token.getText() == null)
            {
                break;
            }
            String text = token.getText().toLowerCase().trim();
            if (text.equals("where"))
            {
                whereIndex = token.getCharPositionInLine() + 1;
            }
            if (text.equals("group"))
            {
                groupbyIndex = token.getCharPositionInLine() + 1;
            }
            if (text.equals("having"))
            {
                havingIndex = token.getCharPositionInLine() + 1;
            }
            if (text.equals("order"))
            {
                orderByIndex = token.getCharPositionInLine() + 1;
            }
            if (text.equals("union"))
            {
                unionIndexes.add(token.getCharPositionInLine() + 1);
            }
        }

        // If we have a union, break string into subselects and process each
        if (unionIndexes.size() != 0)
        {
            StringWriter changedSQL = new StringWriter();
            int lastIndex = 0;
            for (int i = 0; i < unionIndexes.size(); i++)
            {
                int index = unionIndexes.get(i);
                String fragment;
                if (i > 0)
                {
                    fragment = querySQL.substring(lastIndex + 5, index - 1);
                }
                else
                {
                    fragment = querySQL.substring(lastIndex, index - 1);
                }
                String lexedFragment = lexSampleSQL(fragment);

                if (i > 0)
                {
                    changedSQL.append("union ");
                }
                changedSQL.append(lexedFragment);
                lastIndex = index - 1;
            }

            // last part after last union
            String fragment = querySQL.substring(lastIndex + 5, querySQL.length());
            String lexedFragment = lexSampleSQL(fragment);
            changedSQL.append("union ");
            changedSQL.append(lexedFragment);

            return changedSQL.toString();
        }

        // Found a where clause, simplest cases
        if (whereIndex != -1)
        {
            StringWriter changedSQL = new StringWriter();
            String prefix = querySQL.substring(0, whereIndex + 5);
            String suffix = querySQL.substring(whereIndex + 5, querySQL.length());
            changedSQL.write(prefix);
            changedSQL.write("1=0 and ");
            changedSQL.write(suffix);
            return changedSQL.toString();
        }

        // No where clause, find group-by
        int insertIndex = -1;
        if (groupbyIndex != -1)
        {
            insertIndex = groupbyIndex;
        }
        else if (havingIndex != -1)
        {
            insertIndex = havingIndex;
        }
        else if (orderByIndex != -1)
        {
            insertIndex = orderByIndex;
        }
        else
        {
            StringWriter changedSQL = new StringWriter();
            changedSQL.write(querySQL);
            changedSQL.write(" where 1=0 ");
            return changedSQL.toString();
        }

        try
        {
            StringWriter changedSQL = new StringWriter();
            String prefix = querySQL.substring(0, insertIndex - 1);
            changedSQL.write(prefix);
            changedSQL.write("where 1=0 ");
            String suffix = querySQL.substring(insertIndex - 1, querySQL.length());
            changedSQL.write(suffix);
            return changedSQL.toString();
        }
        catch (Exception ex)
        {
            String text = "Error constructing sample SQL to retrieve metadata for JDBC-drivers that don't support metadata, consider using the " + SAMPLE_WHERECLAUSE_PLACEHOLDER + " placeholder or providing a sample SQL";
            log.error(text, ex);
            throw new ExprValidationException(text, ex);
        }
    }

    private static QueryMetaData getPreparedStmtMetadata(Connection connection,
                                                         String[] parameters,
                                                         String preparedStatementText,
                                                         ColumnSettings metadataSetting)
            throws ExprValidationException
    {
        PreparedStatement prepared;
        try
        {
            if (log.isInfoEnabled())
            {
                log.info(".getPreparedStmtMetadata Preparing statement '" + preparedStatementText + "'");
            }
            prepared = connection.prepareStatement(preparedStatementText);
        }
        catch (SQLException ex)
        {
            String text = "Error preparing statement '" + preparedStatementText + '\'';
            log.error(text, ex);
            throw new ExprValidationException(text + ", reason: " + ex.getMessage());
        }

        // Interrogate prepared statement - parameters and result
        List<String> inputParameters = new LinkedList<String>();
        try
        {
            ParameterMetaData parameterMetaData = prepared.getParameterMetaData();
            inputParameters.addAll(Arrays.asList(parameters).subList(0, parameterMetaData.getParameterCount()));
        }
        catch (Exception ex)
        {
            try
            {
                prepared.close();
            }
            catch (SQLException e)
            {
                // don't handle
            }
            String text = "Error obtaining parameter metadata from prepared statement, consider turning off metadata interrogation via configuration, for statement '" + preparedStatementText + '\'';
            log.error(text, ex);
            throw new ExprValidationException(text + ", please check the statement, reason: " + ex.getMessage());
        }

        Map<String, DBOutputTypeDesc> outputProperties;
        try
        {
            outputProperties = compileResultMetaData(prepared.getMetaData(), metadataSetting);
        }
        catch (SQLException ex)
        {
            try
            {
                prepared.close();
            }
            catch (SQLException e)
            {
                // don't handle
            }
            String text = "Error in statement '" + preparedStatementText + "', failed to obtain result metadata, consider turning off metadata interrogation via configuration";
            log.error(text, ex);
            throw new ExprValidationException(text + ", please check the statement, reason: " + ex.getMessage());
        }

        if (log.isDebugEnabled())
        {
            log.debug(".createDBEventStream in=" + inputParameters.toString() +
                " out=" + outputProperties.toString());
        }

        // Close statement
        try
        {
            prepared.close();
        }
        catch (SQLException e)
        {
            String text = "Error closing prepared statement";
            log.error(text, e);
            throw new ExprValidationException(text + ", reason: " + e.getMessage());
        }

        return new QueryMetaData(inputParameters, outputProperties);
    }

    private static String createPreparedStatement(List<PlaceholderParser.Fragment> parseFragements)
    {
        StringBuilder buffer = new StringBuilder();
        for (PlaceholderParser.Fragment fragment : parseFragements)
        {
            if (!fragment.isParameter())
            {
                buffer.append(fragment.getValue());
            }
            else
            {
                if (fragment.getValue().equals(SAMPLE_WHERECLAUSE_PLACEHOLDER))
                {
                    continue;
                }
                buffer.append('?');
            }
        }
        return buffer.toString();
    }

    private static String createSamplePlaceholderStatement(List<PlaceholderParser.Fragment> parseFragements)
    {
        StringBuilder buffer = new StringBuilder();
        for (PlaceholderParser.Fragment fragment : parseFragements)
        {
            if (!fragment.isParameter())
            {
                buffer.append(fragment.getValue());
            }
            else
            {
                if (fragment.getValue().equals(SAMPLE_WHERECLAUSE_PLACEHOLDER))
                {
                    buffer.append(" where 1=0 ");
                    break;
                }
                else
                {
                    buffer.append("null");
                }
            }
        }
        return buffer.toString();
    }

    private static SQLParameterDesc getParameters(List<PlaceholderParser.Fragment> parseFragements)
    {
        List<String> eventPropertyParams = new LinkedList<String>();
        for (PlaceholderParser.Fragment fragment : parseFragements)
        {
            if (fragment.isParameter())
            {
                if (!fragment.getValue().equals(SAMPLE_WHERECLAUSE_PLACEHOLDER))
                {
                    eventPropertyParams.add(fragment.getValue());
                }
            }
        }
        String[] params = eventPropertyParams.toArray(new String[eventPropertyParams.size()]);
        String[] builtin = eventPropertyParams.toArray(new String[eventPropertyParams.size()]);
        return new SQLParameterDesc(params, builtin);
    }

    private static Map<String, DBOutputTypeDesc> compileResultMetaData(ResultSetMetaData resultMetaData,
                                                                       ColumnSettings columnSettings
                                                                       )
            throws SQLException
    {
        Map<String, DBOutputTypeDesc> outputProperties = new HashMap<String, DBOutputTypeDesc>();
        for (int i = 0; i < resultMetaData.getColumnCount(); i++)
        {
            String columnName = resultMetaData.getColumnName(i + 1);
            int columnType = resultMetaData.getColumnType(i + 1);
            String javaClass = resultMetaData.getColumnTypeName(i + 1);

            ConfigurationDBRef.ColumnChangeCaseEnum caseEnum = columnSettings.getColumnCaseConversionEnum();
            if ((caseEnum != null) && (caseEnum == ConfigurationDBRef.ColumnChangeCaseEnum.LOWERCASE))
            {
                columnName = columnName.toLowerCase();
            }
            if ((caseEnum != null) && (caseEnum == ConfigurationDBRef.ColumnChangeCaseEnum.UPPERCASE))
            {
                columnName = columnName.toUpperCase();
            }

            DatabaseTypeBinding binding = null;
            String javaTypeBinding = null;
            if (columnSettings.getJavaSqlTypeBinding() != null)
            {
                javaTypeBinding = columnSettings.getJavaSqlTypeBinding().get(columnType);
            }
            if (javaTypeBinding != null)
            {
                binding = DatabaseTypeEnum.getEnum(javaTypeBinding).getBinding();
            }
            DBOutputTypeDesc outputType = new DBOutputTypeDesc(columnType, javaClass, binding);
            outputProperties.put(columnName, outputType);
        }
        return outputProperties;
    }

    private static final Log log = LogFactory.getLog(DatabasePollingViewableFactory.class);
}

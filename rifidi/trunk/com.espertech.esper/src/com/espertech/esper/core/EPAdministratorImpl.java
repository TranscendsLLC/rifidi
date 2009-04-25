/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core;

import com.espertech.esper.client.*;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.epl.generated.EsperEPL2GrammarParser;
import com.espertech.esper.epl.parse.*;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.antlr.ASTUtil;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation for the admin interface.
 */
public class EPAdministratorImpl implements EPAdministrator
{
    private static ParseRuleSelector patternParseRule;
    private static ParseRuleSelector eplParseRule;
    private static WalkRuleSelector patternWalkRule;
    private static WalkRuleSelector eplWalkRule;

    private EPServicesContext services;
    private ConfigurationOperations configurationOperations;
    private SelectClauseStreamSelectorEnum defaultStreamSelector;

    static
    {
        patternParseRule = new ParseRuleSelector()
        {
            public Tree invokeParseRule(EsperEPL2GrammarParser parser) throws RecognitionException
            {
                EsperEPL2GrammarParser.startPatternExpressionRule_return r = parser.startPatternExpressionRule();
                return (Tree) r.getTree();
            }
        };
        patternWalkRule = new WalkRuleSelector()
        {
            public void invokeWalkRule(EPLTreeWalker walker) throws RecognitionException
            {
                walker.startPatternExpressionRule();
            }
        };

        eplParseRule = new ParseRuleSelector()
        {
            public Tree invokeParseRule(EsperEPL2GrammarParser parser) throws RecognitionException
            {
                EsperEPL2GrammarParser.startEPLExpressionRule_return r = parser.startEPLExpressionRule();
                return (Tree) r.getTree();
            }
        };
        eplWalkRule = new WalkRuleSelector()
        {
            public void invokeWalkRule(EPLTreeWalker walker) throws RecognitionException
            {
                walker.startEPLExpressionRule();
            }
        };
    }

    /**
     * Constructor - takes the services context as argument.
     * @param services - references to services
     * @param configurationOperations - runtime configuration operations
     * @param defaultStreamSelector - the configuration for which insert or remove streams (or both) to produce
     */
    public EPAdministratorImpl(EPServicesContext services,
                               ConfigurationOperations configurationOperations,
                               SelectClauseStreamSelectorEnum defaultStreamSelector)
    {
        this.services = services;
        this.configurationOperations = configurationOperations;
        this.defaultStreamSelector = defaultStreamSelector;
    }

    public EPStatement createPattern(String onExpression) throws EPException
    {
        return createPatternStmt(onExpression, null, null);
    }

    public EPStatement createEPL(String eplStatement) throws EPException
    {
        return createEPLStmt(eplStatement, null, null);
    }

    public EPStatement createPattern(String expression, String statementName) throws EPException
    {
        return createPatternStmt(expression, statementName, null);
    }

    public EPStatement createPattern(String expression, String statementName, Object userObject) throws EPException
    {
        return createPatternStmt(expression, statementName, userObject);
    }

    public EPStatement createEPL(String eplStatement, String statementName) throws EPException
    {
        return createEPLStmt(eplStatement, statementName, null);
    }

    public EPStatement createEPL(String eplStatement, String statementName, Object userObject) throws EPException
    {
        return createEPLStmt(eplStatement, statementName, userObject);
    }

    public EPStatement createPattern(String expression, Object userObject) throws EPException
    {
        return createPatternStmt(expression, null, userObject);
    }

    public EPStatement createEPL(String eplStatement, Object userObject) throws EPException
    {
        return createEPLStmt(eplStatement, null, userObject);
    }

    private EPStatement createPatternStmt(String expression, String statementName, Object userObject) throws EPException
    {
        StatementSpecRaw rawPattern = compilePattern(expression);
        return services.getStatementLifecycleSvc().createAndStart(rawPattern, expression, true, statementName, userObject);

        /**
         * For round-trip testing of all statements, of a statement to SODA and creation from SODA, use below lines:
        String pattern = "select * from pattern[" + expression + "]";
        EPStatementObjectModel model = compileEPL(pattern);
        return create(model, statementName);
         */
    }

    private EPStatement createEPLStmt(String eplStatement, String statementName, Object userObject) throws EPException
    {
        StatementSpecRaw statementSpec = compileEPL(eplStatement, statementName, services, defaultStreamSelector);
        EPStatement statement = services.getStatementLifecycleSvc().createAndStart(statementSpec, eplStatement, false, statementName, userObject);

        log.debug(".createEPLStmt Statement created and started");
        return statement;

        /**
         * For round-trip testing of all statements, of a statement to SODA and creation from SODA, use below lines:
        EPStatementObjectModel model = compile(eplStatement);
        return create(model, statementName);
         */
    }

    public EPStatement create(EPStatementObjectModel sodaStatement) throws EPException
    {
        return create(sodaStatement, null);
    }

    public EPStatement create(EPStatementObjectModel sodaStatement, String statementName, Object userObject) throws EPException
    {
        // Specifies the statement
        StatementSpecRaw statementSpec = StatementSpecMapper.map(sodaStatement, services.getEngineImportService(), services.getVariableService(), services.getConfigSnapshot());
        String eplStatement = sodaStatement.toEPL();

        EPStatement statement = services.getStatementLifecycleSvc().createAndStart(statementSpec, eplStatement, false, statementName, userObject);

        log.debug(".createEPLStmt Statement created and started");
        return statement;
    }

    public EPStatement create(EPStatementObjectModel sodaStatement, String statementName) throws EPException
    {
        // Specifies the statement
        StatementSpecRaw statementSpec = StatementSpecMapper.map(sodaStatement, services.getEngineImportService(), services.getVariableService(), services.getConfigSnapshot());
        String eplStatement = sodaStatement.toEPL();

        EPStatement statement = services.getStatementLifecycleSvc().createAndStart(statementSpec, eplStatement, false, statementName, null);

        log.debug(".createEPLStmt Statement created and started");
        return statement;
    }

    public EPPreparedStatement prepareEPL(String eplExpression) throws EPException
    {
        // compile to specification
        StatementSpecRaw statementSpec = compileEPL(eplExpression, null, services, defaultStreamSelector);

        // map to object model thus finding all substitution parameters and their indexes
        StatementSpecUnMapResult unmapped = StatementSpecMapper.unmap(statementSpec);

        // the prepared statement is the object model plus a list of substitution parameters
        // map to specification will refuse any substitution parameters that are unfilled
        return new EPPreparedStatementImpl(unmapped.getObjectModel(), unmapped.getIndexedParams());
    }

    public EPPreparedStatement preparePattern(String patternExpression) throws EPException
    {
        StatementSpecRaw rawPattern = compilePattern(patternExpression);

        // map to object model thus finding all substitution parameters and their indexes
        StatementSpecUnMapResult unmapped = StatementSpecMapper.unmap(rawPattern);

        // the prepared statement is the object model plus a list of substitution parameters
        // map to specification will refuse any substitution parameters that are unfilled
        return new EPPreparedStatementImpl(unmapped.getObjectModel(), unmapped.getIndexedParams());
    }

    public EPStatement create(EPPreparedStatement prepared, String statementName, Object userObject) throws EPException
    {
        EPPreparedStatementImpl impl = (EPPreparedStatementImpl) prepared;

        StatementSpecRaw statementSpec = StatementSpecMapper.map(impl.getModel(), services.getEngineImportService(), services.getVariableService(), services.getConfigSnapshot());
        String eplStatement = impl.getModel().toEPL();

        return services.getStatementLifecycleSvc().createAndStart(statementSpec, eplStatement, false, statementName, userObject);
    }

    public EPStatement create(EPPreparedStatement prepared, String statementName) throws EPException
    {
        EPPreparedStatementImpl impl = (EPPreparedStatementImpl) prepared;

        StatementSpecRaw statementSpec = StatementSpecMapper.map(impl.getModel(), services.getEngineImportService(), services.getVariableService(), services.getConfigSnapshot());
        String eplStatement = impl.getModel().toEPL();

        return services.getStatementLifecycleSvc().createAndStart(statementSpec, eplStatement, false, statementName, null);
    }

    public EPStatement create(EPPreparedStatement prepared) throws EPException
    {
        return create(prepared, null);
    }

    public EPStatementObjectModel compileEPL(String eplStatement) throws EPException
    {
        StatementSpecRaw statementSpec = compileEPL(eplStatement, null, services, defaultStreamSelector);
        StatementSpecUnMapResult unmapped = StatementSpecMapper.unmap(statementSpec);
        if (unmapped.getIndexedParams().size() != 0)
        {
            throw new EPException("Invalid use of substitution parameters marked by '?' in statement, use the prepare method to prepare statements with substitution parameters");
        }
        return unmapped.getObjectModel();
    }

    public EPStatement getStatement(String name)
    {
        return services.getStatementLifecycleSvc().getStatementByName(name);
    }

    public String[] getStatementNames()
    {
        return services.getStatementLifecycleSvc().getStatementNames();
    }

    public void startAllStatements() throws EPException
    {
        services.getStatementLifecycleSvc().startAllStatements();
    }

    public void stopAllStatements() throws EPException
    {
        services.getStatementLifecycleSvc().stopAllStatements();
    }

    public void destroyAllStatements() throws EPException
    {
        services.getStatementLifecycleSvc().destroyAllStatements();
    }

    public ConfigurationOperations getConfiguration()
    {
        return configurationOperations;
    }

    /**
     * Destroys an engine instance.
     */
    public void destroy()
    {
        services = null;
        configurationOperations = null;
    }

    /**
     * Compile the EPL.
     * @param eplStatement expression to compile
     * @param statementName is the name of the statement
     * @param services is the context
     * @param defaultStreamSelector - the configuration for which insert or remove streams (or both) to produce
     * @return statement specification
     */
    protected static StatementSpecRaw compileEPL(String eplStatement, String statementName, EPServicesContext services, SelectClauseStreamSelectorEnum defaultStreamSelector)
    {
        if (log.isDebugEnabled())
        {
            log.debug(".createEPLStmt statementName=" + statementName + " eplStatement=" + eplStatement);
        }

        Tree ast = ParseHelper.parse(eplStatement, eplParseRule);
        CommonTreeNodeStream nodes = new CommonTreeNodeStream(ast);

        EPLTreeWalker walker = new EPLTreeWalker(nodes, services.getEngineImportService(), services.getVariableService(), services.getSchedulingService(), defaultStreamSelector, services.getEngineURI(), services.getConfigSnapshot());

        try
        {
            ParseHelper.walk(ast, walker, eplWalkRule, eplStatement);
        }
        catch (ASTWalkException ex)
        {
            log.error(".createEPL Error validating expression", ex);
            throw new EPStatementException(ex.getMessage(), eplStatement);
        }
        catch (EPStatementSyntaxException ex)
        {
            throw ex;
        }
        catch (RuntimeException ex)
        {
            String message = "Error in expression";
            log.debug(message, ex);
            throw new EPStatementException(getNullableErrortext(message, ex.getMessage()), eplStatement);
        }

        if (log.isDebugEnabled())
        {
            ASTUtil.dumpAST(ast);
        }

        // Specifies the statement
        return walker.getStatementSpec();
    }

    private StatementSpecRaw compilePattern(String expression)
    {
        // Parse and walk
        Tree ast = ParseHelper.parse(expression, patternParseRule);
        CommonTreeNodeStream nodes = new CommonTreeNodeStream(ast);
        EPLTreeWalker walker = new EPLTreeWalker(nodes, services.getEngineImportService(), services.getVariableService(), services.getSchedulingService(), defaultStreamSelector, services.getEngineURI(), services.getConfigSnapshot());

        try
        {
            ParseHelper.walk(ast, walker, patternWalkRule, expression);
        }
        catch (ASTWalkException ex)
        {
            log.debug(".createPattern Error validating expression", ex);
            throw new EPStatementException(ex.getMessage(), expression);
        }
        catch (EPStatementSyntaxException ex)
        {
            throw ex;
        }
        catch (RuntimeException ex)
        {
            String message = "Error in expression";
            log.debug(message, ex);
            throw new EPStatementException(getNullableErrortext(message, ex.getMessage()), expression);
        }

        if (log.isDebugEnabled())
        {
            ASTUtil.dumpAST(ast);
        }

        if (walker.getStatementSpec().getStreamSpecs().size() > 1)
        {
            throw new IllegalStateException("Unexpected multiple stream specifications encountered");
        }

        // Get pattern specification
        PatternStreamSpecRaw patternStreamSpec = (PatternStreamSpecRaw) walker.getStatementSpec().getStreamSpecs().get(0);

        // Create statement spec, set pattern stream, set wildcard select
        StatementSpecRaw statementSpec = new StatementSpecRaw(SelectClauseStreamSelectorEnum.ISTREAM_ONLY);
        statementSpec.getStreamSpecs().add(patternStreamSpec);
        statementSpec.getSelectClauseSpec().getSelectExprList().clear();
        statementSpec.getSelectClauseSpec().getSelectExprList().add(new SelectClauseElementWildcard());

        return statementSpec;
    }

    private static String getNullableErrortext(String msg, String cause)
    {
        if (cause == null)
        {
            return msg;
        }
        else
        {
            return msg + ": " + cause;
        }        
    }

    private static Log log = LogFactory.getLog(EPAdministratorImpl.class);
}

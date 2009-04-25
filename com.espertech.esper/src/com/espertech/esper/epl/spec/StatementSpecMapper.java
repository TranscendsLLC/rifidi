/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.spec;

import com.espertech.esper.client.ConfigurationInformation;
import com.espertech.esper.client.EPException;
import com.espertech.esper.client.soda.*;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.epl.agg.AggregationSupport;
import com.espertech.esper.epl.core.EngineImportException;
import com.espertech.esper.epl.core.EngineImportService;
import com.espertech.esper.epl.core.EngineImportUndefinedException;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.parse.ASTFilterSpecHelper;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.pattern.*;
import com.espertech.esper.type.CronOperatorEnum;
import com.espertech.esper.type.MathArithTypeEnum;
import com.espertech.esper.type.MinMaxTypeEnum;
import com.espertech.esper.type.RelationalOpEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper for mapping internal representations of a statement to the SODA object model for statements.
 */
public class StatementSpecMapper
{
    /**
     * Maps the SODA object model to a statement specification.
     * @param sodaStatement is the object model to map
     * @param engineImportService for resolving imports such as plug-in aggregations
     * @param variableService provides variable values
     * @param configuration supplies config values
     * @return statement specification, and internal representation of a statement
     */
    public static StatementSpecRaw map(EPStatementObjectModel sodaStatement, EngineImportService engineImportService, VariableService variableService, ConfigurationInformation configuration)
    {
        StatementSpecMapContext mapContext = new StatementSpecMapContext(engineImportService, variableService, configuration);

        StatementSpecRaw raw = map(sodaStatement, mapContext);
        if (mapContext.isHasVariables())
        {
            raw.setHasVariables(true);
        }
        return raw;
    }

    private static StatementSpecRaw map(EPStatementObjectModel sodaStatement, StatementSpecMapContext mapContext)
    {
        StatementSpecRaw raw = new StatementSpecRaw(SelectClauseStreamSelectorEnum.ISTREAM_ONLY);
        mapCreateWindow(sodaStatement.getCreateWindow(), raw, mapContext);
        mapCreateVariable(sodaStatement.getCreateVariable(), raw, mapContext);
        mapOnTrigger(sodaStatement.getOnExpr(), raw, mapContext);
        mapInsertInto(sodaStatement.getInsertInto(), raw);
        mapSelect(sodaStatement.getSelectClause(), raw, mapContext);
        mapFrom(sodaStatement.getFromClause(), raw, mapContext);
        mapWhere(sodaStatement.getWhereClause(), raw, mapContext);
        mapGroupBy(sodaStatement.getGroupByClause(), raw, mapContext);
        mapHaving(sodaStatement.getHavingClause(), raw, mapContext);
        mapOutputLimit(sodaStatement.getOutputLimitClause(), raw, mapContext);
        mapOrderBy(sodaStatement.getOrderByClause(), raw, mapContext);
        mapRowLimit(sodaStatement.getRowLimitClause(), raw, mapContext);
        return raw;
    }

    /**
     * Maps the internal representation of a statement to the SODA object model.
     * @param statementSpec is the internal representation
     * @return object model of statement
     */
    public static StatementSpecUnMapResult unmap(StatementSpecRaw statementSpec)
    {
        StatementSpecUnMapContext unmapContext = new StatementSpecUnMapContext();

        EPStatementObjectModel model = new EPStatementObjectModel();
        unmapCreateWindow(statementSpec.getCreateWindowDesc(), model, unmapContext);
        unmapCreateVariable(statementSpec.getCreateVariableDesc(), model, unmapContext);
        unmapOnClause(statementSpec.getOnTriggerDesc(), model, unmapContext);
        unmapInsertInto(statementSpec.getInsertIntoDesc(), model);
        unmapSelect(statementSpec.getSelectClauseSpec(), statementSpec.getSelectStreamSelectorEnum(), model, unmapContext);
        unmapFrom(statementSpec.getStreamSpecs(), statementSpec.getOuterJoinDescList(), model, unmapContext);
        unmapWhere(statementSpec.getFilterRootNode(), model, unmapContext);
        unmapGroupBy(statementSpec.getGroupByExpressions(), model, unmapContext);
        unmapHaving(statementSpec.getHavingExprRootNode(), model, unmapContext);
        unmapOutputLimit(statementSpec.getOutputLimitSpec(), model, unmapContext);
        unmapOrderBy(statementSpec.getOrderByList(), model, unmapContext);
        unmapRowLimit(statementSpec.getRowLimitSpec(), model, unmapContext);

        return new StatementSpecUnMapResult(model, unmapContext.getIndexedParams());
    }

    private static void unmapOnClause(OnTriggerDesc onTriggerDesc, EPStatementObjectModel model, StatementSpecUnMapContext unmapContext)
    {
        if (onTriggerDesc == null)
        {
            return;
        }
        if (onTriggerDesc.getOnTriggerType() == OnTriggerType.ON_DELETE)
        {
            OnTriggerWindowDesc window = (OnTriggerWindowDesc) onTriggerDesc;
            model.setOnExpr(new OnDeleteClause(window.getWindowName(), window.getOptionalAsName()));
        }
        if (onTriggerDesc.getOnTriggerType() == OnTriggerType.ON_SELECT)
        {
            OnTriggerWindowDesc window = (OnTriggerWindowDesc) onTriggerDesc;
            model.setOnExpr(new OnSelectClause(window.getWindowName(), window.getOptionalAsName()));
        }
        if (onTriggerDesc.getOnTriggerType() == OnTriggerType.ON_SET)
        {
            OnTriggerSetDesc trigger = (OnTriggerSetDesc) onTriggerDesc;
            OnSetClause clause = new OnSetClause();
            for (OnTriggerSetAssignment assignment : trigger.getAssignments())
            {
                Expression expr = unmapExpressionDeep(assignment.getExpression(), unmapContext);
                clause.addAssignment(assignment.getVariableName(), expr);
            }
            model.setOnExpr(clause);
        }
    }

    private static void unmapCreateWindow(CreateWindowDesc createWindowDesc, EPStatementObjectModel model, StatementSpecUnMapContext unmapContext)
    {
        if (createWindowDesc == null)
        {
            return;
        }
        Expression filter = null;
        if (createWindowDesc.getInsertFilter() != null)
        {
            filter = unmapExpressionDeep(createWindowDesc.getInsertFilter(), unmapContext);
        }

        CreateWindowClause clause = new CreateWindowClause(createWindowDesc.getWindowName(), unmapViews(createWindowDesc.getViewSpecs(), unmapContext));
        clause.setInsert(createWindowDesc.isInsert());
        clause.setInsertWhereClause(filter);
        model.setCreateWindow(clause);
    }

    private static void unmapCreateVariable(CreateVariableDesc createVariableDesc, EPStatementObjectModel model, StatementSpecUnMapContext unmapContext)
    {
        if (createVariableDesc == null)
        {
            return;
        }
        Expression assignment = null;
        if (createVariableDesc.getAssignment() != null)
        {
            assignment = unmapExpressionDeep(createVariableDesc.getAssignment(), unmapContext);
        }
        model.setCreateVariable(new CreateVariableClause(createVariableDesc.getVariableType(), createVariableDesc.getVariableName(), assignment));
    }

    private static void unmapOrderBy(List<OrderByItem> orderByList, EPStatementObjectModel model, StatementSpecUnMapContext unmapContext)
    {
        if ((orderByList == null) || (orderByList.size() == 0))
        {
            return;
        }

        OrderByClause clause = new OrderByClause();
        for (OrderByItem item : orderByList)
        {
            Expression expr = unmapExpressionDeep(item.getExprNode(), unmapContext);
            clause.add(expr, item.isDescending());
        }
        model.setOrderByClause(clause);
    }

    private static void unmapOutputLimit(OutputLimitSpec outputLimitSpec, EPStatementObjectModel model, StatementSpecUnMapContext unmapContext)
    {
        if (outputLimitSpec == null)
        {
            return;
        }

        OutputLimitSelector selector = OutputLimitSelector.DEFAULT;
        if (outputLimitSpec.getDisplayLimit() == OutputLimitLimitType.FIRST)
        {
            selector = OutputLimitSelector.FIRST;
        }
        if (outputLimitSpec.getDisplayLimit() == OutputLimitLimitType.LAST)
        {
            selector = OutputLimitSelector.LAST;
        }
        if (outputLimitSpec.getDisplayLimit() == OutputLimitLimitType.SNAPSHOT)
        {
            selector = OutputLimitSelector.SNAPSHOT;
        }
        if (outputLimitSpec.getDisplayLimit() == OutputLimitLimitType.ALL)
        {
            selector = OutputLimitSelector.ALL;
        }

        OutputLimitClause clause;
        OutputLimitUnit unit = OutputLimitUnit.EVENTS;
        if (outputLimitSpec.getRateType() == OutputLimitRateType.TIME_PERIOD)
        {
            unit = OutputLimitUnit.TIME_PERIOD;
            TimePeriodExpression timePeriod = (TimePeriodExpression) unmapExpressionDeep(outputLimitSpec.getTimePeriodExpr(), unmapContext);
            clause = new OutputLimitClause(selector, timePeriod);
        }
        else if (outputLimitSpec.getRateType() == OutputLimitRateType.WHEN_EXPRESSION)
        {
            unit = OutputLimitUnit.WHEN_EXPRESSION;
            Expression whenExpression = unmapExpressionDeep(outputLimitSpec.getWhenExpressionNode(), unmapContext);
            List<Pair<String, Expression>> thenAssignments = new ArrayList<Pair<String, Expression>>();
            clause = new OutputLimitClause(selector, whenExpression, thenAssignments);
            if (outputLimitSpec.getThenExpressions() != null)
            {
                for (OnTriggerSetAssignment assignment : outputLimitSpec.getThenExpressions())
                {
                    Expression expr = unmapExpressionDeep(assignment.getExpression(), unmapContext);
                    clause.addThenAssignment(assignment.getVariableName(), expr);
                }
            }
        }
        else if (outputLimitSpec.getRateType() == OutputLimitRateType.CRONTAB)
        {
            unit = OutputLimitUnit.CRONTAB_EXPRESSION;
            List<ExprNode> timerAtExpressions = outputLimitSpec.getCrontabAtSchedule();
            List<Expression> mappedExpr = unmapExpressionDeep(timerAtExpressions, unmapContext);
            clause = new OutputLimitClause(selector, mappedExpr.toArray(new Expression[mappedExpr.size()]));
        }
        else
        {
            clause = new OutputLimitClause(selector, outputLimitSpec.getRate(), outputLimitSpec.getVariableName(), unit);
        }

        model.setOutputLimitClause(clause);
    }

    private static void unmapRowLimit(RowLimitSpec rowLimitSpec, EPStatementObjectModel model, StatementSpecUnMapContext unmapContext)
    {
        if (rowLimitSpec == null)
        {
            return;
        }
        RowLimitClause spec = new RowLimitClause(rowLimitSpec.getNumRows(), rowLimitSpec.getOptionalOffset(),
                rowLimitSpec.getNumRowsVariable(), rowLimitSpec.getOptionalOffsetVariable());
        model.setRowLimitClause(spec);
    }

    private static void mapOrderBy(OrderByClause orderByClause, StatementSpecRaw raw, StatementSpecMapContext mapContext)
    {
        if (orderByClause == null)
        {
            return;
        }
        for (OrderByElement element : orderByClause.getOrderByExpressions())
        {
            ExprNode orderExpr = mapExpressionDeep(element.getExpression(), mapContext);
            OrderByItem item = new OrderByItem(orderExpr, element.isDescending());
            raw.getOrderByList().add(item);
        }
    }

    private static void mapOutputLimit(OutputLimitClause outputLimitClause, StatementSpecRaw raw, StatementSpecMapContext mapContext)
    {
        if (outputLimitClause == null)
        {
            return;
        }

        OutputLimitLimitType displayLimit = OutputLimitLimitType.valueOf(outputLimitClause.getSelector().toString().toUpperCase());

        OutputLimitRateType rateType;
        if (outputLimitClause.getUnit() == OutputLimitUnit.EVENTS)
        {
            rateType = OutputLimitRateType.EVENTS;
        }
        else if (outputLimitClause.getUnit() == OutputLimitUnit.TIME_PERIOD)
        {
            rateType = OutputLimitRateType.TIME_PERIOD;
        }
        else if (outputLimitClause.getUnit() == OutputLimitUnit.CRONTAB_EXPRESSION)
        {
            rateType = OutputLimitRateType.CRONTAB;
        }
        else if (outputLimitClause.getUnit() == OutputLimitUnit.WHEN_EXPRESSION)
        {
            rateType = OutputLimitRateType.WHEN_EXPRESSION;
        }
        else
        {
            throw new IllegalArgumentException("Unknown output limit unit " + outputLimitClause.getUnit());
        }

        Double frequency = outputLimitClause.getFrequency();
        String frequencyVariable = outputLimitClause.getFrequencyVariable();

        if (frequencyVariable != null)
        {
            mapContext.setHasVariables(true);
        }

        ExprNode whenExpression = null;
        List<OnTriggerSetAssignment> assignments = null;
        if (outputLimitClause.getWhenExpression() != null)
        {
            whenExpression = mapExpressionDeep(outputLimitClause.getWhenExpression(), mapContext);

            assignments = new ArrayList<OnTriggerSetAssignment>();
            for (Pair<String, Expression> pair : outputLimitClause.getThenAssignments())
            {
                ExprNode expr = mapExpressionDeep(pair.getSecond(), mapContext);
                assignments.add(new OnTriggerSetAssignment(pair.getFirst(), expr));
            }
        }

        List<ExprNode> timerAtExprList = null;
        if (outputLimitClause.getCrontabAtParameters() != null)
        {
            timerAtExprList = mapExpressionDeep(Arrays.asList(outputLimitClause.getCrontabAtParameters()), mapContext);
        }

        ExprTimePeriod timePeriod = null;
        if (outputLimitClause.getTimePeriodExpression() != null)
        {
            timePeriod = (ExprTimePeriod) mapExpressionDeep(outputLimitClause.getTimePeriodExpression(), mapContext);
        }

        OutputLimitSpec spec = new OutputLimitSpec(frequency, frequencyVariable, rateType, displayLimit, whenExpression, assignments, timerAtExprList, timePeriod);
        raw.setOutputLimitSpec(spec);
    }

    private static void mapOnTrigger(OnClause onExpr, StatementSpecRaw raw, StatementSpecMapContext mapContext)
    {
        if (onExpr == null)
        {
            return;
        }

        if (onExpr instanceof OnDeleteClause)
        {
            OnDeleteClause onDeleteClause = (OnDeleteClause) onExpr;
            raw.setOnTriggerDesc(new OnTriggerWindowDesc(onDeleteClause.getWindowName(), onDeleteClause.getOptionalAsName(), true));
        }
        else if (onExpr instanceof OnSelectClause)
        {
            OnSelectClause onSelectClause = (OnSelectClause) onExpr;
            raw.setOnTriggerDesc(new OnTriggerWindowDesc(onSelectClause.getWindowName(), onSelectClause.getOptionalAsName(), true));
        }
        else if (onExpr instanceof OnSetClause)
        {
            OnSetClause setClause = (OnSetClause) onExpr;
            mapContext.setHasVariables(true);
            List<OnTriggerSetAssignment> assignments = new ArrayList<OnTriggerSetAssignment>();
            for (Pair<String, Expression> pair : setClause.getAssignments())
            {
                ExprNode expr = mapExpressionDeep(pair.getSecond(), mapContext);
                assignments.add(new OnTriggerSetAssignment(pair.getFirst(), expr));
            }
            OnTriggerSetDesc desc = new OnTriggerSetDesc(assignments);
            raw.setOnTriggerDesc(desc);
        }
        else
        {
            throw new IllegalArgumentException("Cannot map on-clause expression type : " + onExpr);
        }
    }

    private static void mapRowLimit(RowLimitClause rowLimitClause, StatementSpecRaw raw, StatementSpecMapContext mapContext)
    {
        if (rowLimitClause == null)
        {
            return;
        }
        if ((rowLimitClause.getNumRowsVariable() != null) ||
            (rowLimitClause.getOptionalOffsetRowsVariable() != null))
        {
            raw.setHasVariables(true);
        }
        raw.setRowLimitSpec(new RowLimitSpec(rowLimitClause.getNumRows(), rowLimitClause.getOptionalOffsetRows(),
                rowLimitClause.getNumRowsVariable(), rowLimitClause.getOptionalOffsetRowsVariable()));
    }

    private static void mapHaving(Expression havingClause, StatementSpecRaw raw, StatementSpecMapContext mapContext)
    {
        if (havingClause == null)
        {
            return;
        }
        ExprNode node = mapExpressionDeep(havingClause, mapContext);
        raw.setHavingExprRootNode(node);
    }

    private static void unmapHaving(ExprNode havingExprRootNode, EPStatementObjectModel model, StatementSpecUnMapContext unmapContext)
    {
        if (havingExprRootNode == null)
        {
            return;
        }
        Expression expr = unmapExpressionDeep(havingExprRootNode, unmapContext);
        model.setHavingClause(expr);
    }

    private static void mapGroupBy(GroupByClause groupByClause, StatementSpecRaw raw, StatementSpecMapContext mapContext)
    {
        if (groupByClause == null)
        {
            return;
        }
        for (Expression expr : groupByClause.getGroupByExpressions())
        {
            ExprNode node = mapExpressionDeep(expr, mapContext);
            raw.getGroupByExpressions().add(node);
        }
    }

    private static void unmapGroupBy(List<ExprNode> groupByExpressions, EPStatementObjectModel model, StatementSpecUnMapContext unmapContext)
    {
        if (groupByExpressions.size() == 0)
        {
            return;
        }
        GroupByClause clause = new GroupByClause();
        for (ExprNode node : groupByExpressions)
        {
            Expression expr = unmapExpressionDeep(node, unmapContext);
            clause.getGroupByExpressions().add(expr);
        }
        model.setGroupByClause(clause);
    }

    private static void mapWhere(Expression whereClause, StatementSpecRaw raw, StatementSpecMapContext mapContext)
    {
        if (whereClause == null)
        {
            return;
        }
        ExprNode node = mapExpressionDeep(whereClause, mapContext);
        raw.setFilterRootNode(node);
    }

    private static void unmapWhere(ExprNode filterRootNode, EPStatementObjectModel model, StatementSpecUnMapContext unmapContext)
    {
        if (filterRootNode == null)
        {
            return;
        }
        Expression expr = unmapExpressionDeep(filterRootNode, unmapContext);
        model.setWhereClause(expr);
    }

    private static void unmapFrom(List<StreamSpecRaw> streamSpecs, List<OuterJoinDesc> outerJoinDescList, EPStatementObjectModel model, StatementSpecUnMapContext unmapContext)
    {
        FromClause from = new FromClause();
        model.setFromClause(from);

        for (StreamSpecRaw stream : streamSpecs)
        {
            Stream targetStream;
            if (stream instanceof FilterStreamSpecRaw)
            {
                FilterStreamSpecRaw filterStreamSpec = (FilterStreamSpecRaw) stream;
                Filter filter = unmapFilter(filterStreamSpec.getRawFilterSpec(), unmapContext);
                FilterStream filterStream = new FilterStream(filter, filterStreamSpec.getOptionalStreamName());
                unmapStreamOpts(stream.getOptions(), filterStream);
                targetStream = filterStream;
            }
            else if (stream instanceof DBStatementStreamSpec)
            {
                DBStatementStreamSpec db = (DBStatementStreamSpec) stream;
                targetStream = new SQLStream(db.getDatabaseName(), db.getSqlWithSubsParams(), db.getOptionalStreamName(), db.getMetadataSQL());
            }
            else if (stream instanceof PatternStreamSpecRaw)
            {
                PatternStreamSpecRaw pattern = (PatternStreamSpecRaw) stream;
                PatternExpr patternExpr = unmapPatternEvalDeep(pattern.getEvalNode(), unmapContext);
                PatternStream patternStream = new PatternStream(patternExpr, pattern.getOptionalStreamName());
                unmapStreamOpts(stream.getOptions(), patternStream);
                targetStream = patternStream;
            }
            else if (stream instanceof MethodStreamSpec)
            {
                MethodStreamSpec method = (MethodStreamSpec) stream;
                MethodInvocationStream methodStream = new MethodInvocationStream(method.getClassName(), method.getMethodName(), method.getOptionalStreamName());
                for (ExprNode exprNode : method.getExpressions())
                {
                    Expression expr = unmapExpressionDeep(exprNode, unmapContext);
                    methodStream.addParameter(expr);
                }
                targetStream = methodStream;
            }
            else
            {
                throw new IllegalArgumentException("Stream modelled by " + stream.getClass() + " cannot be unmapped");
            }

            if (targetStream instanceof ProjectedStream)
            {
                ProjectedStream projStream = (ProjectedStream) targetStream;
                for (ViewSpec viewSpec : stream.getViewSpecs())
                {
                    List<Expression> viewExpressions = unmapExpressionDeep(viewSpec.getObjectParameters(), unmapContext);
                    projStream.addView(View.create(viewSpec.getObjectNamespace(), viewSpec.getObjectName(), viewExpressions));
                }
            }
            from.add(targetStream);
        }

        for (OuterJoinDesc desc : outerJoinDescList)
        {
            PropertyValueExpression left = (PropertyValueExpression) unmapExpressionFlat(desc.getLeftNode(), unmapContext);
            PropertyValueExpression right = (PropertyValueExpression) unmapExpressionFlat(desc.getRightNode(), unmapContext);

            ArrayList<Pair<PropertyValueExpression, PropertyValueExpression>> additionalProperties = new ArrayList<Pair<PropertyValueExpression, PropertyValueExpression>>();
            if (desc.getAdditionalLeftNodes() != null)
            {
                for (int i = 0; i < desc.getAdditionalLeftNodes().length; i++)
                {
                    ExprIdentNode leftNode = desc.getAdditionalLeftNodes()[i];
                    ExprIdentNode rightNode = desc.getAdditionalRightNodes()[i];
                    PropertyValueExpression propLeft = (PropertyValueExpression) unmapExpressionFlat(leftNode, unmapContext);
                    PropertyValueExpression propRight = (PropertyValueExpression) unmapExpressionFlat(rightNode, unmapContext);
                    additionalProperties.add(new Pair<PropertyValueExpression, PropertyValueExpression>(propLeft, propRight));
                }
            }
            from.add(new OuterJoinQualifier(desc.getOuterJoinType(), left, right, additionalProperties));
        }
    }

    private static void unmapStreamOpts(StreamSpecOptions options, ProjectedStream stream)
    {
        stream.setUnidirectional(options.isUnidirectional());
        stream.setRetainUnion(options.isRetainUnion());
        stream.setRetainIntersection(options.isRetainIntersection());
    }

    private static StreamSpecOptions mapStreamOpts(ProjectedStream stream)
    {
        return new StreamSpecOptions(stream.isUnidirectional(), stream.isRetainUnion(), stream.isRetainIntersection());
    }

    private static void unmapSelect(SelectClauseSpecRaw selectClauseSpec, SelectClauseStreamSelectorEnum selectStreamSelectorEnum, EPStatementObjectModel model, StatementSpecUnMapContext unmapContext)
    {
        SelectClause clause = unmapSelectInternal(selectClauseSpec, selectStreamSelectorEnum, unmapContext);
        model.setSelectClause(clause);
    }

    private static SelectClause unmapSelectInternal(SelectClauseSpecRaw selectClauseSpec, SelectClauseStreamSelectorEnum selectStreamSelectorEnum, StatementSpecUnMapContext unmapContext)
    {
        SelectClause clause = SelectClause.create();
        clause.setStreamSelector(SelectClauseStreamSelectorEnum.mapFromSODA(selectStreamSelectorEnum));
        for (SelectClauseElementRaw raw : selectClauseSpec.getSelectExprList())
        {
            if (raw instanceof SelectClauseStreamRawSpec)
            {
                SelectClauseStreamRawSpec streamSpec = (SelectClauseStreamRawSpec) raw;
                clause.addStreamWildcard(streamSpec.getStreamName(), streamSpec.getOptionalAsName());
            }
            else if (raw instanceof SelectClauseElementWildcard)
            {
                clause.addWildcard();
            }
            else if (raw instanceof SelectClauseExprRawSpec)
            {
                SelectClauseExprRawSpec rawSpec = (SelectClauseExprRawSpec) raw;
                Expression expression = unmapExpressionDeep(rawSpec.getSelectExpression(), unmapContext);
                clause.add(expression, rawSpec.getOptionalAsName());
            }
            else
            {
                throw new IllegalStateException("Unexpected select clause element typed " + raw.getClass().getName());
            }
        }
        return clause;
    }

    private static void unmapInsertInto(InsertIntoDesc insertIntoDesc, EPStatementObjectModel model)
    {
        StreamSelector s = StreamSelector.ISTREAM_ONLY;
        if (insertIntoDesc == null)
        {
            return;
        }
        if (!insertIntoDesc.isIStream())
        {
            s = StreamSelector.RSTREAM_ONLY;
        }
        model.setInsertInto(
                InsertIntoClause.create(insertIntoDesc.getEventTypeName(),
                        insertIntoDesc.getColumnNames().toArray(new String[insertIntoDesc.getColumnNames().size()]), s));
    }

    private static void mapCreateWindow(CreateWindowClause createWindow, StatementSpecRaw raw, StatementSpecMapContext mapContext)
    {
        if (createWindow == null)
        {
            return;
        }

        ExprNode insertFromWhereExpr = null;
        if (createWindow.getInsertWhereClause() != null)
        {
            insertFromWhereExpr = mapExpressionDeep(createWindow.getInsertWhereClause(), mapContext);
        }
        raw.setCreateWindowDesc(new CreateWindowDesc(createWindow.getWindowName(), mapViews(createWindow.getViews(), mapContext), new StreamSpecOptions(), createWindow.isInsert(), insertFromWhereExpr));
    }

    private static void mapCreateVariable(CreateVariableClause createVariable, StatementSpecRaw raw, StatementSpecMapContext mapContext)
    {
        if (createVariable == null)
        {
            return;
        }

        ExprNode assignment = null;
        if (createVariable.getOptionalAssignment() != null)
        {
            assignment = mapExpressionDeep(createVariable.getOptionalAssignment(), mapContext);
        }

        raw.setCreateVariableDesc(new CreateVariableDesc(createVariable.getVariableType(), createVariable.getVariableName(), assignment));
    }

    private static void mapInsertInto(InsertIntoClause insertInto, StatementSpecRaw raw)
    {
        if (insertInto == null)
        {
            return;
        }

        boolean isIStream = insertInto.isIStream();
        String eventTypeName = insertInto.getStreamName();
        InsertIntoDesc desc = new InsertIntoDesc(isIStream, eventTypeName);

        for (String name : insertInto.getColumnNames())
        {
            desc.add(name);
        }

        raw.setInsertIntoDesc(desc);
    }

    private static void mapSelect(SelectClause selectClause, StatementSpecRaw raw, StatementSpecMapContext mapContext)
    {
        if (selectClause == null)
        {
            return;
        }
        SelectClauseSpecRaw spec = mapSelectRaw(selectClause, mapContext);
        raw.setSelectStreamDirEnum(SelectClauseStreamSelectorEnum.mapFromSODA(selectClause.getStreamSelector()));
        raw.setSelectClauseSpec(spec);
    }

    private static SelectClauseSpecRaw mapSelectRaw(SelectClause selectClause, StatementSpecMapContext mapContext)
    {
        SelectClauseSpecRaw spec = new SelectClauseSpecRaw();
        for (SelectClauseElement element : selectClause.getSelectList())
        {
            if (element instanceof SelectClauseWildcard)
            {
                spec.add(new SelectClauseElementWildcard());
            }
            else if (element instanceof SelectClauseExpression)
            {
                SelectClauseExpression selectExpr = (SelectClauseExpression) element;
                Expression expr = selectExpr.getExpression();
                ExprNode exprNode = mapExpressionDeep(expr, mapContext);
                SelectClauseExprRawSpec rawElement = new SelectClauseExprRawSpec(exprNode, selectExpr.getAsName());
                spec.add(rawElement);
            }
            else if (element instanceof SelectClauseStreamWildcard)
            {
                SelectClauseStreamWildcard streamWild = (SelectClauseStreamWildcard) element;
                SelectClauseStreamRawSpec rawElement = new SelectClauseStreamRawSpec(streamWild.getStreamName(), streamWild.getOptionalColumnName());
                spec.add(rawElement);
            }
        }
        return spec;
    }

    private static Expression unmapExpressionDeep(ExprNode exprNode, StatementSpecUnMapContext unmapContext)
    {
        Expression parent = unmapExpressionFlat(exprNode, unmapContext);
        unmapExpressionRecursive(parent, exprNode, unmapContext);
        return parent;
    }

    private static List<ExprNode> mapExpressionDeep(List<Expression> expressions, StatementSpecMapContext mapContext)
    {
        List<ExprNode> result = new ArrayList<ExprNode>();
        for (Expression expr : expressions)
        {
            result.add(mapExpressionDeep(expr, mapContext));
        }
        return result;
    }

    private static ExprNode mapExpressionDeepOptional(Expression expr, StatementSpecMapContext mapContext)
    {
        if (expr == null)
        {
            return null;
        }
        return mapExpressionDeep(expr, mapContext);
    }

    private static ExprNode mapExpressionDeep(Expression expr, StatementSpecMapContext mapContext)
    {
        ExprNode parent = mapExpressionFlat(expr, mapContext);
        mapExpressionRecursive(parent, expr, mapContext);
        return parent;
    }

    private static ExprNode mapExpressionFlat(Expression expr, StatementSpecMapContext mapContext)
    {
        if (expr == null)
        {
            throw new IllegalArgumentException("Null expression parameter");
        }
        if (expr instanceof ArithmaticExpression)
        {
            ArithmaticExpression arith = (ArithmaticExpression) expr;
            return new ExprMathNode(MathArithTypeEnum.parseOperator(arith.getOperator()),
                    mapContext.getConfiguration().getEngineDefaults().getExpression().isIntegerDivision(),
                    mapContext.getConfiguration().getEngineDefaults().getExpression().isDivisionByZeroReturnsNull());
        }
        else if (expr instanceof PropertyValueExpression)
        {
            PropertyValueExpression prop = (PropertyValueExpression) expr;
            int indexDot = ASTFilterSpecHelper.unescapedIndexOfDot(prop.getPropertyName());
            if (indexDot != -1)
            {
                String stream = prop.getPropertyName().substring(0, indexDot);
                String property = prop.getPropertyName().substring(indexDot + 1, prop.getPropertyName().length());
                return new ExprIdentNode(property, stream);
            }

            if (mapContext.getVariableService().getReader(prop.getPropertyName()) != null)
            {
                mapContext.setHasVariables(true);
                return new ExprVariableNode(prop.getPropertyName());
            }
            return new ExprIdentNode(prop.getPropertyName());
        }
        else if (expr instanceof Conjunction)
        {
            return new ExprAndNode();
        }
        else if (expr instanceof Disjunction)
        {
            return new ExprOrNode();
        }
        else if (expr instanceof RelationalOpExpression)
        {
            RelationalOpExpression op = (RelationalOpExpression) expr;
            if (op.getOperator().equals("="))
            {
                return new ExprEqualsNode(false);
            }
            if (op.getOperator().equals("!="))
            {
                return new ExprEqualsNode(true);
            }
            else
            {
                return new ExprRelationalOpNode(RelationalOpEnum.parse(op.getOperator()));
            }
        }
        else if (expr instanceof ConstantExpression)
        {
            ConstantExpression op = (ConstantExpression) expr;
            return new ExprConstantNode(op.getConstant());
        }
        else if (expr instanceof ConcatExpression)
        {
            return new ExprConcatNode();
        }
        else if (expr instanceof SubqueryExpression)
        {
            SubqueryExpression sub = (SubqueryExpression) expr;
            StatementSpecRaw rawSubselect = map(sub.getModel(), mapContext);
            return new ExprSubselectRowNode(rawSubselect);
        }
        else if (expr instanceof SubqueryInExpression)
        {
            SubqueryInExpression sub = (SubqueryInExpression) expr;
            StatementSpecRaw rawSubselect = map(sub.getModel(), mapContext);
            ExprSubselectInNode inSub = new ExprSubselectInNode(rawSubselect);
            inSub.setNotIn(sub.isNotIn());
            return inSub;
        }
        else if (expr instanceof SubqueryExistsExpression)
        {
            SubqueryExistsExpression sub = (SubqueryExistsExpression) expr;
            StatementSpecRaw rawSubselect = map(sub.getModel(), mapContext);
            return new ExprSubselectExistsNode(rawSubselect);
        }
        else if (expr instanceof SubqueryQualifiedExpression)
        {
            SubqueryQualifiedExpression sub = (SubqueryQualifiedExpression) expr;
            StatementSpecRaw rawSubselect = map(sub.getModel(), mapContext);
            boolean isNot = false;
            RelationalOpEnum relop = null;
            if (sub.getOperator().equals("!="))
            {
                isNot = true;
            }
            if (sub.getOperator().equals("="))
            {
            }
            else
            {
                relop = RelationalOpEnum.parse(sub.getOperator());
            }
            return new ExprSubselectAllSomeAnyNode(rawSubselect, isNot, sub.isAll(), relop);
        }
        else if (expr instanceof CountStarProjectionExpression)
        {
            return new ExprCountNode(false);
        }
        else if (expr instanceof CountProjectionExpression)
        {
            CountProjectionExpression count = (CountProjectionExpression) expr;
            return new ExprCountNode(count.isDistinct());
        }
        else if (expr instanceof AvgProjectionExpression)
        {
            AvgProjectionExpression avg = (AvgProjectionExpression) expr;
            return new ExprAvgNode(avg.isDistinct());
        }
        else if (expr instanceof SumProjectionExpression)
        {
            SumProjectionExpression avg = (SumProjectionExpression) expr;
            return new ExprSumNode(avg.isDistinct());
        }
        else if (expr instanceof BetweenExpression)
        {
            BetweenExpression between = (BetweenExpression) expr;
            return new ExprBetweenNode(between.isLowEndpointIncluded(), between.isHighEndpointIncluded(), between.isNotBetween());
        }
        else if (expr instanceof PriorExpression)
        {
            return new ExprPriorNode();
        }
        else if (expr instanceof PreviousExpression)
        {
            return new ExprPreviousNode();
        }
        else if (expr instanceof StaticMethodExpression)
        {
            StaticMethodExpression method = (StaticMethodExpression) expr;
            return new ExprStaticMethodNode(method.getClassName(), method.getMethod(),
                    mapContext.getConfiguration().getEngineDefaults().getExpression().isUdfCache());
        }
        else if (expr instanceof MinProjectionExpression)
        {
            MinProjectionExpression method = (MinProjectionExpression) expr;
            return new ExprMinMaxAggrNode(method.isDistinct(), MinMaxTypeEnum.MIN);
        }
        else if (expr instanceof MaxProjectionExpression)
        {
            MaxProjectionExpression method = (MaxProjectionExpression) expr;
            return new ExprMinMaxAggrNode(method.isDistinct(), MinMaxTypeEnum.MAX);
        }
        else if (expr instanceof NotExpression)
        {
            return new ExprNotNode();
        }
        else if (expr instanceof InExpression)
        {
            InExpression in = (InExpression) expr;
            return new ExprInNode(in.isNotIn());
        }
        else if (expr instanceof CoalesceExpression)
        {
            return new ExprCoalesceNode();
        }
        else if (expr instanceof CaseWhenThenExpression)
        {
            return new ExprCaseNode(false);
        }
        else if (expr instanceof CaseSwitchExpression)
        {
            return new ExprCaseNode(true);
        }
        else if (expr instanceof MaxRowExpression)
        {
            return new ExprMinMaxRowNode(MinMaxTypeEnum.MAX);
        }
        else if (expr instanceof MinRowExpression)
        {
            return new ExprMinMaxRowNode(MinMaxTypeEnum.MIN);
        }
        else if (expr instanceof BitwiseOpExpression)
        {
            BitwiseOpExpression bit = (BitwiseOpExpression) expr;
            return new ExprBitWiseNode(bit.getBinaryOp());
        }
        else if (expr instanceof ArrayExpression)
        {
            return new ExprArrayNode();
        }
        else if (expr instanceof LikeExpression)
        {
            LikeExpression like = (LikeExpression) expr;
            return new ExprLikeNode(like.isNot());
        }
        else if (expr instanceof RegExpExpression)
        {
            RegExpExpression regexp = (RegExpExpression) expr;
            return new ExprRegexpNode(regexp.isNot());
        }
        else if (expr instanceof MedianProjectionExpression)
        {
            MedianProjectionExpression median = (MedianProjectionExpression) expr;
            return new ExprMedianNode(median.isDistinct());
        }
        else if (expr instanceof AvedevProjectionExpression)
        {
            AvedevProjectionExpression node = (AvedevProjectionExpression) expr;
            return new ExprAvedevNode(node.isDistinct());
        }
        else if (expr instanceof StddevProjectionExpression)
        {
            StddevProjectionExpression node = (StddevProjectionExpression) expr;
            return new ExprStddevNode(node.isDistinct());
        }
        else if (expr instanceof InstanceOfExpression)
        {
            InstanceOfExpression node = (InstanceOfExpression) expr;
            return new ExprInstanceofNode(node.getTypeNames());
        }
        else if (expr instanceof CastExpression)
        {
            CastExpression node = (CastExpression) expr;
            return new ExprCastNode(node.getTypeName());
        }
        else if (expr instanceof PropertyExistsExpression)
        {
            return new ExprPropertyExistsNode();
        }
        else if (expr instanceof CurrentTimestampExpression)
        {
            return new ExprTimestampNode();
        }
        else if (expr instanceof TimePeriodExpression)
        {
            TimePeriodExpression tpe = (TimePeriodExpression) expr;
            return new ExprTimePeriod(tpe.isHasDays(), tpe.isHasHours(), tpe.isHasMinutes(), tpe.isHasSeconds(), tpe.isHasMilliseconds());
        }
        else if (expr instanceof CompareListExpression)
        {
            CompareListExpression exp = (CompareListExpression) expr;
            if ((exp.getOperator().equals("=")) || (exp.getOperator().equals("!=")))
            {
                return new ExprEqualsAllAnyNode((exp.getOperator().equals("!=")), exp.isAll());
            }
            else
            {
                return new ExprRelationalOpAllAnyNode(RelationalOpEnum.parse(exp.getOperator()), exp.isAll());
            }
        }
        else if (expr instanceof SubstitutionParameterExpression)
        {
            SubstitutionParameterExpression node = (SubstitutionParameterExpression) expr;
            if (!(node.isSatisfied()))
            {
                throw new EPException("Substitution parameter value for index " + node.getIndex() + " not set, please provide a value for this parameter");
            }
            return new ExprConstantNode(node.getConstant());
        }
        else if (expr instanceof PlugInProjectionExpression)
        {
            PlugInProjectionExpression node = (PlugInProjectionExpression) expr;
            try
            {
                AggregationSupport aggregation = mapContext.getEngineImportService().resolveAggregation(node.getFunctionName());
                return new ExprPlugInAggFunctionNode(node.isDistinct(), aggregation, node.getFunctionName());
            }
            catch (EngineImportUndefinedException e)
            {
                throw new EPException("Error resolving aggregation: " + e.getMessage(), e);
            }
            catch (EngineImportException e)
            {
                throw new EPException("Error resolving aggregation: " + e.getMessage(), e);
            }
        }
        else if (expr instanceof OrderedObjectParamExpression)
        {
            OrderedObjectParamExpression order = (OrderedObjectParamExpression) expr;
            return new ExprOrderedExpr(order.isDescending());
        }
        else if (expr instanceof CrontabFrequencyExpression)
        {
            return new ExprNumberSetFrequency();
        }
        else if (expr instanceof CrontabRangeExpression)
        {
            return new ExprNumberSetRange();
        }
        else if (expr instanceof CrontabParameterSetExpression)
        {
            return new ExprNumberSetList();
        }
        else if (expr instanceof CrontabParameterExpression)
        {
            CrontabParameterExpression cronParam = (CrontabParameterExpression) expr;
            if (cronParam.getType() == CrontabParameterExpression.ScheduleItemType.WILDCARD)
            {
                return new ExprNumberSetWildcard();
            }
            CronOperatorEnum operator;
            if (cronParam.getType() == CrontabParameterExpression.ScheduleItemType.LASTDAY)
            {
                operator = CronOperatorEnum.LASTDAY;
            }
            else if (cronParam.getType() == CrontabParameterExpression.ScheduleItemType.WEEKDAY)
            {
                operator = CronOperatorEnum.WEEKDAY;
            }
            else if (cronParam.getType() == CrontabParameterExpression.ScheduleItemType.LASTWEEKDAY)
            {
                operator = CronOperatorEnum.LASTWEEKDAY;
            }
            else {
                throw new IllegalArgumentException("Cron parameter not recognized: " + cronParam.getType());
            }
            return new ExprNumberSetCronParam(operator);
        }
        throw new IllegalArgumentException("Could not map expression node of type " + expr.getClass().getSimpleName());
    }

    private static List<Expression> unmapExpressionDeep(List<ExprNode> expressions, StatementSpecUnMapContext unmapContext)
    {
        List<Expression> result = new ArrayList<Expression>();
        for (ExprNode expr : expressions)
        {
            result.add(unmapExpressionDeep(expr, unmapContext));
        }
        return result;
    }

    private static Expression unmapExpressionFlat(ExprNode expr, StatementSpecUnMapContext unmapContext)
    {
        if (expr instanceof ExprMathNode)
        {
            ExprMathNode math = (ExprMathNode) expr;
            return new ArithmaticExpression(math.getMathArithTypeEnum().getExpressionText());
        }
        else if (expr instanceof ExprIdentNode)
        {
            ExprIdentNode prop = (ExprIdentNode) expr;
            String propertyName = prop.getUnresolvedPropertyName();
            if (prop.getStreamOrPropertyName() != null)
            {
                propertyName = prop.getStreamOrPropertyName() + "." + prop.getUnresolvedPropertyName();
            }
            return new PropertyValueExpression(propertyName);
        }
        else if (expr instanceof ExprVariableNode)
        {
            ExprVariableNode prop = (ExprVariableNode) expr;
            String propertyName = prop.getVariableName();
            return new PropertyValueExpression(propertyName);
        }
        else if (expr instanceof ExprEqualsNode)
        {
            ExprEqualsNode equals = (ExprEqualsNode) expr;
            String operator = "=";
            if (equals.isNotEquals())
            {
                operator = "!=";
            }
            return new RelationalOpExpression(operator);
        }
        else if (expr instanceof ExprRelationalOpNode)
        {
            ExprRelationalOpNode rel = (ExprRelationalOpNode) expr;
            return new RelationalOpExpression(rel.getRelationalOpEnum().getExpressionText());
        }
        else if (expr instanceof ExprAndNode)
        {
            return new Conjunction();
        }
        else if (expr instanceof ExprOrNode)
        {
            return new Disjunction();
        }
        else if (expr instanceof ExprConstantNode)
        {
            ExprConstantNode constNode = (ExprConstantNode) expr;
            return new ConstantExpression(constNode.getValue());
        }
        else if (expr instanceof ExprConcatNode)
        {
            return new ConcatExpression();
        }
        else if (expr instanceof ExprSubselectRowNode)
        {
            ExprSubselectRowNode sub = (ExprSubselectRowNode) expr;
            StatementSpecUnMapResult unmapped = unmap(sub.getStatementSpecRaw());
            unmapContext.addAll(unmapped.getIndexedParams());
            return new SubqueryExpression(unmapped.getObjectModel());
        }
        else if (expr instanceof ExprSubselectInNode)
        {
            ExprSubselectInNode sub = (ExprSubselectInNode) expr;
            StatementSpecUnMapResult unmapped = unmap(sub.getStatementSpecRaw());
            unmapContext.addAll(unmapped.getIndexedParams());
            return new SubqueryInExpression(unmapped.getObjectModel(), sub.isNotIn());
        }
        else if (expr instanceof ExprSubselectExistsNode)
        {
            ExprSubselectExistsNode sub = (ExprSubselectExistsNode) expr;
            StatementSpecUnMapResult unmapped = unmap(sub.getStatementSpecRaw());
            unmapContext.addAll(unmapped.getIndexedParams());
            return new SubqueryExistsExpression(unmapped.getObjectModel());
        }
        else if (expr instanceof ExprSubselectAllSomeAnyNode)
        {
            ExprSubselectAllSomeAnyNode sub = (ExprSubselectAllSomeAnyNode) expr;
            StatementSpecUnMapResult unmapped = unmap(sub.getStatementSpecRaw());
            unmapContext.addAll(unmapped.getIndexedParams());
            String operator = "=";
            if (sub.isNot())
            {
                operator = "!=";
            }
            if (sub.getRelationalOp() != null)
            {
                operator = sub.getRelationalOp().getExpressionText();
            }
            return new SubqueryQualifiedExpression(unmapped.getObjectModel(), operator, sub.isAll());
        }
        else if (expr instanceof ExprCountNode)
        {
            ExprCountNode sub = (ExprCountNode) expr;
            if (sub.getChildNodes().size() == 0)
            {
                return new CountStarProjectionExpression();
            }
            else
            {
                return new CountProjectionExpression(sub.isDistinct());
            }
        }
        else if (expr instanceof ExprAvgNode)
        {
            ExprAvgNode sub = (ExprAvgNode) expr;
            return new AvgProjectionExpression(sub.isDistinct());
        }
        else if (expr instanceof ExprSumNode)
        {
            ExprSumNode sub = (ExprSumNode) expr;
            return new SumProjectionExpression(sub.isDistinct());
        }
        else if (expr instanceof ExprBetweenNode)
        {
            ExprBetweenNode between = (ExprBetweenNode) expr;
            return new BetweenExpression(between.isLowEndpointIncluded(), between.isHighEndpointIncluded(), between.isNotBetween());
        }
        else if (expr instanceof ExprPriorNode)
        {
            return new PriorExpression();
        }
        else if (expr instanceof ExprPreviousNode)
        {
            return new PreviousExpression();
        }
        else if (expr instanceof ExprStaticMethodNode)
        {
            ExprStaticMethodNode node = (ExprStaticMethodNode) expr;
            return new StaticMethodExpression(node.getClassName(), node.getMethodName());
        }
        else if (expr instanceof ExprMinMaxAggrNode)
        {
            ExprMinMaxAggrNode node = (ExprMinMaxAggrNode) expr;
            if (node.getMinMaxTypeEnum() == MinMaxTypeEnum.MIN)
            {
                return new MinProjectionExpression(node.isDistinct());
            }
            else
            {
                return new MaxProjectionExpression(node.isDistinct());
            }
        }
        else if (expr instanceof ExprNotNode)
        {
            return new NotExpression();
        }
        else if (expr instanceof ExprInNode)
        {
            ExprInNode in = (ExprInNode) expr;
            return new InExpression(in.isNotIn());
        }
        else if (expr instanceof ExprCoalesceNode)
        {
            return new CoalesceExpression();
        }
        else if (expr instanceof ExprCaseNode)
        {
            ExprCaseNode mycase = (ExprCaseNode) expr;
            if (mycase.isCase2())
            {
                return new CaseSwitchExpression();
            }
            else
            {
                return new CaseWhenThenExpression();
            }
        }
        else if (expr instanceof ExprMinMaxRowNode)
        {
            ExprMinMaxRowNode node = (ExprMinMaxRowNode) expr;
            if (node.getMinMaxTypeEnum() == MinMaxTypeEnum.MAX)
            {
                return new MaxRowExpression();
            }
            return new MinRowExpression();
        }
        else if (expr instanceof ExprBitWiseNode)
        {
            ExprBitWiseNode node = (ExprBitWiseNode) expr;
            return new BitwiseOpExpression(node.getBitWiseOpEnum());
        }
        else if (expr instanceof ExprArrayNode)
        {
            return new ArrayExpression();
        }
        else if (expr instanceof ExprLikeNode)
        {
            ExprLikeNode exprLikeNode = (ExprLikeNode) expr;
            return new LikeExpression(exprLikeNode.isNot());
        }
        else if (expr instanceof ExprRegexpNode)
        {
            ExprRegexpNode exprRegexNode = (ExprRegexpNode) expr;
            return new RegExpExpression(exprRegexNode.isNot());
        }
        else if (expr instanceof ExprMedianNode)
        {
            ExprMedianNode median = (ExprMedianNode) expr;
            return new MedianProjectionExpression(median.isDistinct());
        }
        else if (expr instanceof ExprAvedevNode)
        {
            ExprAvedevNode node = (ExprAvedevNode) expr;
            return new AvedevProjectionExpression(node.isDistinct());
        }
        else if (expr instanceof ExprStddevNode)
        {
            ExprStddevNode node = (ExprStddevNode) expr;
            return new StddevProjectionExpression(node.isDistinct());
        }
        else if (expr instanceof ExprPlugInAggFunctionNode)
        {
            ExprPlugInAggFunctionNode node = (ExprPlugInAggFunctionNode) expr;
            return new PlugInProjectionExpression(node.getAggregationFunctionName(), node.isDistinct());
        }
        else if (expr instanceof ExprInstanceofNode)
        {
            ExprInstanceofNode node = (ExprInstanceofNode) expr;
            return new InstanceOfExpression(node.getClassIdentifiers());
        }
        else if (expr instanceof ExprCastNode)
        {
            ExprCastNode node = (ExprCastNode) expr;
            return new CastExpression(node.getClassIdentifier());
        }
        else if (expr instanceof ExprPropertyExistsNode)
        {
            return new PropertyExistsExpression();
        }
        else if (expr instanceof ExprTimestampNode)
        {
            return new CurrentTimestampExpression();
        }
        else if (expr instanceof ExprSubstitutionNode)
        {
            ExprSubstitutionNode node = (ExprSubstitutionNode) expr;
            SubstitutionParameterExpression subParam = new SubstitutionParameterExpression(node.getIndex());
            unmapContext.add(node.getIndex(), subParam);
            return subParam;
        }
        else if (expr instanceof ExprTimePeriod)
        {
            ExprTimePeriod node = (ExprTimePeriod) expr;
            return new TimePeriodExpression(node.isHasDay(), node.isHasHour(), node.isHasMinute(), node.isHasSecond(), node.isHasMillisecond());
        }
        else if (expr instanceof ExprNumberSetWildcard)
        {
            return new CrontabParameterExpression(CrontabParameterExpression.ScheduleItemType.WILDCARD);
        }
        else if (expr instanceof ExprNumberSetFrequency)
        {
            return new CrontabFrequencyExpression();
        }
        else if (expr instanceof ExprNumberSetRange)
        {
            return new CrontabRangeExpression();
        }
        else if (expr instanceof ExprNumberSetList)
        {
            return new CrontabParameterSetExpression();
        }
        else if (expr instanceof ExprOrderedExpr)
        {
            ExprOrderedExpr order = (ExprOrderedExpr) expr;
            return new OrderedObjectParamExpression(order.isDescending());
        }
        else if (expr instanceof ExprEqualsAllAnyNode)
        {
            ExprEqualsAllAnyNode node = (ExprEqualsAllAnyNode) expr;
            String operator = node.isNot() ? "!=" : "=";
            return new CompareListExpression(node.isAll(), operator);
        }
        else if (expr instanceof ExprRelationalOpAllAnyNode)
        {
            ExprRelationalOpAllAnyNode node = (ExprRelationalOpAllAnyNode) expr;
            return new CompareListExpression(node.isAll(), node.getRelationalOpEnum().getExpressionText());
        }
        else if (expr instanceof ExprNumberSetCronParam)
        {
            ExprNumberSetCronParam cronParam = (ExprNumberSetCronParam) expr;
            CrontabParameterExpression.ScheduleItemType type = null;
            if (cronParam.getCronOperator() == CronOperatorEnum.LASTDAY)
            {
                type = CrontabParameterExpression.ScheduleItemType.LASTDAY;
            }
            else if (cronParam.getCronOperator() == CronOperatorEnum.LASTWEEKDAY)
            {
                type = CrontabParameterExpression.ScheduleItemType.LASTWEEKDAY;
            }
            else if (cronParam.getCronOperator() == CronOperatorEnum.WEEKDAY)
            {
                type = CrontabParameterExpression.ScheduleItemType.WEEKDAY;
            }
            else {
                throw new IllegalArgumentException("Cron parameter not recognized: " + cronParam.getCronOperator());
            }
            return new CrontabParameterExpression(type);
        }
        throw new IllegalArgumentException("Could not map expression node of type " + expr.getClass().getSimpleName());
    }

    private static void unmapExpressionRecursive(Expression parent, ExprNode expr, StatementSpecUnMapContext unmapContext)
    {
        for (ExprNode child : expr.getChildNodes())
        {
            Expression result = unmapExpressionFlat(child, unmapContext);
            parent.getChildren().add(result);
            unmapExpressionRecursive(result, child, unmapContext);
        }
    }

    private static void mapExpressionRecursive(ExprNode parent, Expression expr, StatementSpecMapContext mapContext)
    {
        for (Expression child : expr.getChildren())
        {
            ExprNode result = mapExpressionFlat(child, mapContext);
            parent.addChildNode(result);
            mapExpressionRecursive(result, child, mapContext);
        }
    }

    private static void mapFrom(FromClause fromClause, StatementSpecRaw raw, StatementSpecMapContext mapContext)
    {
        if (fromClause == null)
        {
            return;
        }

        for (Stream stream : fromClause.getStreams())
        {
            StreamSpecRaw spec;

            if (stream instanceof FilterStream)
            {
                FilterStream filterStream = (FilterStream) stream;
                FilterSpecRaw filterSpecRaw = mapFilter(filterStream.getFilter(), mapContext);
                StreamSpecOptions options = mapStreamOpts(filterStream);
                spec = new FilterStreamSpecRaw(filterSpecRaw, new ArrayList<ViewSpec>(), filterStream.getStreamName(), options);
            }
            else if (stream instanceof SQLStream)
            {
                SQLStream sqlStream = (SQLStream) stream;
                spec = new DBStatementStreamSpec(sqlStream.getStreamName(), new ArrayList<ViewSpec>(),
                        sqlStream.getDatabaseName(), sqlStream.getSqlWithSubsParams(), sqlStream.getOptionalMetadataSQL());
            }
            else if (stream instanceof PatternStream)
            {
                PatternStream patternStream = (PatternStream) stream;
                EvalNode child = mapPatternEvalDeep(patternStream.getExpression(), mapContext);
                StreamSpecOptions options = mapStreamOpts(patternStream);
                spec = new PatternStreamSpecRaw(child, new ArrayList<ViewSpec>(), patternStream.getStreamName(), options);
            }
            else if (stream instanceof MethodInvocationStream)
            {
                MethodInvocationStream methodStream = (MethodInvocationStream) stream;
                List<ExprNode> expressions = new ArrayList<ExprNode>();
                for (Expression expr : methodStream.getParameterExpressions())
                {
                    ExprNode exprNode = mapExpressionDeep(expr, mapContext);
                    expressions.add(exprNode);
                }

                spec = new MethodStreamSpec(methodStream.getStreamName(), new ArrayList<ViewSpec>(), "method",
                        methodStream.getClassName(), methodStream.getMethodName(), expressions);
            }
            else
            {
                throw new IllegalArgumentException("Could not map from stream " + stream + " to an internal representation");
            }

            raw.getStreamSpecs().add(spec);

            if (stream instanceof ProjectedStream)
            {
                ProjectedStream projectedStream = (ProjectedStream) stream;
                spec.getViewSpecs().addAll(mapViews(projectedStream.getViews(), mapContext));
            }
        }

        for (OuterJoinQualifier qualifier : fromClause.getOuterJoinQualifiers())
        {
            ExprIdentNode left = (ExprIdentNode) mapExpressionFlat(qualifier.getLeft(), mapContext);
            ExprIdentNode right = (ExprIdentNode) mapExpressionFlat(qualifier.getRight(), mapContext);

            ExprIdentNode[] additionalLeft = null;
            ExprIdentNode[] additionalRight = null;
            if (qualifier.getAdditionalProperties().size() != 0)
            {
                additionalLeft = new ExprIdentNode[qualifier.getAdditionalProperties().size()];
                additionalRight = new ExprIdentNode[qualifier.getAdditionalProperties().size()];
                int count = 0;
                for (Pair<PropertyValueExpression, PropertyValueExpression> pair : qualifier.getAdditionalProperties())
                {
                    additionalLeft[count] = (ExprIdentNode) mapExpressionFlat(pair.getFirst(), mapContext);
                    additionalRight[count] = (ExprIdentNode) mapExpressionFlat(pair.getSecond(), mapContext);
                    count++;
                }
            }
            raw.getOuterJoinDescList().add(new OuterJoinDesc(qualifier.getType(), left, right, additionalLeft, additionalRight));
        }
    }

    private static List<ViewSpec> mapViews(List<View> views, StatementSpecMapContext mapContext)
    {
        List<ViewSpec> viewSpecs = new ArrayList<ViewSpec>();
        for (View view : views)
        {
            List<ExprNode> viewExpressions = mapExpressionDeep(view.getParameters(), mapContext);
            viewSpecs.add(new ViewSpec(view.getNamespace(), view.getName(), viewExpressions));
        }
        return viewSpecs;
    }

    private static List<View> unmapViews(List<ViewSpec> viewSpecs, StatementSpecUnMapContext unmapContext)
    {
        List<View> views = new ArrayList<View>();
        for (ViewSpec viewSpec : viewSpecs)
        {
            List<Expression> viewExpressions = unmapExpressionDeep(viewSpec.getObjectParameters(), unmapContext);
            views.add(View.create(viewSpec.getObjectNamespace(), viewSpec.getObjectName(), viewExpressions));
        }
        return views;
    }

    private static EvalNode mapPatternEvalFlat(PatternExpr eval, StatementSpecMapContext mapContext)
    {
        if (eval == null)
        {
            throw new IllegalArgumentException("Null expression parameter");
        }
        if (eval instanceof PatternAndExpr)
        {
            return new EvalAndNode();
        }
        else if (eval instanceof PatternOrExpr)
        {
            return new EvalOrNode();
        }
        else if (eval instanceof PatternFollowedByExpr)
        {
            return new EvalFollowedByNode();
        }
        else if (eval instanceof PatternEveryExpr)
        {
            return new EvalEveryNode();
        }
        else if (eval instanceof PatternFilterExpr)
        {
            PatternFilterExpr filterExpr = (PatternFilterExpr) eval;
            FilterSpecRaw filterSpec = mapFilter(filterExpr.getFilter(), mapContext);
            return new EvalFilterNode(filterSpec, filterExpr.getTagName());
        }
        else if (eval instanceof PatternObserverExpr)
        {
            PatternObserverExpr observer = (PatternObserverExpr) eval;
            List<ExprNode> expressions = mapExpressionDeep(observer.getParameters(), mapContext);
            return new EvalObserverNode(new PatternObserverSpec(observer.getNamespace(), observer.getName(), expressions));
        }
        else if (eval instanceof PatternGuardExpr)
        {
            PatternGuardExpr guard = (PatternGuardExpr) eval;
            List<ExprNode> expressions = mapExpressionDeep(guard.getParameters(), mapContext);
            return new EvalGuardNode(new PatternGuardSpec(guard.getNamespace(), guard.getName(), expressions));
        }
        else if (eval instanceof PatternNotExpr)
        {
            return new EvalNotNode();
        }
        else if (eval instanceof PatternMatchUntilExpr)
        {
            PatternMatchUntilExpr until = (PatternMatchUntilExpr) eval;
            return new EvalMatchUntilNode(new EvalMatchUntilSpec(until.getLow(), until.getHigh()));
        }
        throw new IllegalArgumentException("Could not map pattern expression node of type " + eval.getClass().getSimpleName());
    }

    private static PatternExpr unmapPatternEvalFlat(EvalNode eval, StatementSpecUnMapContext unmapContext)
    {
        if (eval instanceof EvalAndNode)
        {
            return new PatternAndExpr();
        }
        else if (eval instanceof EvalOrNode)
        {
            return new PatternOrExpr();
        }
        else if (eval instanceof EvalFollowedByNode)
        {
            return new PatternFollowedByExpr();
        }
        else if (eval instanceof EvalEveryNode)
        {
            return new PatternEveryExpr();
        }
        else if (eval instanceof EvalNotNode)
        {
            return new PatternNotExpr();
        }
        else if (eval instanceof EvalFilterNode)
        {
            EvalFilterNode filterNode = (EvalFilterNode) eval;
            Filter filter = unmapFilter(filterNode.getRawFilterSpec(), unmapContext);
            return new PatternFilterExpr(filter, filterNode.getEventAsName());
        }
        else if (eval instanceof EvalObserverNode)
        {
            EvalObserverNode observerNode = (EvalObserverNode) eval;
            List<Expression> expressions = unmapExpressionDeep(observerNode.getPatternObserverSpec().getObjectParameters(), unmapContext);
            return new PatternObserverExpr(observerNode.getPatternObserverSpec().getObjectNamespace(),
                    observerNode.getPatternObserverSpec().getObjectName(), expressions);
        }
        else if (eval instanceof EvalGuardNode)
        {
            EvalGuardNode guardNode = (EvalGuardNode) eval;
            List<Expression> expressions = unmapExpressionDeep(guardNode.getPatternGuardSpec().getObjectParameters(), unmapContext);
            return new PatternGuardExpr(guardNode.getPatternGuardSpec().getObjectNamespace(),
                    guardNode.getPatternGuardSpec().getObjectName(), expressions);
        }
        else if (eval instanceof EvalMatchUntilNode)
        {
            EvalMatchUntilNode matchUntilNode = (EvalMatchUntilNode) eval;
            return new PatternMatchUntilExpr(matchUntilNode.getSpec().getLowerBounds(), matchUntilNode.getSpec().getUpperBounds());
        }
        throw new IllegalArgumentException("Could not map pattern expression node of type " + eval.getClass().getSimpleName());
    }

    private static void unmapPatternEvalRecursive(PatternExpr parent, EvalNode eval, StatementSpecUnMapContext unmapContext)
    {
        for (EvalNode child : eval.getChildNodes())
        {
            PatternExpr result = unmapPatternEvalFlat(child, unmapContext);
            parent.getChildren().add(result);
            unmapPatternEvalRecursive(result, child, unmapContext);
        }
    }

    private static void mapPatternEvalRecursive(EvalNode parent, PatternExpr expr, StatementSpecMapContext mapContext)
    {
        for (PatternExpr child : expr.getChildren())
        {
            EvalNode result = mapPatternEvalFlat(child, mapContext);
            parent.addChildNode(result);
            mapPatternEvalRecursive(result, child, mapContext);
        }
    }

    private static PatternExpr unmapPatternEvalDeep(EvalNode exprNode, StatementSpecUnMapContext unmapContext)
    {
        PatternExpr parent = unmapPatternEvalFlat(exprNode, unmapContext);
        unmapPatternEvalRecursive(parent, exprNode, unmapContext);
        return parent;
    }

    private static EvalNode mapPatternEvalDeep(PatternExpr expr, StatementSpecMapContext mapContext)
    {
        EvalNode parent = mapPatternEvalFlat(expr, mapContext);
        mapPatternEvalRecursive(parent, expr, mapContext);
        return parent;
    }

    private static FilterSpecRaw mapFilter(Filter filter, StatementSpecMapContext mapContext)
    {
        List<ExprNode> expr = new ArrayList<ExprNode>();
        if (filter.getFilter() != null)
        {
            ExprNode exprNode = mapExpressionDeep(filter.getFilter(), mapContext);
            expr.add(exprNode);
        }

        PropertyEvalSpec evalSpec = null;
        if (filter.getOptionalPropertySelects() != null)
        {
            evalSpec = new PropertyEvalSpec();
            for (ContainedEventSelect propertySelect : filter.getOptionalPropertySelects())
            {
                SelectClauseSpecRaw selectSpec = null;
                if (propertySelect.getSelectClause() != null)
                {
                    selectSpec = mapSelectRaw(propertySelect.getSelectClause(), mapContext);
                }

                ExprNode exprNodeWhere = null;
                if (propertySelect.getWhereClause() != null)
                {
                    exprNodeWhere = mapExpressionDeep(propertySelect.getWhereClause(), mapContext);
                }

                evalSpec.add(new PropertyEvalAtom(propertySelect.getPropertyName(), propertySelect.getPropertyAsName(), selectSpec, exprNodeWhere));
            }
        }

        return new FilterSpecRaw(filter.getEventTypeName(), expr, evalSpec);
    }

    private static Filter unmapFilter(FilterSpecRaw filter, StatementSpecUnMapContext unmapContext)
    {
        Expression expr = null;
        if (filter.getFilterExpressions().size() > 1)
        {
            expr = new Conjunction();
            for (ExprNode exprNode : filter.getFilterExpressions())
            {
                Expression expression = unmapExpressionDeep(exprNode, unmapContext);
                expr.getChildren().add(expression);
            }
        }
        else if (filter.getFilterExpressions().size() == 1)
        {
            expr = unmapExpressionDeep(filter.getFilterExpressions().get(0), unmapContext);
        }

        Filter filterDef = new Filter(filter.getEventTypeName(), expr);

        if (filter.getOptionalPropertyEvalSpec() != null)
        {
            List<ContainedEventSelect> propertySelects = new ArrayList<ContainedEventSelect>();
            for (PropertyEvalAtom atom : filter.getOptionalPropertyEvalSpec().getAtoms())
            {
                SelectClause selectClause = null;
                if (atom.getOptionalSelectClause() != null)
                {
                    selectClause = unmapSelectInternal(atom.getOptionalSelectClause(), SelectClauseStreamSelectorEnum.ISTREAM_ONLY, unmapContext);
                }

                Expression filterExpression = null;
                if (atom.getOptionalWhereClause() != null)
                {
                    filterExpression = unmapExpressionDeep(atom.getOptionalWhereClause(), unmapContext);
                }

                propertySelects.add(new ContainedEventSelect(atom.getPropertyName(), atom.getOptionalAsName(), selectClause, filterExpression));
            }
            filterDef.setOptionalPropertySelects(propertySelects);
        }
        return filterDef;
    }
}

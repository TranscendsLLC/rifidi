/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.spec;

import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprSubselectNode;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Specification object representing a complete EPL statement including all EPL constructs.
 */
public class StatementSpecCompiled
{
    private final OnTriggerDesc onTriggerDesc;
    private final CreateWindowDesc createWindowDesc;
    private final CreateVariableDesc createVariableDesc;
    private final InsertIntoDesc insertIntoDesc;
    private SelectClauseStreamSelectorEnum selectStreamDirEnum;
    private final SelectClauseSpecCompiled selectClauseSpec;
    private final List<StreamSpecCompiled> streamSpecs;
    private final List<OuterJoinDesc> outerJoinDescList;
    private ExprNode filterExprRootNode;
    private final List<ExprNode> groupByExpressions;
    private final ExprNode havingExprRootNode;
    private final OutputLimitSpec outputLimitSpec;
    private final List<OrderByItem> orderByList;
    private final List<ExprSubselectNode> subSelectExpressions;
    private final boolean hasVariables;
    private final RowLimitSpec rowLimitSpec;
    private final Set<String> eventTypeReferences;

    /**
     * Ctor.
     * @param insertIntoDesc insert into def
     * @param selectClauseStreamSelectorEnum stream selection
     * @param selectClauseSpec select clause
     * @param streamSpecs specs for streams
     * @param outerJoinDescList outer join def
     * @param filterExprRootNode where filter expr nodes
     * @param groupByExpressions group by expression
     * @param havingExprRootNode having expression
     * @param outputLimitSpec output limit
     * @param orderByList order by
     * @param subSelectExpressions list of subqueries
     * @param onTriggerDesc describes on-delete statements
     * @param createWindowDesc describes create-window statements
     * @param createVariableDesc describes create-variable statements
     * @param hasVariables indicator whether the statement uses variables
     * @param rowLimitSpec row limit specification, or null if none supplied
     * @param eventTypeReferences event type names statically determined
     */
    public StatementSpecCompiled(OnTriggerDesc onTriggerDesc,
                                 CreateWindowDesc createWindowDesc,
                                 CreateVariableDesc createVariableDesc,
                                 InsertIntoDesc insertIntoDesc,
                                 SelectClauseStreamSelectorEnum selectClauseStreamSelectorEnum,
                                 SelectClauseSpecCompiled selectClauseSpec,
                                 List<StreamSpecCompiled> streamSpecs,
                                 List<OuterJoinDesc> outerJoinDescList,
                                 ExprNode filterExprRootNode,
                                 List<ExprNode> groupByExpressions,
                                 ExprNode havingExprRootNode,
                                 OutputLimitSpec outputLimitSpec,
                                 List<OrderByItem> orderByList,
                                 List<ExprSubselectNode> subSelectExpressions,
                                 boolean hasVariables,
                                 RowLimitSpec rowLimitSpec,
                                 Set<String> eventTypeReferences)
    {
        this.onTriggerDesc = onTriggerDesc;
        this.createWindowDesc = createWindowDesc;
        this.createVariableDesc = createVariableDesc;
        this.insertIntoDesc = insertIntoDesc;
        this.selectStreamDirEnum = selectClauseStreamSelectorEnum;
        this.selectClauseSpec = selectClauseSpec;
        this.streamSpecs = streamSpecs;
        this.outerJoinDescList = outerJoinDescList;
        this.filterExprRootNode = filterExprRootNode;
        this.groupByExpressions = groupByExpressions;
        this.havingExprRootNode = havingExprRootNode;
        this.outputLimitSpec = outputLimitSpec;
        this.orderByList = orderByList;
        this.subSelectExpressions = subSelectExpressions;
        this.hasVariables = hasVariables;
        this.rowLimitSpec = rowLimitSpec;
        this.eventTypeReferences = eventTypeReferences;
    }

    /**
     * Ctor.
     */
    public StatementSpecCompiled()
    {
        onTriggerDesc = null;
        createWindowDesc = null;
        createVariableDesc = null;
        insertIntoDesc = null;
        selectStreamDirEnum = SelectClauseStreamSelectorEnum.RSTREAM_ISTREAM_BOTH;
        selectClauseSpec = new SelectClauseSpecCompiled();
        streamSpecs = new ArrayList<StreamSpecCompiled>();
        outerJoinDescList = new ArrayList<OuterJoinDesc>();
        filterExprRootNode = null;
        groupByExpressions = new ArrayList<ExprNode>();
        havingExprRootNode = null;
        outputLimitSpec = null;
        orderByList = new ArrayList<OrderByItem>();
        subSelectExpressions = new ArrayList<ExprSubselectNode>();
        hasVariables = false;
        rowLimitSpec = null;
        eventTypeReferences = new HashSet<String>();
    }

    /**
     * Returns the specification for an create-window statement.
     * @return create-window spec, or null if not such a statement
     */
    public CreateWindowDesc getCreateWindowDesc()
    {
        return createWindowDesc;
    }

    /**
     * Returns the create-variable statement descriptor.
     * @return create-variable spec
     */
    public CreateVariableDesc getCreateVariableDesc()
    {
        return createVariableDesc;
    }

    /**
     * Returns the FROM-clause stream definitions.
     * @return list of stream specifications
     */
    public List<StreamSpecCompiled> getStreamSpecs()
    {
        return streamSpecs;
    }

    /**
     * Returns SELECT-clause list of expressions.
     * @return list of expressions and optional name
     */
    public SelectClauseSpecCompiled getSelectClauseSpec()
    {
        return selectClauseSpec;
    }

    /**
     * Returns the WHERE-clause root node of filter expression.
     * @return filter expression root node
     */
    public ExprNode getFilterRootNode()
    {
        return filterExprRootNode;
    }

    /**
     * Returns the LEFT/RIGHT/FULL OUTER JOIN-type and property name descriptor, if applicable. Returns null if regular join.
     * @return outer join type, stream names and property names
     */
    public List<OuterJoinDesc> getOuterJoinDescList()
    {
        return outerJoinDescList;
    }

    /**
     * Returns list of group-by expressions.
     * @return group-by expression nodes as specified in group-by clause
     */
    public List<ExprNode> getGroupByExpressions()
    {
        return groupByExpressions;
    }

    /**
     * Returns expression root node representing the having-clause, if present, or null if no having clause was supplied.
     * @return having-clause expression top node
     */
    public ExprNode getHavingExprRootNode()
    {
        return havingExprRootNode;
    }

    /**
     * Returns the output limit definition, if any.
     * @return output limit spec
     */
    public OutputLimitSpec getOutputLimitSpec()
    {
        return outputLimitSpec;
    }

    /**
     * Return a descriptor with the insert-into event name and optional list of columns.
     * @return insert into specification
     */
    public InsertIntoDesc getInsertIntoDesc()
    {
        return insertIntoDesc;
    }

    /**
     * Returns the list of order-by expression as specified in the ORDER BY clause.
     * @return Returns the orderByList.
     */
    public List<OrderByItem> getOrderByList() {
        return orderByList;
    }

    /**
     * Returns the stream selector (rstream/istream).
     * @return stream selector
     */
    public SelectClauseStreamSelectorEnum getSelectStreamSelectorEnum()
    {
        return selectStreamDirEnum;
    }

    /**
     * Set the where clause filter node.
     * @param optionalFilterNode is the where-clause filter node
     */
    public void setFilterExprRootNode(ExprNode optionalFilterNode)
    {
        filterExprRootNode = optionalFilterNode;
    }

    /**
     * Returns the list of lookup expression nodes.
     * @return lookup nodes
     */
    public List<ExprSubselectNode> getSubSelectExpressions()
    {
        return subSelectExpressions;
    }

    /**
     * Returns the specification for an on-delete or on-select statement.
     * @return on-trigger spec, or null if not such a statement
     */
    public OnTriggerDesc getOnTriggerDesc()
    {
        return onTriggerDesc;
    }

    /**
     * Returns true to indicate the statement has vaiables.
     * @return true for statements that use variables
     */
    public boolean isHasVariables()
    {
        return hasVariables;
    }

    /**
     * Sets the stream selection.
     * @param selectStreamDirEnum stream selection
     */
    public void setSelectStreamDirEnum(SelectClauseStreamSelectorEnum selectStreamDirEnum) {
        this.selectStreamDirEnum = selectStreamDirEnum;
    }

    /**
     * Returns the row limit specification, or null if none supplied.
     * @return row limit spec if any
     */
    public RowLimitSpec getRowLimitSpec()
    {
        return rowLimitSpec;
    }

    /**
     * Returns the event type name in used by the statement.
     * @return set of event type name
     */
    public Set<String> getEventTypeReferences()
    {
        return eventTypeReferences;
    }
}

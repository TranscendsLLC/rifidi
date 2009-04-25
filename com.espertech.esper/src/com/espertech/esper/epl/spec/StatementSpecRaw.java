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
import com.espertech.esper.util.MetaDefItem;

import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;

/**
 * Specification object representing a complete EPL statement including all EPL constructs.
 */
public class StatementSpecRaw implements MetaDefItem, Serializable
{
    private OnTriggerDesc onTriggerDesc;
    private CreateWindowDesc createWindowDesc;
    private CreateVariableDesc createVariableDesc;
    private InsertIntoDesc insertIntoDesc;
    private SelectClauseStreamSelectorEnum selectStreamDirEnum;
    private SelectClauseSpecRaw selectClauseSpec = new SelectClauseSpecRaw();
    private List<StreamSpecRaw> streamSpecs = new LinkedList<StreamSpecRaw>();
    private List<OuterJoinDesc> outerJoinDescList = new LinkedList<OuterJoinDesc>();
    private ExprNode filterExprRootNode;
    private List<ExprNode> groupByExpressions = new LinkedList<ExprNode>();
    private ExprNode havingExprRootNode;
    private OutputLimitSpec outputLimitSpec;
    private RowLimitSpec rowLimitSpec;
    private List<OrderByItem> orderByList = new LinkedList<OrderByItem>();
    private boolean existsSubstitutionParameters;
    private boolean hasVariables;
    private static final long serialVersionUID = 5390766716794133693L;

    /**
     * Ctor.
     * @param defaultStreamSelector stream selection for the statement
     */
    public StatementSpecRaw(SelectClauseStreamSelectorEnum defaultStreamSelector)
    {
        selectStreamDirEnum = defaultStreamSelector;
    }

    /**
     * Returns the FROM-clause stream definitions.
     * @return list of stream specifications
     */
    public List<StreamSpecRaw> getStreamSpecs()
    {
        return streamSpecs;
    }

    /**
     * Returns SELECT-clause list of expressions.
     * @return list of expressions and optional name
     */
    public SelectClauseSpecRaw getSelectClauseSpec()
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
     * Sets the output limiting definition.
     * @param outputLimitSpec defines the rules for output limiting
     */
    public void setOutputLimitSpec(OutputLimitSpec outputLimitSpec)
    {
        this.outputLimitSpec = outputLimitSpec;
    }

    /**
     * Sets the where clause filter expression node.
     * @param filterExprRootNode the where clause expression
     */
    public void setFilterRootNode(ExprNode filterExprRootNode)
    {
        this.filterExprRootNode = filterExprRootNode;
    }

    /**
     * Sets the having-clause filter expression node.
     * @param havingExprRootNode the having-clause expression
     */
    public void setHavingExprRootNode(ExprNode havingExprRootNode)
    {
        this.havingExprRootNode = havingExprRootNode;
    }

    /**
     * Sets the definition for any insert-into clause.
     * @param insertIntoDesc is the descriptor for insert-into rules
     */
    public void setInsertIntoDesc(InsertIntoDesc insertIntoDesc)
    {
        this.insertIntoDesc = insertIntoDesc;
    }

    /**
     * Sets the stream selector (rstream/istream/both etc).
     * @param selectStreamDirEnum to be set
     */
    public void setSelectStreamDirEnum(SelectClauseStreamSelectorEnum selectStreamDirEnum)
    {
        this.selectStreamDirEnum = selectStreamDirEnum;
    }

    /**
     * Sets the select clause.
     * @param selectClauseSpec is the new select clause specification
     */
    public void setSelectClauseSpec(SelectClauseSpecRaw selectClauseSpec)
    {
        this.selectClauseSpec = selectClauseSpec;
    }

    /**
     * Returns true if there are one or more substitution parameters in the statement of contained-within lookup statements
     * @return true if parameters exists
     */
    public boolean isExistsSubstitutionParameters()
    {
        return existsSubstitutionParameters;
    }

    /**
     * Sets the substitution parameters.
     * @param existsSubstitutionParameters true to indicate there are parameters
     */
    public void setExistsSubstitutionParameters(boolean existsSubstitutionParameters)
    {
        this.existsSubstitutionParameters = existsSubstitutionParameters;
    }

    /**
     * Returns the create-window specification.
     * @return descriptor for creating a named window
     */
    public CreateWindowDesc getCreateWindowDesc()
    {
        return createWindowDesc;
    }

    /**
     * Sets the create-window specification.
     * @param createWindowDesc descriptor for creating a named window
     */
    public void setCreateWindowDesc(CreateWindowDesc createWindowDesc)
    {
        this.createWindowDesc = createWindowDesc;
    }

    /**
     * Returns the on-delete statement specification.
     * @return descriptor for creating a an on-delete statement
     */
    public OnTriggerDesc getOnTriggerDesc()
    {
        return onTriggerDesc;
    }

    /**
     * Sets the on-delete statement specification.
     * @param onTriggerDesc descriptor for creating an on-delete statement
     */
    public void setOnTriggerDesc(OnTriggerDesc onTriggerDesc)
    {
        this.onTriggerDesc = onTriggerDesc;
    }

    /**
     * Gets the where clause.
     * @return where clause or null if none
     */
    public ExprNode getFilterExprRootNode()
    {
        return filterExprRootNode;
    }

    /**
     * Sets the where clause or null if none
     * @param filterExprRootNode where clause expression
     */
    public void setFilterExprRootNode(ExprNode filterExprRootNode)
    {
        this.filterExprRootNode = filterExprRootNode;
    }

    /**
     * Returns true if a statement (or subquery sub-statements) use variables.
     * @return indicator if variables are used
     */
    public boolean isHasVariables()
    {
        return hasVariables;
    }

    /**
     * Sets the flag indicating the statement uses variables.
     * @param hasVariables true if variables are used
     */
    public void setHasVariables(boolean hasVariables)
    {
        this.hasVariables = hasVariables;
    }

    /**
     * Returns the descriptor for create-variable statements.
     * @return create-variable info
     */
    public CreateVariableDesc getCreateVariableDesc()
    {
        return createVariableDesc;
    }

    /**
     * Sets the descriptor for create-variable statements, if this is one. 
     * @param createVariableDesc create-variable info
     */
    public void setCreateVariableDesc(CreateVariableDesc createVariableDesc)
    {
        this.createVariableDesc = createVariableDesc;
    }

    /**
     * Returns the row limit, or null if none.
     * @return row limit
     */
    public RowLimitSpec getRowLimitSpec()
    {
        return rowLimitSpec;
    }

    /**
     * Sets the row limit, or null if none.
     * @param rowLimitSpec row limit
     */
    public void setRowLimitSpec(RowLimitSpec rowLimitSpec)
    {
        this.rowLimitSpec = rowLimitSpec;
    }
}

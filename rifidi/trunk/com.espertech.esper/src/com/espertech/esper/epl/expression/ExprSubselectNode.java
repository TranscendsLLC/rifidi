/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.spec.StatementSpecCompiled;
import com.espertech.esper.epl.spec.StatementSpecRaw;
import com.espertech.esper.epl.lookup.TableLookupStrategy;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;

/**
 * Represents a subselect in an expression tree.
 */
public abstract class ExprSubselectNode extends ExprNode
{
    private static final Log log = LogFactory.getLog(ExprSubselectNode.class);

    /**
     * The validated select clause.
     */
    protected ExprNode selectClause;

    /**
     * The validate filter expression.
     */
    protected ExprNode filterExpr;

    /**
     * The event type generated for wildcard selects.
     */
    protected EventType rawEventType;

    private StatementSpecRaw statementSpecRaw;
    private StatementSpecCompiled statementSpecCompiled;
    private TableLookupStrategy strategy;
    private String selectAsName;

    /**
     * Evaluate the lookup expression returning an evaluation result object.
     * @param eventsPerStream is the events for each stream in a join
     * @param isNewData is true for new data, or false for old data
     * @param matchingEvents is filtered results from the table of stored lookup events
     * @return evaluation result
     */
    public abstract Object evaluate(EventBean[] eventsPerStream, boolean isNewData, Set<EventBean> matchingEvents);

    /**
     * Ctor.
     * @param statementSpec is the lookup statement spec from the parser, unvalidated
     */
    public ExprSubselectNode(StatementSpecRaw statementSpec)
    {
        this.statementSpecRaw = statementSpec;
    }

    public boolean isConstantResult()
    {
        if (selectClause != null)
        {
            return selectClause.isConstantResult();
        }
        return false;
    }

    /**
     * Supplies a compiled statement spec.
     * @param statementSpecCompiled compiled validated filters
     */
    public void setStatementSpecCompiled(StatementSpecCompiled statementSpecCompiled)
    {
        this.statementSpecCompiled = statementSpecCompiled;
    }

    /**
     * Returns the compiled statement spec.
     * @return compiled statement
     */
    public StatementSpecCompiled getStatementSpecCompiled()
    {
        return statementSpecCompiled;
    }

    /**
     * Sets the validate select clause
     * @param selectClause is the expression representing the select clause
     */
    public void setSelectClause(ExprNode selectClause)
    {
        this.selectClause = selectClause;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        Set<EventBean> matchingEvents = strategy.lookup(eventsPerStream);
        return evaluate(eventsPerStream, isNewData, matchingEvents);
    }

    /**
     * Returns the uncompiled statement spec.
     * @return statement spec uncompiled
     */
    public StatementSpecRaw getStatementSpecRaw()
    {
        return statementSpecRaw;
    }

    /**
     * Supplies the name of the select expression as-tag
     * @param selectAsName is the as-name
     */
    public void setSelectAsName(String selectAsName)
    {
        this.selectAsName = selectAsName;
    }

    /**
     * Sets the validated filter expression, or null if there is none.
     * @param filterExpr is the filter
     */
    public void setFilterExpr(ExprNode filterExpr)
    {
        this.filterExpr = filterExpr;
    }

    public String toExpressionString()
    {
        if (selectAsName != null)
        {
            return selectAsName;
        }
        if (selectClause == null)
        {
            return "*";
        }
        return selectClause.toExpressionString();
    }

    public boolean equalsNode(ExprNode node)
    {
        return false;   // 2 subselects are never equivalent
    }

    /**
     * Sets the strategy for boiling down the table of lookup events into a subset against which to run the filter.
     * @param strategy is the looking strategy (full table scan or indexed)
     */
    public void setStrategy(TableLookupStrategy strategy)
    {
        this.strategy = strategy;
    }

    /**
     * Sets the event type generated for wildcard selects.
     * @param rawEventType is the wildcard type (parent view)
     */
    public void setRawEventType(EventType rawEventType)
    {
        this.rawEventType = rawEventType;
    }

    /**
     * Returns the select clause or null if none.
     * @return clause
     */
    public ExprNode getSelectClause()
    {
        return selectClause;
    }

    /**
     * Returns filter expr or null if none.
     * @return filter
     */
    public ExprNode getFilterExpr()
    {
        return filterExpr;
    }

    /**
     * Returns the event type.
     * @return type
     */
    public EventType getRawEventType()
    {
        return rawEventType;
    }
}

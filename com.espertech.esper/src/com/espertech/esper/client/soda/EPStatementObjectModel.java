/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client.soda;

import com.espertech.esper.epl.spec.SubstitutionParameterExpression;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;

/**
 * Object model of an EPL statement.
 * <p>
 * Applications can create an object model by instantiating this class and then setting the various clauses.
 * When done, use {@link com.espertech.esper.client.EPAdministrator} to create a statement from the model.
 * <p>
 * Alternativly, a given textual EPL can be compiled into an object model representation via the compile method on
 * {@link com.espertech.esper.client.EPAdministrator}.
 * <p>
 * Use the toEPL method to generate a textual EPL from an object model.
 * <p>
 * Minimally, and EPL statement consists of the select-clause and the where-clause. These are represented by {@link SelectClause}
 * and {@link FromClause} respectively.
 * <p>
 * Here is a short example that create a simple EPL statement such as "select page, responseTime from PageLoad" :
 * <pre>
 * EPStatementObjectModel model = new EPStatementObjectModel();
 * model.setSelectClause(SelectClause.create("page", "responseTime"));
 * model.setFromClause(FromClause.create(FilterStream.create("PageLoad")));
 * </pre>
 * <p>
 * The select-clause and from-clause must be set for the statement object model to be useable by the
 * administrative API. All other clauses a optional.
 * <p>
 * Please see the documentation set for further examples.
 */
public class EPStatementObjectModel implements Serializable
{
    private static final long serialVersionUID = 0L;

    private CreateVariableClause createVariable;
    private CreateWindowClause createWindow;
    private OnClause onExpr;
    private InsertIntoClause insertInto;
    private SelectClause selectClause;
    private FromClause fromClause;
    private Expression whereClause;
    private GroupByClause groupByClause;
    private Expression havingClause;
    private OutputLimitClause outputLimitClause;
    private OrderByClause orderByClause;
    private RowLimitClause rowLimitClause;

    /**
     * Ctor.
     */
    public EPStatementObjectModel()
    {
    }

    /**
     * Specify an insert-into-clause.
     * @param insertInto specifies the insert-into-clause, or null to indicate that the clause is absent
     * @return model
     */
    public EPStatementObjectModel setInsertInto(InsertIntoClause insertInto)
    {
        this.insertInto = insertInto;
        return this;
    }

    /**
     * Return the insert-into-clause, or null to indicate that the clause is absent.
     * @return specification of the insert-into-clause, or null if none present
     */
    public InsertIntoClause getInsertInto()
    {
        return insertInto;
    }

    /**
     * Specify a select-clause.
     * @param selectClause specifies the select-clause, the select-clause cannot be null and must be set
     * @return model
     */
    public EPStatementObjectModel setSelectClause(SelectClause selectClause)
    {
        this.selectClause = selectClause;
        return this;
    }

    /**
     * Return the select-clause.
     * @return specification of the select-clause
     */
    public SelectClause getSelectClause()
    {
        return selectClause;
    }

    /**
     * Specify a from-clause.
     * @param fromClause specifies the from-clause, the from-clause cannot be null and must be set
     * @return model
     */
    public EPStatementObjectModel setFromClause(FromClause fromClause)
    {
        this.fromClause = fromClause;
        return this;
    }

    /**
     * Return the where-clause, or null to indicate that the clause is absent.
     * @return specification of the where-clause, or null if none present
     */
    public Expression getWhereClause()
    {
        return whereClause;
    }

    /**
     * Specify a where-clause.
     * @param whereClause specifies the where-clause, which is optional and can be null
     * @return model
     */
    public EPStatementObjectModel setWhereClause(Expression whereClause)
    {
        this.whereClause = whereClause;
        return this;
    }

    /**
     * Return the from-clause.
     * @return specification of the from-clause
     */
    public FromClause getFromClause()
    {
        return fromClause;
    }

    /**
     * Return the group-by-clause, or null to indicate that the clause is absent.
     * @return specification of the group-by-clause, or null if none present
     */
    public GroupByClause getGroupByClause()
    {
        return groupByClause;
    }

    /**
     * Specify a group-by-clause.
     * @param groupByClause specifies the group-by-clause, which is optional and can be null
     * @return model
     */
    public EPStatementObjectModel setGroupByClause(GroupByClause groupByClause)
    {
        this.groupByClause = groupByClause;
        return this;
    }

    /**
     * Return the having-clause, or null to indicate that the clause is absent.
     * @return specification of the having-clause, or null if none present
     */
    public Expression getHavingClause()
    {
        return havingClause;
    }

    /**
     * Specify a having-clause.
     * @param havingClause specifies the having-clause, which is optional and can be null
     * @return model
     */
    public EPStatementObjectModel setHavingClause(Expression havingClause)
    {
        this.havingClause = havingClause;
        return this;
    }

    /**
     * Return the order-by-clause, or null to indicate that the clause is absent.
     * @return specification of the order-by-clause, or null if none present
     */
    public OrderByClause getOrderByClause()
    {
        return orderByClause;
    }

    /**
     * Specify an order-by-clause.
     * @param orderByClause specifies the order-by-clause, which is optional and can be null
     * @return model
     */
    public EPStatementObjectModel setOrderByClause(OrderByClause orderByClause)
    {
        this.orderByClause = orderByClause;
        return this;
    }

    /**
     * Return the output-rate-limiting-clause, or null to indicate that the clause is absent.
     * @return specification of the output-rate-limiting-clause, or null if none present
     */
    public OutputLimitClause getOutputLimitClause()
    {
        return outputLimitClause;
    }

    /**
     * Specify an output-rate-limiting-clause.
     * @param outputLimitClause specifies the output-rate-limiting-clause, which is optional and can be null
     * @return model
     */
    public EPStatementObjectModel setOutputLimitClause(OutputLimitClause outputLimitClause)
    {
        this.outputLimitClause = outputLimitClause;
        return this;
    }

    /**
     * Renders the object model in it's EPL syntax textual representation.
     * @return EPL representing the statement object model
     * @throws IllegalStateException if required clauses do not exist
     */
    public String toEPL()
    {
        StringWriter writer = new StringWriter();

        if (createWindow != null)
        {
            createWindow.toEPL(writer);
            writer.write(" as ");
            if (selectClause == null)
            {
                throw new IllegalStateException("Select clause has not been defined");
            }
            selectClause.toEPL(writer);
            if (fromClause == null)
            {
                throw new IllegalStateException("From clause has not been defined");
            }
            fromClause.toEPL(writer);
            createWindow.toEPLInsertPart(writer);
            return writer.toString();
        }

        if (createVariable != null)
        {
            createVariable.toEPL(writer);
            return writer.toString();
        }

        if (onExpr != null)
        {
            writer.write("on ");
            fromClause.getStreams().get(0).toEPL(writer);

            if (onExpr instanceof OnDeleteClause)
            {
                writer.write(" delete from ");
                ((OnDeleteClause)onExpr).toEPL(writer);
            }
            else if (onExpr instanceof OnSelectClause)
            {
                writer.write(" ");
                if (insertInto != null)
                {
                    insertInto.toEPL(writer);
                }
                selectClause.toEPL(writer);
                writer.write(" from ");
                ((OnSelectClause)onExpr).toEPL(writer);
            }
            else
            {
                OnSetClause onSet = (OnSetClause) onExpr;
                onSet.toEPL(writer);
            }
        }
        else
        {
            if (selectClause == null)
            {
                throw new IllegalStateException("Select-clause has not been defined");
            }
            if (fromClause == null)
            {
                throw new IllegalStateException("From-clause has not been defined");
            }

            if (insertInto != null)
            {
                insertInto.toEPL(writer);
            }
            selectClause.toEPL(writer);
            fromClause.toEPL(writer);
        }

        if (whereClause != null)
        {
            writer.write(" where ");
            whereClause.toEPL(writer);
        }
        if (groupByClause != null)
        {
            writer.write(" group by ");
            groupByClause.toEPL(writer);
        }
        if (havingClause != null)
        {
            writer.write(" having ");
            havingClause.toEPL(writer);
        }
        if (outputLimitClause != null)
        {
            writer.write(" output ");
            outputLimitClause.toEPL(writer);
        }
        if (orderByClause != null)
        {
            writer.write(" order by ");
            orderByClause.toEPL(writer);
        }
        if (rowLimitClause != null)
        {
            writer.write(" limit ");
            rowLimitClause.toEPL(writer);
        }

        return writer.toString();
    }

    /**
     * Returns the create-window clause for creating named windows, or null if this statement does not
     * create a named window.
     * @return named window creation clause
     */
    public CreateWindowClause getCreateWindow()
    {
        return createWindow;
    }

    /**
     * Sets the create-window clause for creating named windows, or null if this statement does not
     * create a named window.
     * @param createWindow is the named window creation clause
     */
    public void setCreateWindow(CreateWindowClause createWindow)
    {
        this.createWindow = createWindow;
    }

    /**
     * Returns the on-delete clause for deleting from named windows, or null if this statement
     * does not delete from a named window
     * @return on delete clause
     */
    public OnClause getOnExpr()
    {
        return onExpr;
    }

    /**
     * Sets the on-delete or on-select clause for selecting or deleting from named windows, or null if this statement
     * does not on-select or on-delete from a named window
     * @param onExpr is the on-expression (on-select and on-delete) clause to set
     */
    public void setOnExpr(OnClause onExpr)
    {
        this.onExpr = onExpr;
    }

    /**
     * Returns the create-variable clause if this is a statement creating a variable, or null if not.
     * @return create-variable clause
     */
    public CreateVariableClause getCreateVariable()
    {
        return createVariable;
    }

    /**
     * Sets the create-variable clause if this is a statement creating a variable, or null if not.
     * @param createVariable create-variable clause
     */
    public void setCreateVariable(CreateVariableClause createVariable)
    {
        this.createVariable = createVariable;
    }

    /**
     * Returns the row limit specification, or null if none supplied.
     * @return row limit spec if any
     */
    public RowLimitClause getRowLimitClause()
    {
        return rowLimitClause;
    }

    /**
     * Sets the row limit specification, or null if none applicable.
     * @param rowLimitClause row limit spec if any
     */
    public void setRowLimitClause(RowLimitClause rowLimitClause)
    {
        this.rowLimitClause = rowLimitClause;
    }
}

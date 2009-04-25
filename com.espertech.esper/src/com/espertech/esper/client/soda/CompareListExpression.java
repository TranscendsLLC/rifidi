package com.espertech.esper.client.soda;

import java.io.StringWriter;

/**
 * Represents a list-compare of the format "expression operator all/any (expressions)".
 */
public class CompareListExpression extends ExpressionBase
{
    private String operator;
    private boolean isAll;

    /**
     * Ctor.
     * @param all is all, false if any
     * @param operator =, !=, <, >, <=, >=, <>
     */
    public CompareListExpression(boolean all, String operator)
    {
        isAll = all;
        this.operator = operator;
    }

    /**
     * Returns all flag, true for ALL and false for ANY.
     * @return indicator if all or any
     */
    public boolean isAll()
    {
        return isAll;
    }

    /**
     * Sets all flag, true for ALL and false for ANY.
     * @param all indicator if all or any
     */
    public void setAll(boolean all)
    {
        isAll = all;
    }

    /**
     * Returns the operator.
     * @return operator
     */
    public String getOperator()
    {
        return operator;
    }

    /**
     * Sets the operator.
     * @param operator to set (=, !=, <>, <, >, <=, >=)
     */
    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public void toEPL(StringWriter writer)
    {
        this.getChildren().get(0).toEPL(writer);
        writer.write(" ");
        writer.write(operator);
        if (isAll)
        {
            writer.write(" all (");
        }
        else
        {
            writer.write(" any (");
        }

        String delimiter = "";
        for (int i = 1; i < this.getChildren().size(); i++)
        {
            writer.write(delimiter);
            this.getChildren().get(i).toEPL(writer);
            delimiter = ", ";
        }
        writer.write(')');
    }
}

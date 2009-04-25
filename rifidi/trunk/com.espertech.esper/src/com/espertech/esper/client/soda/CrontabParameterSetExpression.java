package com.espertech.esper.client.soda;

import java.io.StringWriter;

/**
 * An expression for use in crontab provides all child expression as part of a parameter list.
 */
public class CrontabParameterSetExpression extends ExpressionBase
{
    /**
     * Ctor.
     */
    public CrontabParameterSetExpression()
    {
    }

    public void toEPL(StringWriter writer)
    {
        String delimiter = "";
        writer.write("[");
        for (Expression expr : this.getChildren())
        {
            writer.append(delimiter);
            expr.toEPL(writer);
            delimiter = ", ";
        }
        writer.write("]");
    }
}

package com.espertech.esper.client.soda;

import java.io.StringWriter;

/**
 * Parameter expression for use in crontab expressions and representing a range.
 */
public class CrontabRangeExpression extends ExpressionBase
{
    /**
     * Ctor.
     */
    public CrontabRangeExpression()
    {
    }

    /**
     * Ctor.
     * @param lowerBounds provides lower bounds
     * @param upperBounds provides upper bounds
     */
    public CrontabRangeExpression(Expression lowerBounds, Expression upperBounds)
    {
        this.getChildren().add(lowerBounds);
        this.getChildren().add(upperBounds);
    }

    public void toEPL(StringWriter writer)
    {
        this.getChildren().get(0).toEPL(writer);
        writer.append(":");
        this.getChildren().get(1).toEPL(writer);
    }
}

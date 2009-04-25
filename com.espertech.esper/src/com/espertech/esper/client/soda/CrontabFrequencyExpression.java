package com.espertech.esper.client.soda;

import java.io.StringWriter;

/**
 * Frequency expression for use in crontab expressions.
 */
public class CrontabFrequencyExpression extends ExpressionBase
{
    /**
     * Ctor.
     */
    public CrontabFrequencyExpression()
    {
    }

    /**
     * Ctor.
     * @param numericParameter the frequency value
     */
    public CrontabFrequencyExpression(Expression numericParameter)
    {
        this.getChildren().add(numericParameter);
    }

    public void toEPL(StringWriter writer)
    {
        writer.append("*/");
        this.getChildren().get(0).toEPL(writer);
    }
}

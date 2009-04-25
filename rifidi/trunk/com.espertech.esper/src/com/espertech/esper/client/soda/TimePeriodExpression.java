package com.espertech.esper.client.soda;

import java.io.StringWriter;

/**
 * Represent an expression 
 */
public class TimePeriodExpression extends ExpressionBase
{
    private boolean hasDays;
    private boolean hasHours;
    private boolean hasMinutes;
    private boolean hasSeconds;
    private boolean hasMilliseconds;

    /**
     * Ctor.
     * @param hasDays flag to indicate that a day-part expression exists
     * @param hasHours flag to indicate that a hour-part expression exists
     * @param hasMinutes flag to indicate that a minute-part expression exists
     * @param hasSeconds flag to indicate that a seconds-part expression exists
     * @param hasMilliseconds flag to indicate that a millisec-part expression exists
     */
    public TimePeriodExpression(boolean hasDays, boolean hasHours, boolean hasMinutes, boolean hasSeconds, boolean hasMilliseconds)
    {
        this.hasDays = hasDays;
        this.hasHours = hasHours;
        this.hasMinutes = hasMinutes;
        this.hasSeconds = hasSeconds;
        this.hasMilliseconds = hasMilliseconds;
    }

    /**
     * Ctor.
     * @param daysExpr expression returning days value, or null if no such part
     * @param hoursExpr expression returning hours value, or null if no such part
     * @param minutesExpr expression returning minutes value, or null if no such part
     * @param secondsExpr expression returning seconds value, or null if no such part
     * @param millisecondsExpr expression returning millisec value, or null if no such part
     */
    public TimePeriodExpression(Expression daysExpr, Expression hoursExpr, Expression minutesExpr, Expression secondsExpr, Expression millisecondsExpr)
    {
        if (daysExpr != null)
        {
            hasDays = true;
            this.addChild(daysExpr);
        }
        if (hoursExpr != null)
        {
            hasHours = true;
            this.addChild(hoursExpr);
        }
        if (minutesExpr != null)
        {
            hasMinutes = true;
            this.addChild(minutesExpr);
        }
        if (secondsExpr != null)
        {
            hasSeconds = true;
            this.addChild(secondsExpr);
        }
        if (millisecondsExpr != null)
        {
            hasMilliseconds = true;
            this.addChild(millisecondsExpr);
        }
    }

    /**
     * Returns true if a subexpression exists that is a day-part.
     * @return indicator for presence of part
     */
    public boolean isHasDays()
    {
        return hasDays;
    }

    /**
     * Set to true if a subexpression exists that is a day-part.
     * @param hasDays for presence of part
     */
    public void setHasDays(boolean hasDays)
    {
        this.hasDays = hasDays;
    }

    /**
     * Returns true if a subexpression exists that is a hour-part.
     * @return indicator for presence of part
     */
    public boolean isHasHours()
    {
        return hasHours;
    }

    /**
     * Set to true if a subexpression exists that is a hour-part.
     * @param hasHours for presence of part
     */
    public void setHasHours(boolean hasHours)
    {
        this.hasHours = hasHours;
    }

    /**
     * Returns true if a subexpression exists that is a minutes-part.
     * @return indicator for presence of part
     */
    public boolean isHasMinutes()
    {
        return hasMinutes;
    }

    /**
     * Set to true if a subexpression exists that is a minutes-part.
     * @param hasMinutes for presence of part
     */
    public void setHasMinutes(boolean hasMinutes)
    {
        this.hasMinutes = hasMinutes;
    }

    /**
     * Returns true if a subexpression exists that is a seconds-part.
     * @return indicator for presence of part
     */
    public boolean isHasSeconds()
    {
        return hasSeconds;
    }

    /**
     * Set to true if a subexpression exists that is a seconds-part.
     * @param hasSeconds for presence of part
     */
    public void setHasSeconds(boolean hasSeconds)
    {
        this.hasSeconds = hasSeconds;
    }

    /**
     * Returns true if a subexpression exists that is a milliseconds-part.
     * @return indicator for presence of part
     */
    public boolean isHasMilliseconds()
    {
        return hasMilliseconds;
    }

    /**
     * Set to true if a subexpression exists that is a msec-part.
     * @param hasMilliseconds for presence of part
     */
    public void setHasMilliseconds(boolean hasMilliseconds)
    {
        this.hasMilliseconds = hasMilliseconds;
    }

    public void toEPL(StringWriter writer)
    {
        String delimiter = "";
        int countExpr = 0;
        if (hasDays)
        {
            this.getChildren().get(countExpr).toEPL(writer);
            writer.append(" days");
            delimiter = " ";
            countExpr++;
        }
        if (hasHours)
        {
            writer.write(delimiter);
            this.getChildren().get(countExpr).toEPL(writer);
            writer.append(" hours");
            delimiter = " ";
            countExpr++;
        }
        if (hasMinutes)
        {
            writer.write(delimiter);
            this.getChildren().get(countExpr).toEPL(writer);
            writer.append(" minutes");
            delimiter = " ";
            countExpr++;
        }
        if (hasSeconds)
        {
            writer.write(delimiter);
            this.getChildren().get(countExpr).toEPL(writer);
            writer.append(" seconds");
            delimiter = " ";
            countExpr++;
        }
        if (hasMilliseconds)
        {
            writer.write(delimiter);
            this.getChildren().get(countExpr).toEPL(writer);
            writer.append(" milliseconds");
        }
    }
}

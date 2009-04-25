package com.espertech.esper.client.soda;

import java.io.StringWriter;

/**
 * Parameter expression such as last/lastweek/weekday/wildcard for use in crontab expressions.
 */
public class CrontabParameterExpression extends ExpressionBase
{
    private ScheduleItemType type;

    /**
     * Ctor.
     * @param type of crontab parameter
     */
    public CrontabParameterExpression(ScheduleItemType type)
    {
        this.type = type;
    }

    public void toEPL(StringWriter writer)
    {
        if (!this.getChildren().isEmpty())
        {
            this.getChildren().get(0).toEPL(writer);
            writer.append(' ');
        }
        writer.write(type.getSyntax());
    }

    /**
     * Returns crontab parameter type.
     * @return crontab parameter type
     */
    public ScheduleItemType getType()
    {
        return type;
    }

    /**
     * Sets the crontab parameter type.
     * @param type crontab parameter type
     */
    public void setType(ScheduleItemType type)
    {
        this.type = type;
    }

    /**
     * Type of schedule item.
     */
    public enum ScheduleItemType
    {
        /**
         * Wildcard means any value.
         */
        WILDCARD("*"),

        /**
         * Last day of week or month.
         */
        LASTDAY("last"),

        /**
         * Weekday (nearest to a date)
         */
        WEEKDAY("weekday"),

        /**
         * Last weekday in a month
         */
        LASTWEEKDAY("lastweekday");

        private String syntax;

        private ScheduleItemType(String s)
        {
            this.syntax = s;
        }

        /**
         * Returns the syntax string.
         * @return syntax
         */
        public String getSyntax()
        {
            return syntax;
        }
    }
}

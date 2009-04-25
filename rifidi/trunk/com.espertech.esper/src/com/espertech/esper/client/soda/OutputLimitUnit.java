/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client.soda;

/**
 * Unit for output rate limiting.
 */
public enum OutputLimitUnit
{
    /**
     * The time period unit.
     */
    TIME_PERIOD ("timeperiod"),

    /**
     * The number of events unit.
     */
    EVENTS ("events"),

    /**
     * The unit representing a when-expression.
     */
    WHEN_EXPRESSION ("when"),

    /**
     * The unit representing a crontab-at-expression.
     */
    CRONTAB_EXPRESSION ("crontab");

    private String text;

    private OutputLimitUnit(String text)
    {
        this.text = text;
    }

    /**
     * Returns the text for the unit.
     * @return unit text
     */
    public String getText()
    {
        return text;
    }
}

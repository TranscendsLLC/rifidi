/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view;

import java.util.Arrays;

/**
 * Enumerates the valid values for each view's public fields. The name of the field or property can be used
 * to obtain values from the view rather than using the hardcoded String value for the field.
 */
public enum ViewFieldEnum
{
    /**
     * Count.
     */
    UNIVARIATE_STATISTICS__DATAPOINTS("datapoints"),

    /**
     * Sum.
     */
    UNIVARIATE_STATISTICS__TOTAL("total"),

    /**
     * Average.
     */
    UNIVARIATE_STATISTICS__AVERAGE ("average"),

    /**
     * Standard dev population.
     */
    UNIVARIATE_STATISTICS__STDDEVPA ("stddevpa"),

    /**
     * Standard dev.
     */
    UNIVARIATE_STATISTICS__STDDEV ("stddev"),

    /**
     * Variance.
     */
    UNIVARIATE_STATISTICS__VARIANCE ("variance"),

    /**
     * Weighted average.
     */
    WEIGHTED_AVERAGE__AVERAGE ("average"),

    /**
     * Correlation.
     */
    CORRELATION__CORRELATION ("correlation"),

    /**
     * Slope.
     */
    REGRESSION__SLOPE("slope"),

    /**
     * Y-intercept.
     */
    REGRESSION__YINTERCEPT("YIntercept"),

    /**
     * Size.
     */
    SIZE_VIEW__SIZE ("size");

    private final String name;

    ViewFieldEnum(String name)
    {
        this.name = name;
    }

    /**
     * Returns the field name of fields that contain data within a view's posted objects.
     * @return field name for use with DataSchema to obtain values out of objects.
     */
    public String getName()
    {
        return name;
    }
}

/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view;

import com.espertech.esper.view.std.*;
import com.espertech.esper.view.ext.*;
import com.espertech.esper.view.window.*;
import com.espertech.esper.view.stat.*;
import com.espertech.esper.view.internal.PriorEventViewFactory;
import com.espertech.esper.view.internal.UnionViewFactory;
import com.espertech.esper.view.internal.IntersectViewFactory;

/**
 * Enum for all build-in views.
 */
public enum ViewEnum
{
    /**
     * Length window.
     */
    LENGTH_WINDOW("win", "length", LengthWindowViewFactory.class, null),

    /**
     * Length first window.
     */
    FIRST_LENGTH_WINDOW("win", "firstlength", FirstLengthWindowViewFactory.class, null),

    /**
     * Length batch window.
     */
    LENGTH_BATCH("win", "length_batch", LengthBatchViewFactory.class, null),

    /**
     * Time window.
     */
    TIME_WINDOW("win", "time", TimeWindowViewFactory.class, null),

    /**
     * Time first window.
     */
    FIRST_TIME_WINDOW("win", "firsttime", FirstTimeViewFactory.class, null),

    /**
     * Time batch.
     */
    TIME_BATCH("win","time_batch", TimeBatchViewFactory.class, null),

    /**
     * Time length batch.
     */
    TIME_LENGTH_BATCH("win","time_length_batch", TimeLengthBatchViewFactory.class, null),

    /**
     * Time accumulating view.
     */
    TIME_ACCUM("win","time_accum", TimeAccumViewFactory.class, null),

    /**
     * Externally timed window.
     */
    EXT_TIMED_WINDOW("win", "ext_timed", ExternallyTimedWindowViewFactory.class, null),

    /**
     * Keep-all data window.
     */
    KEEPALL_WINDOW("win", "keepall", KeepAllViewFactory.class, null),

    /**
     * Size view.
     */
    SIZE("std", "size", SizeViewFactory.class, null),

    /**
     * Last event.
     */
    LAST_EVENT("std", "lastevent", LastElementViewFactory.class, null),

    /**
     * First event.
     */
    FIRST_EVENT("std", "firstevent", FirstElementViewFactory.class, null),

    /**
     * Unique.
     */
    UNIQUE_BY_PROPERTY("std", "unique", UniqueByPropertyViewFactory.class, null),

    /**
     * Unique.
     */
    UNIQUE_FIRST_BY_PROPERTY("std", "firstunique", FirstUniqueByPropertyViewFactory.class, null),

    /**
     * Group-by merge.
     */
    GROUP_MERGE("std", "merge", MergeViewFactory.class, null),

    /**
     * Group-by.
     */
    GROUP_PROPERTY("std", "groupby", GroupByViewFactory.class, GROUP_MERGE),

    /**
     * Univariate statistics.
     */
    UNIVARIATE_STATISTICS("stat", "uni", UnivariateStatisticsViewFactory.class, null),

    /**
     * Weighted avg.
     */
    WEIGHTED_AVERAGE("stat", "weighted_avg", WeightedAverageViewFactory.class, null),

    /**
     * Correlation.
     */
    CORRELATION("stat", "correl", CorrelationViewFactory.class, null),

    /**
     * Linest.
     */
    REGRESSION_LINEST("stat", "linest", RegressionLinestViewFactory.class, null),

    /**
     * Sorted window.
     */
    SORT_WINDOW("ext", "sort", SortWindowViewFactory.class, null),

    /**
     * Time order event window.
     */
    TIME_ORDER("ext", "time_order", TimeOrderViewFactory.class, null),

    /**
     * Prior event view.
     */
    PRIOR_EVENT_VIEW("int", "prioreventinternal", PriorEventViewFactory.class, null),

    /**
     * For retain-union policy.
     */
    INTERNAL_UNION("internal", "union", UnionViewFactory.class, null),

    /**
     * For retain-intersection policy.
     */
    INTERNAL_INTERSECT("internal", "intersect", IntersectViewFactory.class, null);

    private final String namespace;
    private final String name;
    private final Class factoryClass;
    private final ViewEnum mergeView;

    ViewEnum(String namespace, String name, Class factoryClass, ViewEnum mergeView)
    {
        this.namespace = namespace;
        this.name = name;
        this.factoryClass = factoryClass;
        this.mergeView = mergeView;
    }

    /**
     * Returns namespace that the object belongs to.
     * @return namespace
     */
    public String getNamespace()
    {
        return namespace;
    }

    /**
     * Returns name of the view that can be used to reference the view in a view expression.
     * @return short name of view
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the enumeration value of the view for merging the data generated by another view.
     * @return view enum for the merge view
     */
    public ViewEnum getMergeView()
    {
        return mergeView;
    }

    /**
     * Returns a view's factory class.
     * @return class of view factory
     */
    public Class getFactoryClass()
    {
        return factoryClass;
    }

    /**
     * Returns the view enumeration value given the name of the view.
     * @param namespace is the namespace name of the view
     * @param name is the short name of the view as used in view expressions
     * @return view enumeration value, or null if no such view name is among the enumerated values
     */
    public static ViewEnum forName(String namespace, String name)
    {
        for (ViewEnum viewEnum : ViewEnum.values())
        {
            if ((viewEnum.namespace.equals(namespace)) && (viewEnum.name.equals(name)))
            {
                return viewEnum;
            }
        }

        return null;
    }
}

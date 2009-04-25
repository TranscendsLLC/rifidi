/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.schedule;

import com.espertech.esper.type.ScheduleUnit;
import com.espertech.esper.type.CronParameter;
import com.espertech.esper.type.NumberSetParameter;

import java.util.SortedSet;
import java.util.EnumMap;
import java.util.TreeSet;
import java.util.Set;

/**
 * Utility for computing from a set of parameter objects a schedule specification carry a
 * crontab-like schedule definition.
 */
public class ScheduleSpecUtil
{
    /**
     * Compute from parameters a crontab schedule.
     * @param args parameters
     * @return crontab schedule
     * @throws ScheduleParameterException if the parameters are invalid
     */
    public static ScheduleSpec computeValues(Object[] args) throws ScheduleParameterException
    {
        EnumMap<ScheduleUnit, SortedSet<Integer>> unitMap = new EnumMap<ScheduleUnit, SortedSet<Integer>>(ScheduleUnit.class);
        Object minutes = args[0];
        Object hours = args[1];
        Object daysOfMonth = args[2];
        Object months = args[3];
        Object daysOfWeek = args[4];
        unitMap.put(ScheduleUnit.MINUTES, computeValues(minutes, ScheduleUnit.MINUTES));
        unitMap.put(ScheduleUnit.HOURS, computeValues(hours, ScheduleUnit.HOURS));
        SortedSet<Integer> resultMonths = computeValues(months, ScheduleUnit.MONTHS);
        if (daysOfWeek instanceof CronParameter && daysOfMonth instanceof CronParameter)
        {
            throw new ScheduleParameterException("Invalid combination between days of week and days of month fields for timer:at");
        }
        if (resultMonths != null && resultMonths.size() == 1 && (resultMonths.first() instanceof Integer))
        {
            // If other arguments are cronParameters, use it for later computations
            CronParameter parameter = null;
            if (daysOfMonth instanceof CronParameter)
            {
                parameter = ((CronParameter) daysOfMonth);
            }
            else if (daysOfWeek instanceof CronParameter)
            {
                parameter = ((CronParameter) daysOfWeek);
            }
            if (parameter != null)
            {
                parameter.setMonth(resultMonths.first());
            }
        }
        SortedSet<Integer> resultDaysOfWeek = computeValues(daysOfWeek, ScheduleUnit.DAYS_OF_WEEK);
        SortedSet<Integer> resultDaysOfMonth = computeValues(daysOfMonth, ScheduleUnit.DAYS_OF_MONTH);
        if (resultDaysOfWeek != null && resultDaysOfWeek.size() == 1 && (resultDaysOfWeek.first() instanceof Integer))
        {
            // The result is in the form "last xx of the month
            // Days of week is replaced by a wildcard and days of month is updated with
            // the computation of "last xx day of month".
            // In this case "days of month" parameter has to be a wildcard.
            if (resultDaysOfWeek.first() > 6)
            {
                if (resultDaysOfMonth != null)
                {
                    throw new ScheduleParameterException("Invalid combination between days of week and days of month fields for timer:at");
                }
                resultDaysOfMonth = resultDaysOfWeek;
                resultDaysOfWeek = null;
            }
        }
        if (resultDaysOfMonth != null && resultDaysOfMonth.size() == 1 && (resultDaysOfMonth.first() instanceof Integer))
        {
            if (resultDaysOfWeek != null)
            {
                throw new ScheduleParameterException("Invalid combination between days of week and days of month fields for timer:at");
            }
        }
        unitMap.put(ScheduleUnit.DAYS_OF_WEEK, resultDaysOfWeek);
        unitMap.put(ScheduleUnit.DAYS_OF_MONTH, resultDaysOfMonth);
        unitMap.put(ScheduleUnit.MONTHS, resultMonths);
        if (args.length > 5)
        {
            unitMap.put(ScheduleUnit.SECONDS, computeValues(args[5], ScheduleUnit.SECONDS));
        }
        return new ScheduleSpec(unitMap);
    }

    private static SortedSet<Integer> computeValues(Object unitParameter, ScheduleUnit unit) throws ScheduleParameterException
    {
        if (unitParameter instanceof Integer)
        {
            SortedSet<Integer> result = new TreeSet<Integer>();
            result.add((Integer) unitParameter);
            return result;
        }

        NumberSetParameter numberSet = (NumberSetParameter) unitParameter;
        if (numberSet.isWildcard(unit.min(), unit.min()))
        {
            return null;
        }

        Set<Integer> result = numberSet.getValuesInRange(unit.min(), unit.max());
        SortedSet<Integer> resultSorted = new TreeSet<Integer>();
        resultSorted.addAll(result);

        return resultSorted;
    }
}

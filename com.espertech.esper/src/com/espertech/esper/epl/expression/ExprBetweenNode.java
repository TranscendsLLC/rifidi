/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.util.*;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.schedule.TimeProvider;

import java.util.Iterator;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Represents the between-clause function in an expression tree.
 */
public class ExprBetweenNode extends ExprNode
{
    private final boolean isLowEndpointIncluded;
    private final boolean isHighEndpointIncluded;
    private final boolean isNotBetween;

    private boolean isAlwaysFalse;
    private ExprBetweenComp computer;

    /**
     * Ctor.
     * @param lowEndpointIncluded is true for the regular 'between' or false for "val in (a:b)" (open range), or
     * false if the endpoint is not included
     * @param highEndpointIncluded indicates whether the high endpoint is included
     * @param notBetween is true for 'not between' or 'not in (a:b), or false for a regular between
     */
    public ExprBetweenNode(boolean lowEndpointIncluded, boolean highEndpointIncluded, boolean notBetween)
    {
        isLowEndpointIncluded = lowEndpointIncluded;
        isHighEndpointIncluded = highEndpointIncluded;
        isNotBetween = notBetween;
    }

    public boolean isConstantResult()
    {
        return false;
    }

    /**
     * Returns true if the low endpoint is included, false if not
     * @return indicator if endppoint is included
     */
    public boolean isLowEndpointIncluded()
    {
        return isLowEndpointIncluded;
    }

    /**
     * Returns true if the high endpoint is included, false if not
     * @return indicator if endppoint is included
     */
    public boolean isHighEndpointIncluded()
    {
        return isHighEndpointIncluded;
    }

    /**
     * Returns true for inverted range, or false for regular (openn/close/half-open/half-closed) ranges.
     * @return true for not betwene, false for between
     */
    public boolean isNotBetween()
    {
        return isNotBetween;
    }

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        if (this.getChildNodes().size() != 3)
        {
            throw new ExprValidationException("The Between operator requires exactly 3 child expressions");
        }

        // Must be either numeric or string
        Class typeOne = JavaClassHelper.getBoxedType(this.getChildNodes().get(0).getType());
        Class typeTwo = JavaClassHelper.getBoxedType(this.getChildNodes().get(1).getType());
        Class typeThree = JavaClassHelper.getBoxedType(this.getChildNodes().get(2).getType());

        if (typeOne == null)
        {
            throw new ExprValidationException("Null value not allowed in between-clause");
        }

        Class compareType = null;
        if ((typeTwo == null) || (typeThree == null))
        {
            isAlwaysFalse = true;
        }
        else {
            if ((typeOne != String.class) || (typeTwo != String.class) || (typeThree != String.class))
            {
                if (!JavaClassHelper.isNumeric(typeOne))
                {
                    throw new ExprValidationException("Implicit conversion from datatype '" +
                            typeOne.getSimpleName() +
                            "' to numeric is not allowed");
                }
                if (!JavaClassHelper.isNumeric(typeTwo))
                {
                    throw new ExprValidationException("Implicit conversion from datatype '" +
                            typeTwo.getSimpleName() +
                            "' to numeric is not allowed");
                }
                if (!JavaClassHelper.isNumeric(typeThree))
                {
                    throw new ExprValidationException("Implicit conversion from datatype '" +
                            typeThree.getSimpleName() +
                            "' to numeric is not allowed");
                }
            }

            Class intermedType = JavaClassHelper.getCompareToCoercionType(typeOne, typeTwo);
            compareType = JavaClassHelper.getCompareToCoercionType(intermedType, typeThree);
            computer = makeComputer(compareType, typeOne, typeTwo, typeThree);
        }
    }

    public Class getType()
    {
        return Boolean.class;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        if (isAlwaysFalse)
        {
            return false;
        }

        // Evaluate first child which is the base value to compare to
        Iterator<ExprNode> it = this.getChildNodes().iterator();
        Object value = it.next().evaluate(eventsPerStream, isNewData);
        if (value == null)
        {
            return false;
        }
        Object lower = it.next().evaluate(eventsPerStream, isNewData);
        Object higher = it.next().evaluate(eventsPerStream, isNewData);

        boolean result = computer.isBetween(value, lower, higher);

        if (isNotBetween)
        {
            return !result;
        }

        return result;
    }

    public boolean equalsNode(ExprNode node_)
    {
        if (!(node_ instanceof ExprBetweenNode))
        {
            return false;
        }

        ExprBetweenNode other = (ExprBetweenNode) node_;
        return other.isNotBetween == this.isNotBetween;
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();

        Iterator<ExprNode> it = this.getChildNodes().iterator();
        buffer.append(it.next().toExpressionString());
        if (isNotBetween)
        {
            buffer.append(" not between ");
        }
        else
        {
            buffer.append(" between ");
        }

        buffer.append(it.next().toExpressionString());
        buffer.append(" and ");
        buffer.append(it.next().toExpressionString());

        return buffer.toString();
    }

    private ExprBetweenComp makeComputer(Class compareType, Class valueType, Class lowType, Class highType)
    {
        ExprBetweenComp computer;

        if (compareType == String.class)
        {
            computer = new ExprBetweenCompString(isLowEndpointIncluded, isHighEndpointIncluded);
        }
        else if (compareType == BigDecimal.class)
        {
            computer = new ExprBetweenCompBigDecimal(isLowEndpointIncluded, isHighEndpointIncluded, valueType, lowType, highType);
        }
        else if (compareType == BigInteger.class)
        {
            computer = new ExprBetweenCompBigInteger(isLowEndpointIncluded, isHighEndpointIncluded, valueType, lowType, highType);
        }
        else if (compareType == Long.class)
        {
            computer = new ExprBetweenCompLong(isLowEndpointIncluded, isHighEndpointIncluded);
        }
        else
        {
            computer = new ExprBetweenCompDouble(isLowEndpointIncluded, isHighEndpointIncluded);
        }
        return computer;
    }

    private interface ExprBetweenComp
    {
        public boolean isBetween(Object value, Object lower, Object upper);
    }

    private static class ExprBetweenCompString implements ExprBetweenComp
    {
        private boolean isLowIncluded;
        private boolean isHighIncluded;

        public ExprBetweenCompString(boolean lowIncluded, boolean isHighIncluded)
        {
            this.isLowIncluded = lowIncluded;
            this.isHighIncluded = isHighIncluded;
        }

        public boolean isBetween(Object value, Object lower, Object upper)
        {
            if ((value == null) || (lower == null) || ((upper == null)))
            {
                return false;
            }
            String valueStr = (String) value;
            String lowerStr = (String) lower;
            String upperStr = (String) upper;

            if (upperStr.compareTo(lowerStr) < 0)
            {
                String temp = upperStr;
                upperStr = lowerStr;
                lowerStr = temp;
            }

            if (valueStr.compareTo(lowerStr) < 0)
            {
                return false;
            }
            if (valueStr.compareTo(upperStr) > 0)
            {
                return false;
            }
            if (!(isLowIncluded))
            {
                if (valueStr.equals(lowerStr))
                {
                    return false;
                }
            }
            if (!(isHighIncluded))
            {
                if (valueStr.equals(upperStr))
                {
                    return false;
                }
            }
            return true;
        }

        public boolean isEqualsEndpoint(Object value, Object endpoint)
        {
            return value.equals(endpoint);
        }
    }

    private static class ExprBetweenCompDouble implements ExprBetweenComp
    {
        private boolean isLowIncluded;
        private boolean isHighIncluded;

        public ExprBetweenCompDouble(boolean lowIncluded, boolean highIncluded)
        {
            isLowIncluded = lowIncluded;
            isHighIncluded = highIncluded;
        }

        public boolean isBetween(Object value, Object lower, Object upper)
        {
            if ((value == null) || (lower == null) || ((upper == null)))
            {
                return false;
            }
            double valueD = ((Number) value).doubleValue();
            double lowerD = ((Number) lower).doubleValue();
            double upperD = ((Number) upper).doubleValue();

            if (lowerD > upperD)
            {
                double temp = upperD;
                upperD = lowerD;
                lowerD = temp;
            }

            if (valueD > lowerD)
            {
                if (valueD < upperD)
                {
                    return true;
                }
                if (isHighIncluded)
                {
                    return valueD == upperD;
                }
                return false;
            }
            if ((isLowIncluded) && (valueD == lowerD))
            {
                return true;
            }
            return false;
        }
    }

    private static class ExprBetweenCompLong implements ExprBetweenComp
    {
        private boolean isLowIncluded;
        private boolean isHighIncluded;

        public ExprBetweenCompLong(boolean lowIncluded, boolean highIncluded)
        {
            isLowIncluded = lowIncluded;
            isHighIncluded = highIncluded;
        }

        public boolean isBetween(Object value, Object lower, Object upper)
        {
            if ((value == null) || (lower == null) || ((upper == null)))
            {
                return false;
            }
            long valueD = ((Number) value).longValue();
            long lowerD = ((Number) lower).longValue();
            long upperD = ((Number) upper).longValue();

            if (lowerD > upperD)
            {
                long temp = upperD;
                upperD = lowerD;
                lowerD = temp;
            }

            if (valueD > lowerD)
            {
                if (valueD < upperD)
                {
                    return true;
                }
                if (isHighIncluded)
                {
                    return valueD == upperD;
                }
                return false;
            }
            if ((isLowIncluded) && (valueD == lowerD))
            {
                return true;
            }
            return false;
        }
    }

    private static class ExprBetweenCompBigDecimal implements ExprBetweenComp
    {
        private boolean isLowIncluded;
        private boolean isHighIncluded;
        private SimpleNumberBigDecimalCoercer numberCoercerLower;
        private SimpleNumberBigDecimalCoercer numberCoercerUpper;
        private SimpleNumberBigDecimalCoercer numberCoercerValue;

        public ExprBetweenCompBigDecimal(boolean lowIncluded, boolean highIncluded, Class valueType, Class lowerType, Class upperType)
        {
            isLowIncluded = lowIncluded;
            isHighIncluded = highIncluded;

            numberCoercerLower = SimpleNumberCoercerFactory.getCoercerBigDecimal(lowerType);
            numberCoercerUpper = SimpleNumberCoercerFactory.getCoercerBigDecimal(upperType);
            numberCoercerValue = SimpleNumberCoercerFactory.getCoercerBigDecimal(valueType);
        }

        public boolean isBetween(Object value, Object lower, Object upper)
        {
            if ((value == null) || (lower == null) || ((upper == null)))
            {
                return false;
            }
            BigDecimal valueD = numberCoercerValue.coerceBoxedBigDec((Number) value);
            BigDecimal lowerD = numberCoercerLower.coerceBoxedBigDec((Number) lower);
            BigDecimal upperD = numberCoercerUpper.coerceBoxedBigDec((Number) upper);

            if (lowerD.compareTo(upperD) > 0)
            {
                BigDecimal temp = upperD;
                upperD = lowerD;
                lowerD = temp;
            }

            if (valueD.compareTo(lowerD) > 0)
            {
                if (valueD.compareTo(upperD) < 0)
                {
                    return true;
                }
                if (isHighIncluded)
                {
                    return valueD.equals(upperD);
                }
                return false;
            }
            if ((isLowIncluded) && (valueD.equals(lowerD)))
            {
                return true;
            }
            return false;
        }
    }

    private static class ExprBetweenCompBigInteger implements ExprBetweenComp
    {
        private boolean isLowIncluded;
        private boolean isHighIncluded;
        private SimpleNumberBigIntegerCoercer numberCoercerLower;
        private SimpleNumberBigIntegerCoercer numberCoercerUpper;
        private SimpleNumberBigIntegerCoercer numberCoercerValue;

        public ExprBetweenCompBigInteger(boolean lowIncluded, boolean highIncluded, Class valueType, Class lowerType, Class upperType)
        {
            isLowIncluded = lowIncluded;
            isHighIncluded = highIncluded;

            numberCoercerLower = SimpleNumberCoercerFactory.getCoercerBigInteger(lowerType);
            numberCoercerUpper = SimpleNumberCoercerFactory.getCoercerBigInteger(upperType);
            numberCoercerValue = SimpleNumberCoercerFactory.getCoercerBigInteger(valueType);
        }

        public boolean isBetween(Object value, Object lower, Object upper)
        {
            if ((value == null) || (lower == null) || ((upper == null)))
            {
                return false;
            }
            BigInteger valueD = numberCoercerValue.coerceBoxedBigInt((Number) value);
            BigInteger lowerD = numberCoercerLower.coerceBoxedBigInt((Number) lower);
            BigInteger upperD = numberCoercerUpper.coerceBoxedBigInt((Number) upper);

            if (lowerD.compareTo(upperD) > 0)
            {
                BigInteger temp = upperD;
                upperD = lowerD;
                lowerD = temp;
            }

            if (valueD.compareTo(lowerD) > 0)
            {
                if (valueD.compareTo(upperD) < 0)
                {
                    return true;
                }
                if (isHighIncluded)
                {
                    return valueD.equals(upperD);
                }
                return false;
            }
            if ((isLowIncluded) && (valueD.equals(lowerD)))
            {
                return true;
            }
            return false;
        }
    }
}

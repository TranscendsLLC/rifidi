/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.type;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.util.SimpleNumberBigIntegerCoercer;
import com.espertech.esper.util.SimpleNumberBigDecimalCoercer;

import java.math.BigInteger;
import java.math.BigDecimal;


/**
 * Enumeration for the type of arithmatic to use.
 */
public enum MinMaxTypeEnum
{
    /**
     * Max.
     */
    MAX ("max"),

    /**
     * Min.
     */
    MIN ("min");

    private String expressionText;

    private MinMaxTypeEnum(String expressionText)
    {
        this.expressionText = expressionText;
    }

    /**
     * Returns textual representation of enum.
     * @return text for enum
     */
    public String getExpressionText()
    {
        return expressionText;
    }

    /**
     * Executes child expression nodes and compares results.
     */
    public interface Computer
    {
        /**
         * Executes child expression nodes and compares results, returning the min/max.
         * @param eventsPerStream events per stream
         * @param isNewData true if new data
         * @return result
         */
        public Number execute(EventBean[] eventsPerStream, boolean isNewData);
    }

    /**
     * Determines minimum using Number.doubleValue().
     */
    public static class MinComputerDoubleCoerce implements Computer
    {
        private ExprNode[] childNodes;

        /**
         * Ctor.
         * @param childNodes array of expression nodes
         */
        public MinComputerDoubleCoerce(ExprNode[] childNodes)
        {
            this.childNodes = childNodes;
        }

        public Number execute(EventBean[] eventsPerStream, boolean isNewData)
        {
            Number valueChildOne = (Number) childNodes[0].evaluate(eventsPerStream, isNewData);
            Number valueChildTwo = (Number) childNodes[1].evaluate(eventsPerStream, isNewData);

            if ((valueChildOne == null) || (valueChildTwo == null))
            {
                return null;
            }

            Number result;
            if (valueChildOne.doubleValue() > valueChildTwo.doubleValue())
            {
                result = valueChildTwo;
            }
            else
            {
                result = valueChildOne;
            }
            for (int i = 2; i < childNodes.length; i++)
            {
                Number valueChild = (Number) childNodes[i].evaluate(eventsPerStream, isNewData);
                if (valueChild == null)
                {
                    return null;
                }
                if (valueChild.doubleValue() < result.doubleValue())
                {
                    result = valueChild;
                }
            }
            return result;
        }
    }

    /**
     * Determines maximum using Number.doubleValue().
     */
    public static class MaxComputerDoubleCoerce implements Computer
    {
        private ExprNode[] childNodes;

        /**
         * Ctor.
         * @param childNodes array of expression nodes
         */
        public MaxComputerDoubleCoerce(ExprNode[] childNodes)
        {
            this.childNodes = childNodes;
        }

        public Number execute(EventBean[] eventsPerStream, boolean isNewData)
        {
            Number valueChildOne = (Number) childNodes[0].evaluate(eventsPerStream, isNewData);
            Number valueChildTwo = (Number) childNodes[1].evaluate(eventsPerStream, isNewData);

            if ((valueChildOne == null) || (valueChildTwo == null))
            {
                return null;
            }

            Number result;
            if (valueChildOne.doubleValue() > valueChildTwo.doubleValue())
            {
                result = valueChildOne;
            }
            else
            {
                result = valueChildTwo;
            }
            for (int i = 2; i < childNodes.length; i++)
            {
                Number valueChild = (Number) childNodes[i].evaluate(eventsPerStream, isNewData);
                if (valueChild == null)
                {
                    return null;
                }
                if (valueChild.doubleValue() > result.doubleValue())
                {
                    result = valueChild;
                }
            }
            return result;
        }
    }

    /**
     * Determines minimum/maximum using BigInteger.compareTo.
     */
    public static class ComputerBigIntCoerce implements Computer
    {
        private ExprNode[] childNodes;
        private SimpleNumberBigIntegerCoercer[] convertors;
        private boolean isMax;

        /**
         * Ctor.
         * @param childNodes expressions
         * @param convertors convertors to BigInteger
         * @param isMax true if max, false if min
         */
        public ComputerBigIntCoerce(ExprNode[] childNodes, SimpleNumberBigIntegerCoercer[] convertors, boolean isMax)
        {
            this.childNodes = childNodes;
            this.convertors = convertors;
            this.isMax = isMax;
        }

        public Number execute(EventBean[] eventsPerStream, boolean isNewData)
        {
            Number valueChildOne = (Number) childNodes[0].evaluate(eventsPerStream, isNewData);
            Number valueChildTwo = (Number) childNodes[1].evaluate(eventsPerStream, isNewData);

            if ((valueChildOne == null) || (valueChildTwo == null))
            {
                return null;
            }

            BigInteger bigIntOne = convertors[0].coerceBoxedBigInt(valueChildOne);
            BigInteger bigIntTwo = convertors[1].coerceBoxedBigInt(valueChildTwo);

            BigInteger result;
            if ( (isMax && (bigIntOne.compareTo(bigIntTwo) > 0)) ||
                 (!isMax && (bigIntOne.compareTo(bigIntTwo) < 0)))
            {
                result = bigIntOne;
            }
            else
            {
                result = bigIntTwo;
            }
            for (int i = 2; i < childNodes.length; i++)
            {
                Number valueChild = (Number) childNodes[i].evaluate(eventsPerStream, isNewData);
                if (valueChild == null)
                {
                    return null;
                }
                BigInteger bigInt = convertors[i].coerceBoxedBigInt(valueChild);
                if ( (isMax && (result.compareTo(bigInt) < 0)) ||
                     (!isMax && (result.compareTo(bigInt) > 0)))
                {
                    result = bigInt;
                }
            }
            return result;
        }
    }

    /**
     * Determines minimum/maximum using BigDecimal.compareTo.
     */
    public static class ComputerBigDecCoerce implements Computer
    {
        private ExprNode[] childNodes;
        private SimpleNumberBigDecimalCoercer[] convertors;
        private boolean isMax;

        /**
         * Ctor.
         * @param childNodes expressions
         * @param convertors convertors to BigDecimal
         * @param isMax true if max, false if min
         */
        public ComputerBigDecCoerce(ExprNode[] childNodes, SimpleNumberBigDecimalCoercer[] convertors, boolean isMax)
        {
            this.childNodes = childNodes;
            this.convertors = convertors;
            this.isMax = isMax;
        }

        public Number execute(EventBean[] eventsPerStream, boolean isNewData)
        {
            Number valueChildOne = (Number) childNodes[0].evaluate(eventsPerStream, isNewData);
            Number valueChildTwo = (Number) childNodes[1].evaluate(eventsPerStream, isNewData);

            if ((valueChildOne == null) || (valueChildTwo == null))
            {
                return null;
            }

            BigDecimal bigDecOne = convertors[0].coerceBoxedBigDec(valueChildOne);
            BigDecimal bigDecTwo = convertors[1].coerceBoxedBigDec(valueChildTwo);

            BigDecimal result;
            if ( (isMax && (bigDecOne.compareTo(bigDecTwo) > 0)) ||
                 (!isMax && (bigDecOne.compareTo(bigDecTwo) < 0)))
            {
                result = bigDecOne;
            }
            else
            {
                result = bigDecTwo;
            }
            for (int i = 2; i < childNodes.length; i++)
            {
                Number valueChild = (Number) childNodes[i].evaluate(eventsPerStream, isNewData);
                if (valueChild == null)
                {
                    return null;
                }
                BigDecimal bigDec = convertors[i].coerceBoxedBigDec(valueChild);
                if ( (isMax && (result.compareTo(bigDec) < 0)) ||
                     (!isMax && (result.compareTo(bigDec) > 0)))
                {
                    result = bigDec;
                }
            }
            return result;
        }
    }
}

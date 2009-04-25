/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.util.CoercionException;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.util.SimpleNumberCoercerFactory;
import com.espertech.esper.util.SimpleNumberCoercer;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents the case-when-then-else control flow function is an expression tree.
 */
public class ExprCaseNode extends ExprNode
{
    private final boolean isCase2;
    private List<UniformPair<ExprNode>> whenThenNodeList;
    private ExprNode optionalCompareExprNode;
    private ExprNode optionalElseExprNode;
    private Class resultType;
    private boolean isNumericResult;
    private boolean mustCoerce;
    private SimpleNumberCoercer coercer;

    /**
     * Ctor.
     * @param isCase2 is an indicator of which Case statement we are working on.
     * <p> True indicates a 'Case2' statement with syntax "case a when a1 then b1 else b2".
     * <p> False indicates a 'Case1' statement with syntax "case when a=a1 then b1 else b2".
     */
    public ExprCaseNode(boolean isCase2)
    {
        this.isCase2 = isCase2;
    }

    /**
     * Returns true if this is a switch-type case.
     * @return true for switch-type case, or false for when-then type
     */
    public boolean isCase2()
    {
        return isCase2;
    }

    public void validate(StreamTypeService streamTypeService_, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        if (isCase2)
        {
            validateCaseTwo();
        }
        else
        {
            validateCaseOne();
        }

        // Determine type of each result (then-node and else node) child node expression
        List<Class> childTypes = new LinkedList<Class>();
        for (UniformPair<ExprNode> pair : whenThenNodeList)
        {
            childTypes.add(pair.getSecond().getType());
        }
        if (optionalElseExprNode != null)
        {
            childTypes.add(optionalElseExprNode.getType());
        }

        // Determine common denominator type
        try {
            resultType = JavaClassHelper.getCommonCoercionType(childTypes.toArray(new Class[childTypes.size()]));
            if (JavaClassHelper.isNumeric(resultType))
            {
                isNumericResult = true;
            }
        }
        catch (CoercionException ex)
        {
            throw new ExprValidationException("Implicit conversion not allowed: " + ex.getMessage());
        }
    }

    public boolean isConstantResult()
    {
        return false;
    }

    public Class getType()
    {
        return resultType;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        if (!isCase2)
        {
            return evaluateCaseSyntax1(eventsPerStream, isNewData);
        }
        else
        {
            return evaluateCaseSyntax2(eventsPerStream, isNewData);
        }
    }

    public boolean equalsNode(ExprNode node_)
    {
        if (!(node_ instanceof ExprCaseNode))
        {
            return false;
        }

        ExprCaseNode otherExprCaseNode = (ExprCaseNode) node_;
        return this.isCase2 == otherExprCaseNode.isCase2;
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("case");
        if (isCase2)
        {
            buffer.append(' ');
            buffer.append(this.getChildNodes().get(0).toExpressionString());
        }
        for (UniformPair<ExprNode> p : whenThenNodeList)
        {
            buffer.append(" when ");
            buffer.append(p.getFirst().toExpressionString());
            buffer.append(" then ");
            buffer.append(p.getSecond().toExpressionString());
        }
        if (optionalElseExprNode != null)
        {
            buffer.append(" else ");
            buffer.append(optionalElseExprNode.toExpressionString());
        }
        buffer.append(" end");
        return buffer.toString();
    }

    private void validateCaseOne() throws ExprValidationException
    {
        // Case 1 expression example:
        //      case when a=b then x [when c=d then y...] [else y]
        //
        ExprNode[] children = this.getChildNodes().toArray(new ExprNode[this.getChildNodes().size()]);
        if (children.length < 2)
        {
            throw new ExprValidationException("Case node must have at least 2 child nodes");
        }

        whenThenNodeList = new LinkedList<UniformPair<ExprNode>>();
        int numWhenThen = children.length >> 1;
        for (int i = 0; i < numWhenThen; i++)
        {
            ExprNode whenExpr = children[(i << 1)];
            ExprNode thenExpr = children[(i << 1) + 1];
            if (whenExpr.getType() != Boolean.class)
            {
                throw new ExprValidationException("Case node 'when' expressions must return a boolean value");
            }
            whenThenNodeList.add(new UniformPair<ExprNode>(whenExpr, thenExpr));
        }
        if (children.length % 2 != 0)
        {
            optionalElseExprNode = children[children.length - 1];
        }
    }

    @SuppressWarnings({"MultiplyOrDivideByPowerOfTwo"})
    private void validateCaseTwo() throws ExprValidationException
    {
        // Case 2 expression example:
        //      case p when p1 then x [when p2 then y...] [else z]
        //
        ExprNode[] children = this.getChildNodes().toArray(new ExprNode[this.getChildNodes().size()]);
        if (children.length < 3)
        {
            throw new ExprValidationException("Case node must have at least 3 child nodes");
        }

        optionalCompareExprNode = children[0];

        whenThenNodeList = new LinkedList<UniformPair<ExprNode>>();
        int numWhenThen = (children.length - 1) / 2;
        for (int i = 0; i < numWhenThen; i++)
        {
            whenThenNodeList.add(new UniformPair<ExprNode>(children[i * 2 + 1], children[i * 2 + 2]));
        }
        if (numWhenThen * 2 + 1 < children.length)
        {
            optionalElseExprNode = children[children.length - 1];
        }

        // validate we can compare result types
        List<Class> comparedTypes = new LinkedList<Class>();
        comparedTypes.add(optionalCompareExprNode.getType());
        for (UniformPair<ExprNode> pair : whenThenNodeList)
        {
            comparedTypes.add(pair.getFirst().getType());
        }

        // Determine common denominator type
        try {
            Class coercionType = JavaClassHelper.getCommonCoercionType(comparedTypes.toArray(new Class[comparedTypes.size()]));

            // Determine if we need to coerce numbers when one type doesn't match any other type
            if (JavaClassHelper.isNumeric(coercionType))
            {
                mustCoerce = false;
                for (Class comparedType : comparedTypes)
                {
                    if (comparedType != coercionType)
                    {
                        mustCoerce = true;
                    }
                }
                if (mustCoerce)
                {
                    coercer = SimpleNumberCoercerFactory.getCoercer(null, coercionType);
                }
            }
        }
        catch (CoercionException ex)
        {
            throw new ExprValidationException("Implicit conversion not allowed: " + ex.getMessage());
        }
    }

    private Object evaluateCaseSyntax1(EventBean[] eventsPerStream, boolean isNewData)
    {
        // Case 1 expression example:
        //      case when a=b then x [when c=d then y...] [else y]

        Object caseResult = null;
        boolean matched = false;
        for (UniformPair<ExprNode> p : whenThenNodeList)
        {
            Boolean whenResult = (Boolean) p.getFirst().evaluate(eventsPerStream, isNewData);

            // If the 'when'-expression returns true
            if (whenResult)
            {
                caseResult = p.getSecond().evaluate(eventsPerStream, isNewData);
                matched = true;
                break;
            }
        }

        if ((!matched) && (optionalElseExprNode != null))
        {
            caseResult = optionalElseExprNode.evaluate(eventsPerStream, isNewData);
        }

        if (caseResult == null)
        {
            return null;
        }

        if ((caseResult.getClass() != resultType) && (isNumericResult))
        {
            return JavaClassHelper.coerceBoxed( (Number) caseResult, resultType);
        }
        return caseResult;
    }

    private Object evaluateCaseSyntax2(EventBean[] eventsPerStream, boolean isNewData)
    {
        // Case 2 expression example:
        //      case p when p1 then x [when p2 then y...] [else z]

        Object checkResult = optionalCompareExprNode.evaluate(eventsPerStream, isNewData);
        Object caseResult = null;
        boolean matched = false;
        for (UniformPair<ExprNode> p : whenThenNodeList)
        {
            Object whenResult = p.getFirst().evaluate(eventsPerStream, isNewData);

            if (compare(checkResult, whenResult)) {
                caseResult = p.getSecond().evaluate(eventsPerStream, isNewData);
                matched = true;
                break;
            }
        }

        if ((!matched) && (optionalElseExprNode != null))
        {
            caseResult = optionalElseExprNode.evaluate(eventsPerStream, isNewData);
        }

        if (caseResult == null)
        {
            return null;
        }

        if ((caseResult.getClass() != resultType) && (isNumericResult))
        {
            return JavaClassHelper.coerceBoxed( (Number) caseResult, resultType);
        }
        return caseResult;
    }

    private boolean compare(Object leftResult, Object rightResult)
    {
        if (leftResult == null)
        {
            return (rightResult == null);
        }
        if (rightResult == null)
        {
            return false;
        }

        if (!mustCoerce)
        {
            return leftResult.equals(rightResult);
        }
        else
        {
            Number left = coercer.coerceBoxed((Number) leftResult);
            Number right = coercer.coerceBoxed((Number) rightResult);
            return left.equals(right);
        }
    }
}



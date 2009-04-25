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
import com.espertech.esper.epl.core.ViewResourceCallback;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.view.window.RandomAccessByIndex;
import com.espertech.esper.view.window.RelativeAccessByEventNIndex;
import com.espertech.esper.view.window.RandomAccessByIndexGetter;
import com.espertech.esper.view.window.RelativeAccessByEventNIndexGetter;
import com.espertech.esper.view.ViewCapDataWindowAccess;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.schedule.TimeProvider;

/**
 * Represents the 'prev' previous event function in an expression node tree.
 */
public class ExprPreviousNode extends ExprNode implements ViewResourceCallback
{
    private static final long serialVersionUID = 0L;

    private Class resultType;
    private int streamNumber;
    private Integer constantIndexNumber;
    private boolean isConstantIndex;

    private transient RandomAccessByIndexGetter randomAccessGetter;
    private transient RelativeAccessByEventNIndexGetter relativeAccessGetter;

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        if (this.getChildNodes().size() != 2)
        {
            throw new ExprValidationException("Previous node must have 2 child nodes");
        }

        // Determine if the index is a constant value or an expression to evaluate
        if (this.getChildNodes().get(0).isConstantResult())
        {
            ExprNode constantNode = this.getChildNodes().get(0);
            Object value = constantNode.evaluate(null, false);
            if (!(value instanceof Number))
            {
                throw new ExprValidationException("Previous function requires an integer index parameter or expression");
            }

            Number valueNumber = (Number) value;
            if (JavaClassHelper.isFloatingPointNumber(valueNumber))
            {
                throw new ExprValidationException("Previous function requires an integer index parameter or expression");
            }

            constantIndexNumber = valueNumber.intValue();
            isConstantIndex = true;
        }

        // Determine stream number
        ExprIdentNode identNode = (ExprIdentNode) this.getChildNodes().get(1);
        streamNumber = identNode.getStreamId();
        resultType = this.getChildNodes().get(1).getType();

        if (viewResourceDelegate == null)
        {
            throw new ExprValidationException("Previous function cannot be used in this context");
        }

        // Request a callback that provides the required access
        if (!viewResourceDelegate.requestCapability(streamNumber, new ViewCapDataWindowAccess(), this))
        {
            throw new ExprValidationException("Previous function requires a single data window view onto the stream");
        }
    }

    public Class getType()
    {
        return resultType;
    }

    public boolean isConstantResult()
    {
        return false;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        Integer index;

        // Use constant if supplied
        if (isConstantIndex)
        {
            index = constantIndexNumber;
        }
        else
        {
            // evaluate first child, which returns the index
            Object indexResult = this.getChildNodes().get(0).evaluate(eventsPerStream, isNewData);
            if (indexResult == null)
            {
                return null;
            }
            index = ((Number) indexResult).intValue();
        }

        // access based on index returned
        EventBean substituteEvent = null;
        if (randomAccessGetter != null)
        {
            RandomAccessByIndex randomAccess = randomAccessGetter.getAccessor();
            if (isNewData)
            {
                substituteEvent = randomAccess.getNewData(index);
            }
        }
        else
        {
            if (isNewData)
            {
                EventBean evalEvent = eventsPerStream[streamNumber];
                RelativeAccessByEventNIndex relativeAccess = relativeAccessGetter.getAccessor(evalEvent);
                substituteEvent = relativeAccess.getRelativeToEvent(evalEvent, index);
            }
        }
        if (substituteEvent == null)
        {
            return null;
        }

        // Substitute original event with prior event, evaluate inner expression
        EventBean originalEvent = eventsPerStream[streamNumber];
        eventsPerStream[streamNumber] = substituteEvent;
        Object evalResult = this.getChildNodes().get(1).evaluate(eventsPerStream, isNewData);
        eventsPerStream[streamNumber] = originalEvent;

        return evalResult;
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("prev(");
        buffer.append(this.getChildNodes().get(0).toExpressionString());
        buffer.append(',');
        buffer.append(this.getChildNodes().get(1).toExpressionString());
        buffer.append(')');
        return buffer.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprPreviousNode))
        {
            return false;
        }

        return true;
    }

    public void setViewResource(Object resource)
    {
        if (resource instanceof RandomAccessByIndexGetter)
        {
            randomAccessGetter = (RandomAccessByIndexGetter) resource;
        }
        else if (resource instanceof RelativeAccessByEventNIndexGetter)
        {
            relativeAccessGetter = (RelativeAccessByEventNIndexGetter) resource;
        }
        else
        {
            throw new IllegalArgumentException("View resource " + resource.getClass() + " not recognized by expression node");
        }
    }
}

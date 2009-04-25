/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.core.ViewResourceCallback;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.view.window.RandomAccessByIndex;
import com.espertech.esper.view.window.RelativeAccessByEventNIndex;
import com.espertech.esper.view.ViewCapPriorEventAccess;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.schedule.TimeProvider;

/**
 * Represents the 'prior' prior event function in an expression node tree.
 */
public class ExprPriorNode extends ExprNode implements ViewResourceCallback
{
    private Class resultType;
    private int streamNumber;
    private int constantIndexNumber;
    private RelativeAccessByEventNIndex relativeAccess;
    private RandomAccessByIndex randomAccess;

    /**
     * Returns the index of the prior.
     * @return index of prior function
     */
    public int getConstantIndexNumber()
    {
        return constantIndexNumber;
    }

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        if (this.getChildNodes().size() != 2)
        {
            throw new ExprValidationException("Prior node must have 2 child nodes");
        }
        if (!(this.getChildNodes().get(0).isConstantResult()))
        {
            throw new ExprValidationException("Prior function requires an integer index parameter");
        }
        ExprNode constantNode = this.getChildNodes().get(0);
        if (constantNode.getType() != Integer.class)
        {
            throw new ExprValidationException("Prior function requires an integer index parameter");
        }

        Object value = constantNode.evaluate(null, false);
        constantIndexNumber = ((Number) value).intValue();

        // Determine stream number
        ExprIdentNode identNode = (ExprIdentNode) this.getChildNodes().get(1);
        streamNumber = identNode.getStreamId();
        resultType = this.getChildNodes().get(1).getType();

        if (viewResourceDelegate == null)
        {
            throw new ExprValidationException("Prior function cannot be used in this context");
        }
        // Request a callback that provides the required access
        if (!viewResourceDelegate.requestCapability(streamNumber, new ViewCapPriorEventAccess(constantIndexNumber), this))
        {
            throw new ExprValidationException("Prior function requires the prior event view resource");
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
        EventBean originalEvent = eventsPerStream[streamNumber];
        EventBean substituteEvent = null;

        if (randomAccess != null)
        {
            if (isNewData)
            {
                substituteEvent = randomAccess.getNewData(constantIndexNumber);
            }
            else
            {
                substituteEvent = randomAccess.getOldData(constantIndexNumber);
            }
        }
        else
        {
            substituteEvent = relativeAccess.getRelativeToEvent(originalEvent, constantIndexNumber);
        }

        // Substitute original event with prior event, evaluate inner expression
        eventsPerStream[streamNumber] = substituteEvent;
        Object evalResult = this.getChildNodes().get(1).evaluate(eventsPerStream, isNewData);
        eventsPerStream[streamNumber] = originalEvent;

        return evalResult;
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("prior(");
        buffer.append(this.getChildNodes().get(0).toExpressionString());
        buffer.append(',');
        buffer.append(this.getChildNodes().get(1).toExpressionString());
        buffer.append(')');
        return buffer.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprPriorNode))
        {
            return false;
        }

        return true;
    }

    public void setViewResource(Object resource)
    {
        if (resource instanceof RelativeAccessByEventNIndex)
        {
            relativeAccess = (RelativeAccessByEventNIndex) resource;
        }
        else if (resource instanceof RandomAccessByIndex)
        {
            randomAccess = (RandomAccessByIndex) resource;
        }
        else
        {
            throw new IllegalArgumentException("View resource " + resource.getClass() + " not recognized by expression node");
        }
    }
}

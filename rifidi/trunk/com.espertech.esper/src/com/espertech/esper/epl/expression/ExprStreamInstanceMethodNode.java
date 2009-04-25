/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.schedule.TimeProvider;
import net.sf.cglib.reflect.FastMethod;
import net.sf.cglib.reflect.FastClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Represents an invocation of a instance method on an event of a given stream in the expression tree.
 */
public class ExprStreamInstanceMethodNode extends ExprNode
{
    private static final Log log = LogFactory.getLog(ExprNode.class);
	private final String streamName;
	private final String methodName;

    private int streamNum = -1;
    private Class[] paramTypes;
	private FastMethod instanceMethod;

    /**
	 * Ctor.
	 * @param streamName - the declaring class for the method that this node will invoke
	 * @param methodName - the name of the method that this node will invoke
	 */
	public ExprStreamInstanceMethodNode(String streamName, String methodName)
	{
		if(streamName == null)
		{
			throw new NullPointerException("Stream name is null");
		}
		if(methodName == null)
		{
			throw new NullPointerException("Method name is null");
		}

		this.streamName = streamName;
		this.methodName = methodName;
	}

    public boolean isConstantResult()
    {
        return false;
    }

	/**
     * Returns the stream name.
	 * @return the stream that provides events that provide the instance method
	 */
	public String getStreamName() {
		return streamName;
	}

	/**
     * Returns the method name.
	 * @return the name of the method
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
     * Returns parameter descriptor.
	 * @return the types of the child nodes of this node
	 */
	public Class[] getParamTypes() {
		return paramTypes;
	}

    /**
     * Returns stream id supplying the property value.
     * @return stream number
     */
    public int getStreamId()
    {
        if (streamNum == -1)
        {
            throw new IllegalStateException("Stream underlying node has not been validated");
        }
        return streamNum;
    }

    public String toExpressionString()
	{
        StringBuilder buffer = new StringBuilder();
		buffer.append(streamName);
		buffer.append('.');
		buffer.append(methodName);

		buffer.append('(');
		String appendString = "";
		for(ExprNode child : getChildNodes())
		{
			buffer.append(appendString);
			buffer.append(child.toExpressionString());
			appendString = ", ";
		}
		buffer.append(')');

		return buffer.toString();
	}

	public boolean equalsNode(ExprNode node)
	{
		if(!(node instanceof ExprStreamInstanceMethodNode))
		{
			return false;
		}

		if(instanceMethod == null)
		{
			throw new IllegalStateException("ExprStreamInstanceMethodNode has not been validated");
		}
		else
		{
			ExprStreamInstanceMethodNode otherNode = (ExprStreamInstanceMethodNode) node;
			return streamName.equals(otherNode.streamName) && instanceMethod.equals(otherNode.instanceMethod);
		}
	}

	public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
	{
		// Get the types of the childNodes
		List<ExprNode> childNodes = this.getChildNodes();
		paramTypes = new Class[childNodes.size()];
		int count = 0;

        for(ExprNode childNode : childNodes)
		{
			paramTypes[count++] = childNode.getType();
        }

        String[] streams = streamTypeService.getStreamNames();
        for (int i = 0; i < streams.length; i++)
        {
            if ((streams[i] != null) && (streams[i].equals(streamName)))
            {
                streamNum = i;
                break;
            }
        }

        if (streamNum == -1)
        {
            throw new ExprValidationException("Stream by name '" + streamName + "' could not be found among all streams");
        }

        EventType eventType = streamTypeService.getEventTypes()[streamNum];
        Class type = eventType.getUnderlyingType();

        // Try to resolve the method
		try
		{
            Method method = methodResolutionService.resolveMethod(type, methodName, paramTypes);
			FastClass declaringClass = FastClass.create(Thread.currentThread().getContextClassLoader(), method.getDeclaringClass());
			instanceMethod = declaringClass.getMethod(method);
		}
		catch(Exception e)
		{
            log.debug("Error resolving method for instance", e);
            throw new ExprValidationException(e.getMessage(), e);
		}
	}

	public Class getType()
	{
		if(instanceMethod == null)
		{
			throw new IllegalStateException("ExprStaticMethodNode has not been validated");
		}
		return instanceMethod.getReturnType();
	}

	public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
	{
        // get underlying event
        EventBean event = eventsPerStream[streamNum];
        if (event == null)
        {
            return null;
        }
        Object underlying = event.getUnderlying();

        // get parameters
        List<ExprNode> childNodes = this.getChildNodes();
		Object[] args = new Object[childNodes.size()];
		int count = 0;
		for(ExprNode childNode : childNodes)
		{
			args[count++] = childNode.evaluate(eventsPerStream, isNewData);
		}

		try
		{
            return instanceMethod.invoke(underlying, args);
		}
		catch (InvocationTargetException e)
		{
            log.warn("Error evaluating instance method by name '" + instanceMethod.getName() + "': " + e.getMessage(), e);
            return null;
		}
	}
}

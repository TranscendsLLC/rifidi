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
import com.espertech.esper.schedule.TimeProvider;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Represents an invocation of a static library method in the expression tree.
 */
public class ExprStaticMethodNode extends ExprNode
{
    private static final Log log = LogFactory.getLog(ExprStaticMethodNode.class);

	private final String className;
	private final String methodName;
	private Class[] paramTypes;
	private FastMethod staticMethod;
    private boolean isConstantParameters;
    private boolean isCachedResult;
    private Object cachedResult;
    private boolean isUseCache;

    /**
	 * Ctor.
	 * @param className - the declaring class for the method that this node will invoke
	 * @param methodName - the name of the method that this node will invoke
     * @param isUseCache - configuration whether to use cache
	 */
	public ExprStaticMethodNode(String className, String methodName, boolean isUseCache)
	{
		if(className == null)
		{
			throw new NullPointerException("Class name is null");
		}
		if(methodName == null)
		{
			throw new NullPointerException("Method name is null");
		}

		this.className = className;
		this.methodName = methodName;
        this.isUseCache = isUseCache;
    }

    public boolean isConstantResult()
    {
        return isConstantParameters;
    }

    /**
     * Returns the static method.
	 * @return the static method that this node invokes
	 */
	protected Method getStaticMethod()
	{
		return staticMethod.getJavaMethod();
	}

	/**
     * Returns the class name.
	 * @return the class that declared the static method
	 */
	public String getClassName() {
		return className;
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

	public String toExpressionString()
	{
        StringBuilder buffer = new StringBuilder();
		buffer.append(className);
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
		if(!(node instanceof ExprStaticMethodNode))
		{
			return false;
		}

		if(staticMethod == null)
		{
			throw new IllegalStateException("ExprStaticMethodNode has not been validated");
		}
		else
		{
			ExprStaticMethodNode otherNode = (ExprStaticMethodNode) node;
			return staticMethod.equals(otherNode.staticMethod);
		}
	}

	public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
	{
		// Get the types of the childNodes
		List<ExprNode> childNodes = this.getChildNodes();
		paramTypes = new Class[childNodes.size()];
		int count = 0;
        
        boolean allConstants = true;
        for(ExprNode childNode : childNodes)
		{
			paramTypes[count++] = childNode.getType();
            if (!(childNode.isConstantResult()))
            {
                allConstants = false;
            }
        }
        isConstantParameters = allConstants && isUseCache;

        // Try to resolve the method
		try
		{
			Method method = methodResolutionService.resolveMethod(className, methodName, paramTypes);
			FastClass declaringClass = FastClass.create(Thread.currentThread().getContextClassLoader(), method.getDeclaringClass());
			staticMethod = declaringClass.getMethod(method);
		}
		catch(Exception e)
		{
			throw new ExprValidationException(e.getMessage());
		}
	}

	public Class getType()
	{
		if(staticMethod == null)
		{
			throw new IllegalStateException("ExprStaticMethodNode has not been validated");
		}
		return staticMethod.getReturnType();
	}

	public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
	{
        if ((isConstantParameters) && (isCachedResult))
        {
            return cachedResult;
        }
        List<ExprNode> childNodes = this.getChildNodes();

		Object[] args = new Object[childNodes.size()];
		int count = 0;
		for(ExprNode childNode : childNodes)
		{
			args[count++] = childNode.evaluate(eventsPerStream, isNewData);
		}

		// The method is static so the object it is invoked on
		// can be null
		Object obj = null;
		try
		{
            Object result = staticMethod.invoke(obj, args);
            if (isConstantParameters)
            {
                cachedResult = result;
                isCachedResult = true;
            }
            return result;
		}
		catch (InvocationTargetException e)
		{
            String message = "Method '" + staticMethod.getName() +
                    "' of class '" + className +
                    "' reported an exception: " +
                    e.getTargetException();
            log.error(message, e.getTargetException());
		}
        return null;
    }
}

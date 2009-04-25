/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client.soda;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * An stream that polls from a method.
 */
public class MethodInvocationStream extends Stream
{
    private String className;
    private String methodName;
    private List<Expression> parameterExpressions;

    /**
     * Creates a new method-invocation-based stream without parameters.
     * @param className is the name of the class providing the method
     * @param methodName is the name of the public static method
     * @return stream
     */
    public static MethodInvocationStream create(String className, String methodName)
    {
        return new MethodInvocationStream(className, methodName, null);
    }

    /**
     * Creates a new method-invocation-based stream without parameters.
     * @param className is the name of the class providing the method
     * @param methodName is the name of the public static method
     * @param optStreamName is the optional as-name of the stream, or null if unnamed
     * @return stream
     */
    public static MethodInvocationStream create(String className, String methodName, String optStreamName)
    {
        return new MethodInvocationStream(className, methodName, optStreamName);
    }

    /**
     * Ctor.
     * @param className is the name of the class providing the method
     * @param methodName is the name of the public static method
     * @param optStreamName is the optional as-name of the stream, or null if unnamed
     */
    public MethodInvocationStream(String className, String methodName, String optStreamName)
    {
        super(optStreamName);
        this.className = className;
        this.methodName = methodName;
        parameterExpressions = new ArrayList<Expression>();
    }

    /**
     * Returns the name of the class providing the method.
     * @return class name
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Sets the name of the class providing the method.
     * @param className class name
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    /**
     * Returns the name of the static method to invoke in the from-clause.
     * @return method name
     */
    public String getMethodName()
    {
        return methodName;
    }

    /**
     * Sets the name of the static method to invoke in the from-clause.
     * @param methodName method name
     */
    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    /**
     * Returns a list of expressions that are parameters to the method.
     * @return list of parameter expressions
     */
    public List<Expression> getParameterExpressions()
    {
        return parameterExpressions;
    }

    /**
     * Sets a list of expressions that are parameters to the method.
     * @param parameterExpressions list of parameter expressions
     */
    public void setParameterExpressions(List<Expression> parameterExpressions)
    {
        this.parameterExpressions = parameterExpressions;
    }

    /**
     * Adds a parameters to the method invocation.
     * @param parameterExpression is the expression to add
     * @return stream
     */
    public MethodInvocationStream addParameter(Expression parameterExpression)
    {
        parameterExpressions.add(parameterExpression);
        return this;
    }

    public void toEPLStream(StringWriter writer)
    {
        writer.write("method:");
        writer.write(className);
        writer.write(".");
        writer.write(methodName);
        writer.write("(");
        for (Expression expr : parameterExpressions)
        {
            expr.toEPL(writer);
        }
        writer.write(")");
    }
}

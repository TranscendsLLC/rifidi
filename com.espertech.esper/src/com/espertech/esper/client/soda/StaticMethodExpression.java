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

/**
 * Static method call consists of a class name and method name.
 */
public class StaticMethodExpression extends ExpressionBase
{
    private String className;
    private String method;

    /**
     * Ctor.
     * @param className class name providing the static method
     * @param method method name
     * @param parameters an optiona array of parameters
     */
    public StaticMethodExpression(String className, String method, Object[] parameters)
    {
        this.className = className;
        this.method = method;
        
        for (int i = 0; i < parameters.length; i++)
        {
            if (parameters[i] instanceof Expression)
            {
                this.getChildren().add((Expression)parameters[i]);
            }
            else
            {
                this.getChildren().add(new ConstantExpression(parameters[i]));
            }
        }
    }

    /**
     * Ctor.
     * @param className class name providing the static method
     * @param method method name
     */
    public StaticMethodExpression(String className, String method)
    {
        this.className = className;
        this.method = method;
    }

    public void toEPL(StringWriter writer)
    {
        writer.write(className);
        writer.write('.');
        writer.write(method);
        writer.write('(');

        String delimiter = "";
        for (Expression child : this.getChildren())
        {
            writer.write(delimiter);
            child.toEPL(writer);
            delimiter = ", ";
        }

        writer.write(')');
    }

    /**
     * Returns the class name.
     * @return class name
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Sets the class name.
     * @param className class name
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    /**
     * Returns the method name.
     * @return method name
     */
    public String getMethod()
    {
        return method;
    }

    /**
     * Sets the method name.
     * @param method method name
     */
    public void setMethod(String method)
    {
        this.method = method;
    }
}

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
 * Instance-of expression checks if an expression returns a certain type.
 */
public class InstanceOfExpression extends ExpressionBase
{
    private String[] typeNames;

    /**
     * Ctor - for use to create an expression tree, without child expression.
     * @param typeNames is the fully-qualified class names or Java primitive type names or "string"
     */
    public InstanceOfExpression(String[] typeNames)
    {
        this.typeNames = typeNames;
    }

    /**
     * Ctor.
     * @param expressionToCheck provides values to check the type of
     * @param typeName is one fully-qualified class names or Java primitive type names or "string"
     * @param moreTypes is additional optional fully-qualified class names or Java primitive type names or "string"
     */
    public InstanceOfExpression(Expression expressionToCheck, String typeName, String ...moreTypes)
    {
        this.getChildren().add(expressionToCheck);
        if (moreTypes == null)
        {
            typeNames = new String[] {typeName};
        }
        else
        {
            typeNames = new String[moreTypes.length + 1];
            typeNames[0] = typeName;
            System.arraycopy(moreTypes, 0, this.typeNames, 1, moreTypes.length);
        }
    }

    /**
     * Renders the clause in textual representation.
     * @param writer to output to
     */
    public void toEPL(StringWriter writer)
    {
        writer.write("instanceof(");
        this.getChildren().get(0).toEPL(writer);
        writer.write(", ");

        String delimiter = "";
        for (String typeName : typeNames)
        {
            writer.write(delimiter);
            writer.write(typeName);
            delimiter = ", ";
        }
        writer.write(")");
    }

    /**
     * Returns the types to compare to.
     * @return list of types to compare to
     */
    public String[] getTypeNames()
    {
        return typeNames;
    }

    /**
     * Sets the types to compare to.
     * @param typeNames list of types to compare to
     */
    public void setTypeNames(String[] typeNames)
    {
        this.typeNames = typeNames;
    }
}

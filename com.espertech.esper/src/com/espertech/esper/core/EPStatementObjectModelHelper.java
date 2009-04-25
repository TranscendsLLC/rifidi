/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core;

import java.io.StringWriter;

/**
 * Helper methods for use by the statement object model.
 */
public class EPStatementObjectModelHelper
{
    /**
     * Renders a constant as an EPL.
     * @param writer to output to
     * @param constant to render
     */
    public static void renderEPL(StringWriter writer, Object constant)
    {
        if (constant == null)
        {
            writer.write("null");
            return;
        }

        if ((constant instanceof String) ||
            (constant instanceof Character))
        {
            writer.write('\"');
            writer.write(constant.toString());
            writer.write('\"');
        }
        else
        {
            writer.write(constant.toString());
        }
    }
}

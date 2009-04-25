/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.util;

import java.sql.Types;
import java.math.BigDecimal;

/**
 * Utility for mapping SQL types of {@link java.sql.Types} to Java classes.
 */
public class SQLTypeMapUtil
{

    /**
     * Mapping as defined by JDBC 3 Spec , page B-177, table B-1 JBDC Types mapped to Java Types.
     * @param sqlType to return Java class for
     * @param className is the classname that result metadata returns for a column
     * @return Java class for JDBC sql types
     */
    public static Class sqlTypeToClass(int sqlType, String className)
    {
        if ((sqlType == Types.BOOLEAN) ||
            (sqlType == Types.BIT))
        {
            return Boolean.class;
        }
        if ((sqlType == Types.CHAR) ||
            (sqlType == Types.VARCHAR) ||
            (sqlType == Types.LONGVARCHAR))
        {
            return String.class;
        }
        if ((sqlType == Types.CHAR) ||
            (sqlType == Types.VARCHAR))
        {
            return String.class;
        }
        if ((sqlType == Types.DOUBLE) ||
            (sqlType == Types.FLOAT))
        {
            return Double.class;
        }
        if (sqlType == Types.REAL)
        {
            return Float.class;
        }
        if (sqlType == Types.INTEGER)
        {
            return Integer.class;
        }
        if (sqlType == Types.BIGINT)
        {
            return Long.class;
        }
        if (sqlType == Types.TINYINT)
        {
            return Byte.class;
        }
        if (sqlType == Types.SMALLINT)
        {
            return Short.class;
        }
        if ((sqlType == Types.NUMERIC) ||
            (sqlType == Types.DECIMAL))
        {
            return BigDecimal.class;
        }
        if ((sqlType == Types.BINARY) ||
            (sqlType == Types.VARBINARY) ||
            (sqlType == Types.LONGVARBINARY))
        {
            return byte[].class;
        }
        if (sqlType == Types.DATE)
        {
            return java.sql.Date.class;
        }
        if (sqlType == Types.TIME)
        {
            return java.sql.Time.class;
        }
        if (sqlType == Types.TIMESTAMP)
        {
            return java.sql.Timestamp.class;
        }
        if (sqlType == Types.CLOB)
        {
            return java.sql.Clob.class;
        }
        if (sqlType == Types.BLOB)
        {
            return java.sql.Blob.class;
        }
        if (sqlType == Types.ARRAY)
        {
            return java.sql.Array.class;
        }
        if (sqlType == Types.STRUCT)
        {
            return java.sql.Struct.class;
        }
        if (sqlType == Types.REF)
        {
            return java.sql.Ref.class;
        }
        if (sqlType == Types.DATALINK)
        {
            return java.net.URL.class;
        }
        if ((sqlType == Types.JAVA_OBJECT) ||
            (sqlType == Types.DISTINCT))
        {
            if (className == null)
            {
                throw new IllegalArgumentException("No class supplied for sql type " + sqlType);
            }
            try
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                return Class.forName(className, true, cl);
            }
            catch (ClassNotFoundException e)
            {
                throw new IllegalArgumentException("Cannot load class for sql type " + sqlType + " and class " + className);
            }
        }
        throw new IllegalArgumentException("Cannot map java.sql.Types type " + sqlType);
    }
}

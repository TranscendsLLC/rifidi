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
 * Match-Until construct for use in pattern expressions.
 */
public class PatternMatchUntilExpr extends PatternExprBase
{
    private Integer low;
    private Integer high;

    /**
     * Ctor - for use to create a pattern expression tree, without pattern child expression.
     */
    public PatternMatchUntilExpr()
    {
    }

    /**
     * Ctor - for use when adding required child nodes later.
     * @param low - low number of matches, or null if no lower boundary
     * @param high - high number of matches, or null if no high boundary
     */
    public PatternMatchUntilExpr(Integer low, Integer high)
    {
        this.low = low;
        this.high = high;
    }

    /**
     * Ctor.
     * @param low - low number of matches, or null if no lower boundary
     * @param high - high number of matches, or null if no high boundary
     * @param match - the pattern expression that is sought to match repeatedly
     * @param until - the pattern expression that ends matching (optional, can be null)
     */
    public PatternMatchUntilExpr(Integer low, Integer high, PatternExpr match, PatternExpr until)
    {
        this.low = low;
        this.high = high;
        this.addChild(match);
        this.addChild(until);
    }

    /**
     * Returns the optional low endpoint for the repeat, or null if none supplied.
     * @return low endpoint
     */
    public Integer getLow()
    {
        return low;
    }

    /**
     * Sets the optional low endpoint for the repeat, or null if none supplied.
     * @param low - low endpoint to set
     */
    public void setLow(Integer low)
    {
        this.low = low;
    }

    /**
     * Returns the optional high endpoint for the repeat, or null if none supplied.
     * @return high endpoint
     */
    public Integer getHigh()
    {
        return high;
    }

    /**
     * Sets the optional high endpoint for the repeat, or null if none supplied.
     * @param high - high endpoint to set
     */
    public void setHigh(Integer high)
    {
        this.high = high;
    }

    public void toEPL(StringWriter writer)
    {
        if ((low != null) || (high != null))
        {
            writer.write("[");
            if ((low != null) && (high != null))
            {
                if (low.equals(high))
                {
                    writer.write(Integer.toString(low));
                }
                else
                {
                    writer.write(Integer.toString(low));
                    writer.write("..");
                    writer.write(Integer.toString(high));
                }
            }
            else if (low != null)
            {
                writer.write(Integer.toString(low));
                writer.write("..");
            }
            else
            {
                writer.write("..");
                writer.write(Integer.toString(high));
            }
            writer.write("] ");
        }

        writer.write('(');
        this.getChildren().get(0).toEPL(writer);
        writer.write(')');

        if (this.getChildren().size() > 1)
        {
            writer.write(" until ");
            writer.write('(');
            this.getChildren().get(1).toEPL(writer);
            writer.write(')');
        }
    }
}

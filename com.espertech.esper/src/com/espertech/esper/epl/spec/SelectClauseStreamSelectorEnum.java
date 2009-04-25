/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.spec;

import com.espertech.esper.client.soda.StreamSelector;

/**
 * Enumeration for representing select-clause selection of the remove stream or the insert stream, or both.
 */
public enum SelectClauseStreamSelectorEnum
{
    /**
     * Indicates selection of the remove stream only.
     */
    RSTREAM_ONLY,

    /**
     * Indicates selection of the insert stream only.
     */
    ISTREAM_ONLY,

    /**
     * Indicates selection of both the insert and the remove stream.
     */
    RSTREAM_ISTREAM_BOTH;

    /**
     * Maps the SODA-selector to the internal representation
     * @param selector is the SODA-selector to map
     * @return internal stream selector
     */
    public static SelectClauseStreamSelectorEnum mapFromSODA(StreamSelector selector)
    {
        if (selector == StreamSelector.ISTREAM_ONLY)
        {
            return SelectClauseStreamSelectorEnum.ISTREAM_ONLY;
        }
        else if (selector == StreamSelector.RSTREAM_ONLY)
        {
            return SelectClauseStreamSelectorEnum.RSTREAM_ONLY;
        }
        else if (selector == StreamSelector.RSTREAM_ISTREAM_BOTH)
        {
            return SelectClauseStreamSelectorEnum.RSTREAM_ISTREAM_BOTH;
        }
        else
        {
            throw new IllegalArgumentException("Invalid selector '" + selector + "' encountered");
        }
    }

    /**
     * Maps the internal stream selector to the SODA-representation
     * @param selector is the internal selector to map
     * @return SODA stream selector
     */
    public static StreamSelector mapFromSODA(SelectClauseStreamSelectorEnum selector)
    {
        if (selector == SelectClauseStreamSelectorEnum.ISTREAM_ONLY)
        {
            return StreamSelector.ISTREAM_ONLY;
        }
        else if (selector == SelectClauseStreamSelectorEnum.RSTREAM_ONLY)
        {
            return StreamSelector.RSTREAM_ONLY;
        }
        else if (selector == SelectClauseStreamSelectorEnum.RSTREAM_ISTREAM_BOTH)
        {
            return StreamSelector.RSTREAM_ISTREAM_BOTH;
        }
        else
        {
            throw new IllegalArgumentException("Invalid selector '" + selector + "' encountered");
        }
    }
}

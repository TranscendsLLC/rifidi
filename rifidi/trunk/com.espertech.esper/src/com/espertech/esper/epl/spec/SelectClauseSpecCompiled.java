/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.spec;

import com.espertech.esper.util.MetaDefItem;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * Encapsulates the parsed select expressions in a select-clause in an EPL statement.
 */
public class SelectClauseSpecCompiled implements MetaDefItem, Serializable
{
	private List<SelectClauseElementCompiled> selectClauseElements;
    private static final long serialVersionUID = 5759170079421795146L;

    /**
     * Ctor.
     */
    public SelectClauseSpecCompiled()
	{
		selectClauseElements = new ArrayList<SelectClauseElementCompiled>();
    }

    /**
     * Ctor.
     * @param selectList for a populates list of select expressions
     */
    public SelectClauseSpecCompiled(List<SelectClauseElementCompiled> selectList)
	{
        this.selectClauseElements = selectList;
	}

    /**
     * Adds an select expression within the select clause.
     * @param element is the expression to add
     */
    public void add(SelectClauseElementCompiled element)
	{
		selectClauseElements.add(element);
	}

    /**
     * Returns the list of select expressions.
     * @return list of expressions
     */
    public List<SelectClauseElementCompiled> getSelectExprList()
	{
		return selectClauseElements;
	}

    /**
     * Returns true if the select clause contains at least one wildcard.
     * @return true if clause contains wildcard, false if not
     */
    public boolean isUsingWildcard()
    {
        for (SelectClauseElementCompiled element : selectClauseElements)
        {
            if (element instanceof SelectClauseElementWildcard)
            {
                return true;
            }
        }
        return false;
    }
}

/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view;

/**
 * Tag interface for data window views that express a batch expiry policy.
 * <p>
 * Such data windows allow iteration through the currently batched events,
 * and such data windows post insert stream events only when batching conditions have been met and
 * the batch is released.
 */
public interface BatchingDataWindowView extends DataWindowView
{
}

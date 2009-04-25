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
 * Views that require initialization after view instantiation and after view hook-up with the parent view
 * can impleeent this interface and get invoked to initialize.
 */
public interface InitializableView
{
    /**
     * Initializes a view.
     */
    public void initialize();
}

/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view;

import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.schedule.TimeProvider;

/**
 * Interface for views that require validation against stream event types.
 */
public interface ValidatedView
{
    /**
     * Validate the view.
     * @param streamTypeService supplies the types of streams against which to validate
     * @param methodResolutionService for resolving imports and classes and methods
     * @param timeProvider for providing current time
     * @param variableService for access to variables
     * @throws ExprValidationException is thrown to indicate an exception in validating the view
     */
    public void validate(StreamTypeService streamTypeService,
                         MethodResolutionService methodResolutionService,
                         TimeProvider timeProvider,
                         VariableService variableService) throws ExprValidationException;
}

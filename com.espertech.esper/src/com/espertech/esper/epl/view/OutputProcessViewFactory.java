/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.view;

import com.espertech.esper.core.InternalEventRouter;
import com.espertech.esper.core.StatementContext;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.OutputLimitSpec;
import com.espertech.esper.epl.spec.SelectClauseStreamSelectorEnum;
import com.espertech.esper.epl.spec.StatementSpecCompiled;
import com.espertech.esper.epl.spec.OutputLimitLimitType;

/**
 * Factory for output processing views.
 */
public class OutputProcessViewFactory
{
    /**
     * Creates an output processor view depending on the presence of output limiting requirements.
     * @param resultSetProcessor is the processing for select-clause and grouping
     * @param statementContext is the statement-level services
     * @param internalEventRouter service for routing events internally
     * @param statementSpec the statement specification
     * @return output processing view
     * @throws ExprValidationException to indicate
     */
    public static OutputProcessView makeView(ResultSetProcessor resultSetProcessor,
                          StatementSpecCompiled statementSpec,
                          StatementContext statementContext,
                          InternalEventRouter internalEventRouter)
            throws ExprValidationException
    {
        boolean isRouted = false;
        if (statementSpec.getInsertIntoDesc() != null)
        {
            isRouted = true;
        }

        OutputStrategy outputStrategy;
        if ((statementSpec.getInsertIntoDesc() != null) || (statementSpec.getSelectStreamSelectorEnum() == SelectClauseStreamSelectorEnum.RSTREAM_ONLY))
        {
            boolean isRouteRStream = false;
            if (statementSpec.getInsertIntoDesc() != null)
            {
                isRouteRStream = !statementSpec.getInsertIntoDesc().isIStream();
            }

            outputStrategy = new OutputStrategyPostProcess(isRouted, isRouteRStream, statementSpec.getSelectStreamSelectorEnum(), internalEventRouter, statementContext.getEpStatementHandle());
        }
        else
        {
            outputStrategy = new OutputStrategySimple();
        }

        // Do we need to enforce an output policy?
        int streamCount = statementSpec.getStreamSpecs().size();
        OutputLimitSpec outputLimitSpec = statementSpec.getOutputLimitSpec();

        try
        {
            if (outputLimitSpec != null)
            {
                if (outputLimitSpec.getDisplayLimit() == OutputLimitLimitType.SNAPSHOT)
                {
                    return new OutputProcessViewSnapshot(resultSetProcessor, outputStrategy, isRouted, streamCount, outputLimitSpec, statementContext);
                }
                else
                {
                    return new OutputProcessViewPolicy(resultSetProcessor, outputStrategy, isRouted, streamCount, outputLimitSpec, statementContext);
                }
            }
            return new OutputProcessViewDirect(resultSetProcessor, outputStrategy, isRouted, statementContext.getStatementResultService());
        }
        catch (RuntimeException ex)
        {
            throw new ExprValidationException("Error in the output rate limiting clause: " + ex.getMessage(), ex);
        }
    }
}

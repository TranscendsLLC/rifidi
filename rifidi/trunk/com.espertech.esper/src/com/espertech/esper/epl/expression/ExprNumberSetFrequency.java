package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.type.FrequencyParameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Expression for use within crontab to specify a frequency.
 */
public class ExprNumberSetFrequency extends ExprNode
{
    private static final Log log = LogFactory.getLog(ExprNumberSetFrequency.class);
    
    public String toExpressionString()
    {
        return "*/" + this.getChildNodes().get(0); 
    }

    public boolean isConstantResult()
    {
        return this.getChildNodes().get(0).isConstantResult();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprNumberSetFrequency))
        {
            return false;
        }
        return true;
    }

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        Class type = this.getChildNodes().get(0).getType();
        if (!(JavaClassHelper.isNumericNonFP(type)))
        {
            throw new ExprValidationException("Frequency operator requires an integer-type parameter");
        }
    }

    public Class getType()
    {
        return FrequencyParameter.class;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        Object value = this.getChildNodes().get(0).evaluate(eventsPerStream, isNewData);
        if (value == null)
        {
            log.warn("Null value returned for frequency parameter");
            return new FrequencyParameter(Integer.MAX_VALUE);
        }
        else
        {
            int intValue = ((Number) value).intValue();
            return new FrequencyParameter(intValue);
        }
    }
}

package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.type.*;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Expression for use within crontab to specify a list of values.
 */
public class ExprNumberSetList extends ExprNode
{
    private static final Log log = LogFactory.getLog(ExprNumberSetList.class);

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        String delimiter = "";

        buffer.append('[');
        Iterator<ExprNode> it = this.getChildNodes().iterator();
        do
        {
            ExprNode expr = it.next();
            buffer.append(delimiter);
            buffer.append(expr.toExpressionString());
            delimiter = ",";
        }
        while (it.hasNext());
        buffer.append(']');

        return buffer.toString();
    }

    public boolean isConstantResult()
    {
        for (ExprNode child : this.getChildNodes())
        {
            if (!child.isConstantResult())
            {
                return false;
            }
        }
        return true;
    }

    public boolean equalsNode(ExprNode node)
    {
        return (node instanceof ExprNumberSetList);
    }

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        // all nodes must either be int, frequency or range
        for (ExprNode child : this.getChildNodes())
        {
            Class type = child.getType();
            if ((type == FrequencyParameter.class) || (type == RangeParameter.class))
            {
                continue;
            }
            if (!(JavaClassHelper.isNumericNonFP(type)))
            {
                throw new ExprValidationException("Frequency operator requires an integer-type parameter");
            }
        }
        
    }

    public Class getType()
    {
        return ListParameter.class;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        List<NumberSetParameter> parameters = new ArrayList<NumberSetParameter>();
        for (ExprNode child : this.getChildNodes())
        {
            Object value = child.evaluate(eventsPerStream, isNewData);
            if (value == null)
            {
                log.info("Null value returned for lower bounds value in list parameter, skipping parameter");
                continue;
            }
            if ((value instanceof FrequencyParameter) || (value instanceof RangeParameter))
            {
                parameters.add((NumberSetParameter) value);
                continue;
            }
            
            int intValue = ((Number) value).intValue();
            parameters.add(new IntParameter(intValue));
        }
        if (parameters.isEmpty())
        {
            log.warn("Empty list of values in list parameter, using upper bounds");
            parameters.add(new IntParameter(Integer.MAX_VALUE));
        }
        return new ListParameter(parameters);
    }
}

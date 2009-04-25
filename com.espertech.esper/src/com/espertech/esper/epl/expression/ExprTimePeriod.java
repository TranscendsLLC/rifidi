package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.util.JavaClassHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Expression representing a time period.
 * <p>
 * Child nodes to this expression carry the actual parts and must return a numeric value.
 */
public class ExprTimePeriod extends ExprNode
{
    private static final Log log = LogFactory.getLog(ExprTimePeriod.class);

    private final boolean hasDay;
    private final boolean hasHour;
    private final boolean hasMinute;
    private final boolean hasSecond;
    private final boolean hasMillisecond;
    private boolean hasVariable;

    /**
     * Ctor.
     * @param hasDay true if the expression has that part, false if not
     * @param hasHour true if the expression has that part, false if not
     * @param hasMinute true if the expression has that part, false if not
     * @param hasSecond true if the expression has that part, false if not
     * @param hasMillisecond true if the expression has that part, false if not
     */
    public ExprTimePeriod(boolean hasDay, boolean hasHour, boolean hasMinute, boolean hasSecond, boolean hasMillisecond)
    {
        this.hasDay = hasDay;
        this.hasHour = hasHour;
        this.hasMinute = hasMinute;
        this.hasSecond = hasSecond;
        this.hasMillisecond = hasMillisecond;
    }

    /**
     * Indicator whether the time period has a day part child expression.
     * @return true for part present, false for not present
     */
    public boolean isHasDay()
    {
        return hasDay;
    }

    /**
     * Indicator whether the time period has a hour part child expression.
     * @return true for part present, false for not present
     */
    public boolean isHasHour()
    {
        return hasHour;
    }

    /**
     * Indicator whether the time period has a minute part child expression.
     * @return true for part present, false for not present
     */
    public boolean isHasMinute()
    {
        return hasMinute;
    }

    /**
     * Indicator whether the time period has a second part child expression.
     * @return true for part present, false for not present
     */
    public boolean isHasSecond()
    {
        return hasSecond;
    }

    /**
     * Indicator whether the time period has a millisecond part child expression.
     * @return true for part present, false for not present
     */
    public boolean isHasMillisecond()
    {
        return hasMillisecond;
    }

    /**
     * Indicator whether the time period has a variable in any of the child expressions.
     * @return true for variable present, false for not present
     */
    public boolean hasVariable()
    {
        return hasVariable;
    }

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        for (ExprNode childNode : this.getChildNodes())
        {
            validate(childNode);
        }
    }

    private void validate(ExprNode expression) throws ExprValidationException
    {
        if (expression == null)
        {
            return;
        }
        Class returnType = expression.getType();
        if (!JavaClassHelper.isNumeric(returnType))
        {
            throw new ExprValidationException("Time period expression requires a numeric parameter type");
        }
        if (expression instanceof ExprVariableNode)
        {
            hasVariable = true;
        }
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        double seconds = 0;
        int exprCtr = 0;

        if (hasDay)
        {
            Double result = eval(this.getChildNodes().get(exprCtr++), eventsPerStream);
            if (result == null)
            {
                return null;
            }
            seconds += result * 24 * 60 * 60;
        }
        if (hasHour)
        {
            Double result = eval(this.getChildNodes().get(exprCtr++), eventsPerStream);
            if (result == null)
            {
                return null;
            }
            seconds += result * 60 * 60;
        }
        if (hasMinute)
        {
            Double result = eval(this.getChildNodes().get(exprCtr++), eventsPerStream);
            if (result == null)
            {
                return null;
            }
            seconds += result * 60;
        }
        if (hasSecond)
        {
            Double result = eval(this.getChildNodes().get(exprCtr++), eventsPerStream);
            if (result == null)
            {
                return null;
            }
            seconds += result;
        }
        if (hasMillisecond)
        {
            Double result = eval(this.getChildNodes().get(exprCtr), eventsPerStream);
            if (result == null)
            {
                return null;
            }
            if (result != 0)
            {
                seconds += result / 1000d;
            }
        }
        return seconds;
    }

    public Class getType()
    {
        return Double.class;
    }

    public boolean isConstantResult()
    {
        for (ExprNode child : getChildNodes())
        {
            if (!child.isConstantResult())
            {
                return false;
            }
        }
        return true;
    }

    public String toExpressionString()
    {
        StringBuffer buf = new StringBuffer();
        int exprCtr = 0;
        if (hasDay)
        {
            buf.append(getChildNodes().get(exprCtr++).toExpressionString());
            buf.append(" days ");
        }
        if (hasHour)
        {
            buf.append(getChildNodes().get(exprCtr++).toExpressionString());
            buf.append(" hours ");
        }
        if (hasMinute)
        {
            buf.append(getChildNodes().get(exprCtr++).toExpressionString());
            buf.append(" minutes ");
        }
        if (hasSecond)
        {
            buf.append(getChildNodes().get(exprCtr++).toExpressionString());
            buf.append(" seconds ");
        }
        if (hasMillisecond)
        {
            buf.append(getChildNodes().get(exprCtr).toExpressionString());
            buf.append(" milliseconds ");
        }
        return buf.toString();
    }
    
    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprTimePeriod))
        {
            return false;
        }
        ExprTimePeriod other = (ExprTimePeriod) node;

        if (hasDay != other.hasDay)
        {
            return false;
        }
        if (hasHour != other.hasHour)
        {
            return false;
        }
        if (hasMinute != other.hasMinute)
        {
            return false;
        }
        if (hasSecond != other.hasSecond)
        {
            return false;
        }
        return (hasMillisecond == other.hasMillisecond);
    }

    private Double eval(ExprNode expr, EventBean[] events)
    {
        Object value = expr.evaluate(events, true);
        if (value == null)
        {
            log.warn("Time period expression returned a null value for expression '" + expr.toExpressionString() + "'");
            return null;
        }
        if (value instanceof BigDecimal)
        {
            return ((BigDecimal) value).doubleValue();
        }
        if (value instanceof BigInteger)
        {
            return ((BigInteger) value).doubleValue();
        }
        return ((Number) value).doubleValue();
    }
}

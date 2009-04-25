package com.espertech.esper.filter;

import com.espertech.esper.util.SimpleNumberCoercer;
import com.espertech.esper.pattern.MatchedEventMap;
import com.espertech.esper.client.EventBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class represents a filter parameter containing a reference to another event's property
 * in the event pattern result, for use to describe a filter parameter in a {@link com.espertech.esper.filter.FilterSpecCompiled} filter specification.
 */
public final class FilterSpecParamEventPropIndexed extends FilterSpecParam
{
    private static final Log log = LogFactory.getLog(FilterSpecParamEventPropIndexed.class);
    private final String resultEventAsName;
    private final int resultEventIndex;
    private final String resultEventProperty;
    private final boolean isMustCoerce;
    private final SimpleNumberCoercer numberCoercer;
    private final Class coercionType;

    /**
     * Constructor.
     * @param propertyName is the event property name
     * @param filterOperator is the type of compare
     * @param resultEventAsName is the name of the result event from which to get a property value to compare
     * @param resultEventProperty is the name of the property to get from the named result event
     * @param isMustCoerce indicates on whether numeric coercion must be performed
     * @param coercionType indicates the numeric coercion type to use
     * @param numberCoercer interface to use to perform coercion
     * @param resultEventIndex index
     * @throws IllegalArgumentException if an operator was supplied that does not take a single constant value
     */
    public FilterSpecParamEventPropIndexed(String propertyName, FilterOperator filterOperator, String resultEventAsName,
                                    int resultEventIndex, String resultEventProperty, boolean isMustCoerce,
                                    SimpleNumberCoercer numberCoercer, Class coercionType)
        throws IllegalArgumentException
    {
        super(propertyName, filterOperator);
        this.resultEventAsName = resultEventAsName;
        this.resultEventIndex = resultEventIndex;
        this.resultEventProperty = resultEventProperty;
        this.isMustCoerce = isMustCoerce;
        this.numberCoercer = numberCoercer;
        this.coercionType = coercionType;

        if (filterOperator.isRangeOperator())
        {
            throw new IllegalArgumentException("Illegal filter operator " + filterOperator + " supplied to " +
                    "event property filter parameter");
        }
    }

    /**
     * Returns true if numeric coercion is required, or false if not
     * @return true to coerce at runtime
     */
    public boolean isMustCoerce()
    {
        return isMustCoerce;
    }

    /**
     * Returns the numeric coercion type.
     * @return type to coerce to
     */
    public Class getCoercionType()
    {
        return coercionType;
    }

    /**
     * Returns tag for result event.
     * @return tag
     */
    public String getResultEventAsName()
    {
        return resultEventAsName;
    }

    /**
     * Returns the property of the result event.
     * @return property name
     */
    public String getResultEventProperty()
    {
        return resultEventProperty;
    }

    public Object getFilterValue(MatchedEventMap matchedEvents)
    {
        EventBean[] events = (EventBean[]) matchedEvents.getMatchingEventAsObject(resultEventAsName);

        Object value = null;
        if (events == null)
        {
            log.warn("Matching events for tag '" + resultEventAsName + "' returned a null result, using null value in filter criteria");
        }
        else if (resultEventIndex > (events.length - 1))
        {
            log.warn("Matching events for tag '" + resultEventAsName + "' returned no result for index " + resultEventIndex + " at array length " + events.length + ", using null value in filter criteria");
        }
        else
        {
            value = events[resultEventIndex].get(resultEventProperty);
        }

        // Coerce if necessary
        if (isMustCoerce)
        {
            value = numberCoercer.coerceBoxed((Number) value);
        }
        return value;
    }

    /**
     * Returns the index.
     * @return index
     */
    public int getResultEventIndex()
    {
        return resultEventIndex;
    }

    public int getFilterHash()
    {
        return resultEventProperty.hashCode();
    }

    public final String toString()
    {
        return super.toString() +
                " resultEventAsName=" + resultEventAsName +
                " resultEventProperty=" + resultEventProperty;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof FilterSpecParamEventPropIndexed))
        {
            return false;
        }

        FilterSpecParamEventPropIndexed other = (FilterSpecParamEventPropIndexed) obj;
        if (!super.equals(other))
        {
            return false;
        }

        if ((!this.resultEventAsName.equals(other.resultEventAsName)) ||
            (!this.resultEventProperty.equals(other.resultEventProperty)) ||
            (this.resultEventIndex != other.resultEventIndex))
        {
            return false;
        }
        return true;
    }

    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + resultEventProperty.hashCode();
        return result;
    }
}

package com.espertech.esper.event.bean;

import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.PropertyAccessException;

/**
 * Shortcut-evaluator for use with POJO-backed events only.
 */
public interface BeanEventPropertyGetter extends EventPropertyGetter
{
    /**
     * Returns the property as an object.
     * @param object to evaluate
     * @return property of object
     * @throws PropertyAccessException if access failed
     */
    public Object getBeanProp(Object object) throws PropertyAccessException;

    /**
     * Returns true if the dynamic property exists.
     * @param object to evaluate
     * @return indicator if property exists
     */
    public boolean isBeanExistsProperty(Object object);
}

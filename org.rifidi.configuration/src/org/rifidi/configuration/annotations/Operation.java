
package org.rifidi.configuration.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be available as a JMX operation. The marked method MUST be
 * threadsafe.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Operation {
	/**
	 * Human readable description of the operation.
	 * 
	 * @return
	 */
	String description();
}

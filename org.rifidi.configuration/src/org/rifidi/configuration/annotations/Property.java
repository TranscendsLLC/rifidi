/**
 * 
 */
package org.rifidi.configuration.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a getter or setter for turning it into a property (follow bean
 * conventions.)
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Property {
	/**
	 * A human readable description of the property.
	 */
	String description();

	/**
	 * A human readable name of the property.
	 */
	String name();

	/**
	 * True if the property is writable.
	 */
	boolean writable();
}

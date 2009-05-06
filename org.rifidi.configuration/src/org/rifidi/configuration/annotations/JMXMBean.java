/**
 * 
 */
package org.rifidi.configuration.annotations;
//TODO: Comments
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class to be managable by JMX.
 * 
 * @author Jochen Mader - jochen@pramari.com
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface JMXMBean {
	
}

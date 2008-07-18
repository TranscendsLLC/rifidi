package org.rifidi.edge.core.readerplugin.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to describe the name and functionality of a command. This
 * annotation will be available at runtime.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandDesc {
	/**
	 * Name of the command
	 * 
	 * @return the name of the command
	 */
	public String name();

	/**
	 * Groups this command belongs to
	 * 
	 * @return list of groups this command belongs to
	 */
	public String[] groups() default {};
}

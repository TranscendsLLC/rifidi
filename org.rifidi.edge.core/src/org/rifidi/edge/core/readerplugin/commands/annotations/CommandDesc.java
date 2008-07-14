package org.rifidi.edge.core.readerplugin.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandDesc {
	public String name();
	public String[] groups() default {} ;
}

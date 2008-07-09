package org.rifidi.edge.adminclient.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	String name();
	String[] arguments() default {} ;
}

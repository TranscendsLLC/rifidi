package org.rifidi.edge.testclient.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	String name();
	String[] arguments() default {} ;
}

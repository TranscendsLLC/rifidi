package org.rifidi.edge.core.readerplugin.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Widget {
	WidgetType type();
	String elementName();
	String defaultValue();
	String displayName();
	boolean editable() default true;
	float max() default Integer.MAX_VALUE;
	float min() default Integer.MIN_VALUE;
	String regex() default "";
	String[] possibleValues() default {};
}

package org.rifidi.edge.core.readerplugin.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Widget {
	public static final String TYPE="Type";
	public static final String ELEMENT_NAME="ElementName";
	public static final String DEFAULT_VALUE="DefaultValue";
	public static final String DISPLAY_NAME="DisplayName";
	public static final String EDITABLE="Editable";
	public static final String MAX="Max";
	public static final String MIN="Min";
	public static final String DECIMAL_PLACES = "DecimalPlaces";
	public static final String REGEX="Regex";
	public static final String POSSIBLE_VALUES="PossibleValues";
	public static final String POSSIBLE_VALUE = "value";
	
	
	WidgetType type();
	String elementName();
	String defaultValue();
	String displayName();
	boolean editable() default true;
	double max() default Integer.MAX_VALUE;
	double min() default Integer.MIN_VALUE;
	int decimalPlaces() default 0;
	String regex() default "";
	//String[] possibleValues() default {};
	String enumClass() default "";
}

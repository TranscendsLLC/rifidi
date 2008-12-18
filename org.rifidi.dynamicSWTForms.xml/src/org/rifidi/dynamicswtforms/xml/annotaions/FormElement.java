package org.rifidi.dynamicswtforms.xml.annotaions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.rifidi.dynamicswtforms.xml.constants.FormElementType;

/**
 * The FormElement is an annotation that can be used within the Form annotation.
 * Each FormElement corresponds to a label and a widget inside the form. The
 * data inside this annotation which do not have a default value are used by all
 * FormElement types.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
@Target( {})
@Retention(RetentionPolicy.RUNTIME)
public @interface FormElement {
	/**
	 * The type of the formElement
	 * 
	 * @return
	 */
	FormElementType type();

	/**
	 * The name of the formElement
	 * 
	 * @return
	 */
	String elementName();

	/**
	 * The Element's default value
	 * 
	 * @return
	 */
	String defaultValue();

	/**
	 * The Element's display name, which is used in the label on the widget
	 * 
	 * @return
	 */
	String displayName();

	/**
	 * Whether or not the element is editable. Non-editable elements will be
	 * greyed out
	 * 
	 * @return
	 */
	boolean editable() default true;

	/**
	 * Used for String Element types. Contains a java regular expression string
	 * that the input will be validated against
	 * 
	 * @return
	 */
	String regex() default "";

	/**
	 * Used for Integer and Float FormElement types. The maximum value for the
	 * number
	 * 
	 * @return
	 */
	int max() default Integer.MAX_VALUE;

	/**
	 * Used for Integer and Float FormElement types. The minimum value for the
	 * number
	 * 
	 * @return
	 */
	int min() default Integer.MIN_VALUE;

	/**
	 * Used for Float FormElement types. The number of digits following a
	 * decimal point
	 * 
	 * @return
	 */
	int decimalPlaces() default 0;

	/**
	 * Used for choice FormElement types. The values in this enum will be
	 * displayed as choices
	 * 
	 * @return
	 */
	String enumClass() default "";
}

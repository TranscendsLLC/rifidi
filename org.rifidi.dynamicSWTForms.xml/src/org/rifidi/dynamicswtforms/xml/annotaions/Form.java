package org.rifidi.dynamicswtforms.xml.annotaions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class is an annotation that can be used above classes who wish to
 * contribute a DynamicSWTForm XML to a UI.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Form {
	/**
	 * The name of the form
	 * @return
	 */
	String name();

	/**
	 * A list of FormElements that make up this form.
	 * @return
	 */
	FormElement[] formElements();
}

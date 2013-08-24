/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.configuration;

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
 * @author Kyle Neumeier - kyle@pramari.com
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
	String displayName();

	/**
	 * True if the property is writable.
	 */
	boolean writable();

	/**
	 * A category that this property belongs to
	 */
	String category() default "Miscellaneous";

	/**
	 * The class that this type belongs to. Default is String
	 */
	PropertyType type() default PropertyType.PT_STRING;

	/**
	 * The minimum value that this property has. It must be of the same type as
	 * the type and the type must implement Comparable
	 * 
	 */
	String minValue() default "";

	/**
	 * The maximum value that this property has. It must be of the same type as
	 * the type and the type must implement Comparable
	 * 
	 */
	String maxValue() default "";

	/**
	 * The default value of the property
	 */
	String defaultValue() default "";

	/**
	 * This value is used to help order the property on the UI. A value of -1 is
	 * intended to indicated that the property should be grouped at the end. For
	 * all values greater than -1, lower values indicate greater rank
	 * 
	 */
	float orderValue() default -1;
}

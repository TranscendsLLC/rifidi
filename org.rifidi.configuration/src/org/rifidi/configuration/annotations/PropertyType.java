package org.rifidi.configuration.annotations;

import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

/**
 * This enum represents the possible types for a property type
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public enum PropertyType {
	PT_STRING, PT_INTEGER, PT_BOOLEAN, PT_LONG;

	/**
	 * Returns an OpenType equivalent of a PropertyType for use in
	 * MBeanAttributeInfo, etc
	 * 
	 * @param type
	 * @return
	 */
	public static OpenType<?> getOpenType(PropertyType type) {
		switch (type) {
		case PT_STRING:
			return SimpleType.STRING;
		case PT_INTEGER:
			return SimpleType.INTEGER;
		case PT_BOOLEAN:
			return SimpleType.BOOLEAN;
		case PT_LONG:
			return SimpleType.LONG;
		default:
			return null;
		}
	}

	/**
	 * Returns a native java class equivalent of a PropertyType
	 * 
	 * @param type
	 * @return
	 */
	public static Class<?> getClassFromType(PropertyType type) {
		switch (type) {
		case PT_STRING:
			return String.class;
		case PT_INTEGER:
			return Integer.class;
		case PT_BOOLEAN:
			return Boolean.class;
		case PT_LONG:
			return Long.class;
		default:
			return null;
		}
	}

	/**
	 * Converts an String to an object whose type is that specified in the
	 * PropertyType
	 * 
	 * @param value
	 *            The string to convert
	 * @param toType
	 *            The type to convert to
	 * @return An object whose type is that specified by toType
	 */
	public static Object convert(String value, PropertyType toType) {
		switch (toType) {
		case PT_STRING:
			return value;
		case PT_INTEGER:
			return Integer.parseInt(value);
		case PT_BOOLEAN:
			return Boolean.parseBoolean(value);
		case PT_LONG:
			return Long.parseLong(value);
		}
		return null;
	}
}

package org.rifidi.dynamicswtforms.xml.processor;

import java.util.List;

import org.rifidi.dynamicswtforms.xml.exceptions.DynamicSWTFormAnnotationException;
import org.w3c.dom.Document;

/**
 * These are helper methods for converting a class annotated with the Form
 * annotation to an XML document that can be sent to a UI
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface DynamicSWTFormXMLProcessor {

	/**
	 * This method takes in a single class and converts it to a form xml
	 * 
	 * @param clazz
	 *            The class that is annotated with the Form Annotation
	 * @return An XML document that contains the XML to be sent to the UI
	 * @throws DynamicSWTFormAnnotationException
	 */
	public Document processAnnotation(Class<?> clazz)
			throws DynamicSWTFormAnnotationException;

	/**
	 * This method takes in a list of classes, each of which is annotated with
	 * the Form Annotation. It returns an XML element that may contain more than
	 * one Form XML element
	 * 
	 * @param rootName
	 *            The name of the root element
	 * @param classes
	 *            The list of classes, each of which is annotated with a Form
	 *            Annotatation
	 * @return an XML document that contains (possibly) more than one Form
	 * @throws DynamicSWTFormAnnotationException
	 */
	public Document processAnnotation(String rootName, List<Class<?>> classes)
			throws DynamicSWTFormAnnotationException;

}

package org.rifidi.edge.core.readerplugin.annotations.service;

import java.util.List;

import org.rifidi.edge.core.exceptions.RifidiWidgetAnnotationException;
import org.w3c.dom.Document;

public interface WidgetAnnotationProcessorService {
	
	public Document processAnnotation(Class<?> clazz) throws RifidiWidgetAnnotationException;
	
	public Document processAnnotation(String rootName, List<Class<?>> classes) throws RifidiWidgetAnnotationException;

}

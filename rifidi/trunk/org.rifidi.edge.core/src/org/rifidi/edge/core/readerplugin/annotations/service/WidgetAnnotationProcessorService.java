package org.rifidi.edge.core.readerplugin.annotations.service;

import org.rifidi.edge.core.exceptions.RifidiWidgetAnnotationException;
import org.w3c.dom.Document;

public interface WidgetAnnotationProcessorService {
	
	public Document processAnnotation(Class<?> clazz) throws RifidiWidgetAnnotationException;

}

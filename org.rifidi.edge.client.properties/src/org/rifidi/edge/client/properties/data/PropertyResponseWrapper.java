package org.rifidi.edge.client.properties.data;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class PropertyResponseWrapper {

	Document doc;
	//private Log logger = LogFactory.getLog(PropertyResponseWrapper.class);

	public PropertyResponseWrapper(String xml) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		//logger.debug(xml);
		Reader reader = new StringReader(xml);
		doc = builder.build(reader);

	}

	public PropertyResponse getPropertyResponse(String propertyName) {
		Element e = doc.getRootElement().getChild(propertyName);
		return new PropertyResponse(e);
	}
}

package org.rifidi.edge.common.utilities.jaxb;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * This Utility converts a given Object into a XML 
 * 
 * @author andreas
 *
 */
public class ObjectToXML {

	public static String convertToXML(Object o) throws JAXBException {
		StringWriter stringWriter = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(o.getClass());
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(o, stringWriter);
		return stringWriter.toString();
	}
}

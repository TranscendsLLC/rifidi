package org.rifidi.edge.readerplugin.alien.messages;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.core.readerplugin.messages.Message;

@XmlRootElement
public class GenericAlienMessage implements Message {
	private Marshaller marshaller;
	
	private Property property;
	
	public GenericAlienMessage(Property property){
		this.property = property;
	}
	
	
	@Override
	public String toXML() {
		if (marshaller == null) {
			try {
				JAXBContext context = JAXBContext.newInstance(this.getClass());
				marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			} catch (JAXBException e) {
				e.printStackTrace();
				return null;
			}
		}
		StringWriter writer = new StringWriter();
		try {
			marshaller.marshal(this, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
		return writer.toString();
	}

	/**
	 * @return the property
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @param property the property to set
	 */
	public void setProperty(Property property) {
		this.property = property;
	}
}

package org.rifidi.edge.readerplugin.alien.messages;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.rifidi.edge.core.api.readerplugin.messages.Message;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class AlienAllInfoMessage implements Message{
	private Marshaller marshaller;
	
	Map<String, String> general = new HashMap<String, String>();
	Map<String, String> network = new HashMap<String, String>();
	Map<String, String> time = new HashMap<String, String>();
	Map<String, String> tagList = new HashMap<String, String>();
	Map<String, String> acquire = new HashMap<String, String>();
	Map<String, String> io = new HashMap<String, String>();
	Map<String, String> autoMode = new HashMap<String, String>();
	Map<String, String> notify = new HashMap<String, String>();
	Map<String, String> program = new HashMap<String, String>();
	Map<String, String> experimental = new HashMap<String, String>();
	
	/**
	 * @param allinfo
	 */
	public AlienAllInfoMessage(String allinfo){
		
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.messages.Message#toXML()
	 */
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

}

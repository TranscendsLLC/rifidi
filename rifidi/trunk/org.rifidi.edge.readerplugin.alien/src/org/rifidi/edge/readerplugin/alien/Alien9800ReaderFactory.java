/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.Destination;

import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.core.readers.AbstractReaderFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class Alien9800ReaderFactory extends
		AbstractReaderFactory<Alien9800Reader> {

	private Destination destination;
	private JmsTemplate template;
	/** The Unique ID for this Factory */
	public static final String FACTORY_ID = "Alien9800";
	/** Description of the readerSession. */
	private static final String description = "The Alien 9800 is an IP based RFID ReaderSession using a telnet interface.";
	/** Name of the readerSession. */
	private static final String name = "Alien9800";
	/** A JMS event notification sender*/
	private NotifierServiceWrapper notifierServiceWrapper;

	/**
	 * Called by spring
	 * 
	 * @param wrapper
	 */
	public void setNotifierServiceWrapper(NotifierServiceWrapper wrapper) {
		this.notifierServiceWrapper = wrapper;
	}

	/**
	 * @return the destination
	 */
	public Destination getDestination() {
		return destination;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	/**
	 * @return the template
	 */
	public JmsTemplate getTemplate() {
		return template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.AbstractServiceFactory#getClazz()
	 */
	@Override
	public Class<Alien9800Reader> getClazz() {
		return Alien9800Reader.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public List<String> getFactoryIDs() {
		List<String> ret = new ArrayList<String>();
		ret.add(FACTORY_ID);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.AbstractServiceFactory#customConfig(java
	 * .lang.Object)
	 */
	@Override
	public void customConfig(Alien9800Reader instance) {
		instance.setDestination(destination);
		instance.setTemplate(template);
		instance.setNotifiyServiceWrapper(notifierServiceWrapper);
		Set<String> interfaces = new HashSet<String>();
		interfaces.add(AbstractReader.class.getName());
		instance.register(getContext(), interfaces);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getDisplayName() {
		return name;
	}

}

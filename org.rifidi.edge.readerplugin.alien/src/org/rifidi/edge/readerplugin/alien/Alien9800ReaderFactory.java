/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;

import org.rifidi.edge.core.readers.AbstractReaderFactory;
import org.rifidi.edge.core.readers.AbstractReader;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class Alien9800ReaderFactory extends
		AbstractReaderFactory<Alien9800Reader> {

	private Destination destination;
	private JmsTemplate template;

	/**
	 * @return the destination
	 */
	public Destination getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
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
	 * @param template the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}
	
	/** The ComandConfigurationFactory for this readerSession */
	private Alien9800CommandConfigurationFactory commandConfigFactory;

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
		ret.add("Alien9800");
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
		instance.setServiceRegistration(getContext().registerService(AbstractReader.class.getName(),
				instance, null));
	}

	@Override
	public String getCommandConfigFactoryID() {
		return Alien9800CommandConfigurationFactory.uniqueID;
	}

}

/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.newcore.readers.AbstractReaderConfigurationFactory;
import org.rifidi.edge.newcore.readers.ReaderConfiguration;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class Alien9800ReaderConfigurationFactory extends
		AbstractReaderConfigurationFactory<Alien9800ReaderConfiguration> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.AbstractServiceFactory#getClazz()
	 */
	@Override
	public Class<Alien9800ReaderConfiguration> getClazz() {
		return Alien9800ReaderConfiguration.class;
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
	public void customConfig(Alien9800ReaderConfiguration instance) {
		getContext().registerService(ReaderConfiguration.class.getName(),
				instance, null);
	}

	@Override
	public String getCommandConfigurationFactoryFactoryID() {
		return Alien9800CommandConfigurationFactory.uniqueID;
	}

}

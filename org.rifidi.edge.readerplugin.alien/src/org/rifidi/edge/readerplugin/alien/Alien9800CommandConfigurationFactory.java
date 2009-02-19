/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.commands.CommandFactory;
import org.rifidi.edge.readerplugin.alien.commands.AlienGetTagListCommandFactory;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class Alien9800CommandConfigurationFactory extends
		AbstractCommandConfigurationFactory {

	private Map<String, Class<?>> factoryIdToClass;
	/** The gloabaly unique name for this factory */
	public static final String uniqueID = "Alien9800CommandConfigurationFactory";

	/**
	 * 
	 */
	public Alien9800CommandConfigurationFactory() {
		super();
		factoryIdToClass = new HashMap<String, Class<?>>();
		factoryIdToClass.put("Alien9800-GetTagList",
				AlienGetTagListCommandFactory.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.AbstractMultiServiceFactory#customInit(java.
	 * lang.Object)
	 */
	@Override
	public void customInit(Object instance) {
		if (instance instanceof CommandFactory<?>) {
			getContext().registerService(CommandFactory.class.getName(),
					instance, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.AbstractMultiServiceFactory#getFactoryIDToClass
	 * ()
	 */
	@Override
	public Map<String, Class<?>> getFactoryIDToClass() {
		return new HashMap<String, Class<?>>(factoryIdToClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.ServiceFactory#getFactoryIDs()
	 */
	@Override
	public List<String> getFactoryIDs() {
		return new ArrayList<String>(factoryIdToClass.keySet());
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory#getID()
	 */
	@Override
	public String getID() {
		return uniqueID;
	}
	
}

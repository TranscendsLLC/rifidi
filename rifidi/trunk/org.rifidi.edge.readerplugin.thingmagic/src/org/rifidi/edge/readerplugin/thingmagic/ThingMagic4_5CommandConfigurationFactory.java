package org.rifidi.edge.readerplugin.thingmagic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.commands.Command;
import org.rifidi.edge.readerplugin.thingmagic.command.ThingMagicGetTagListCommandConfiguration;

public class ThingMagic4_5CommandConfigurationFactory extends
		AbstractCommandConfigurationFactory {

	private Map<String, Class<?>> factoryIdToClass;
	public static final String uniqueID = "ThingMagic4_5CommandConfigurationFactory";

	
	/**
	 * 
	 */
	public ThingMagic4_5CommandConfigurationFactory() {
		super();
		factoryIdToClass = new HashMap<String, Class<?>>();
		factoryIdToClass.put("ThingMagic4_5-GetTagList", ThingMagicGetTagListCommandConfiguration.class);
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory#getReaderFactoryID()
	 */
	@Override
	public String getReaderFactoryID() {
		return ThingMagic4_5ReaderFactory.FACTORY_ID;
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
		if (instance instanceof AbstractCommandConfiguration<?>) {
			AbstractCommandConfiguration<?> cc = (AbstractCommandConfiguration<?>) instance;
			Set<String> intefaces = new HashSet<String>();
			intefaces.add(AbstractCommandConfiguration.class.getName());
			cc.register(getContext(), intefaces);
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
}

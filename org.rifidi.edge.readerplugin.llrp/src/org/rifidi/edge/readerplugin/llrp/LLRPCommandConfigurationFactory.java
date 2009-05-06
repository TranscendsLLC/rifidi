/*
 *  LLRPCommandConfigurationFactory.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.llrp;
//TODO: Comments
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.readerplugin.llrp.commands.LLRPGetTagListCommandConfiguration;
import org.rifidi.edge.readerplugin.llrp.commands.LLRPROSpecCommandConfiguration;

/**
 * @author Matthew Dean
 * 
 */
public class LLRPCommandConfigurationFactory extends
		AbstractCommandConfigurationFactory {

	private Map<String, Class<?>> factoryIdToClass;

	/**
	 * 
	 */
	public LLRPCommandConfigurationFactory() {
		super();
		factoryIdToClass = new HashMap<String, Class<?>>();
		factoryIdToClass.put("LLRP-GetTagList",
				LLRPGetTagListCommandConfiguration.class);
		factoryIdToClass.put("LLRP-CreateROSpec", LLRPROSpecCommandConfiguration.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.impl.AbstractMultiServiceFactory#customInit(
	 * java.lang.Object)
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
	 * org.rifidi.configuration.impl.AbstractMultiServiceFactory#getFactoryIDToClass
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

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.commands.AbstractCommandConfigurationFactory#
	 * getReaderFactoryID()
	 */
	@Override
	public String getReaderFactoryID() {
		return LLRPReaderFactory.FACTORY_ID;
	}

}

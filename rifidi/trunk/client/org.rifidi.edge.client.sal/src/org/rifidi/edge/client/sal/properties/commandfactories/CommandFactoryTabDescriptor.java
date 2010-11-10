/*
 * CommandFactoryTabDescriptor.java
 * 
 * Created:     Oct 21st, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.sal.properties.commandfactories;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISectionDescriptor;
import org.rifidi.edge.client.model.sal.RemoteCommandConfigFactory;

/**
 * This class provides descriptor for the Tab that describes a CommandFactory in
 * a Tabbed Properties view
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CommandFactoryTabDescriptor extends AbstractTabDescriptor {

	/** The ID for the CommandFactories Tab */
	public static final String ID = "org.rifidi.edge.client.sal.props.commandfactories";

	public CommandFactoryTabDescriptor(RemoteCommandConfigFactory commandFactory) {
		super();
		List<ISectionDescriptor> descriptors = new ArrayList<ISectionDescriptor>();
		descriptors
				.add(new CommandFactoryInfoSectionDescriptor(commandFactory));
		super.setSectionDescriptors(descriptors);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.ITabDescriptor#getCategory()
	 */
	@Override
	public String getCategory() {
		return "info";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.ITabDescriptor#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.ITabDescriptor#getLabel()
	 */
	@Override
	public String getLabel() {
		return "Info";
	}

}

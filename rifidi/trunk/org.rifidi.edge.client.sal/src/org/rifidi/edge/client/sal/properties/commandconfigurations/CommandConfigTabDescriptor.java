/*
 * CommandConfigTabDescriptor.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.sal.properties.commandconfigurations;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanInfo;

import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;

/**
 * The TabDescriptor for the CommandConfiguration property view.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CommandConfigTabDescriptor extends AbstractTabDescriptor {

	/** Tab ID */
	public final String ID;
	/** Category of the tab */
	private String category;

	/**
	 * Constructor.
	 * 
	 * @param info
	 *            the MBeanInfo to use for the tab
	 * @param commandConfig
	 *            The CommandConfig to display the properties of
	 * @param category
	 *            The category to display
	 */
	public CommandConfigTabDescriptor(MBeanInfo info,
			RemoteCommandConfiguration commandConfig, String category) {
		super();
		this.category = category;
		ID = "org.rifidi.edge.client.sal.props.edgeserver.command."
				+ commandConfig.getID() + "." + category;
		List<CommandConfigSectionDescriptor> descriptors = new ArrayList<CommandConfigSectionDescriptor>();
		descriptors.add(new CommandConfigSectionDescriptor(ID, info,
				commandConfig, category));
		super.setSectionDescriptors(descriptors);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.ITabDescriptor#getCategory()
	 */
	@Override
	public String getCategory() {
		return category;
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
		StringBuffer buf = new StringBuffer(category);
		//ensure first letter of Category is capitalized
		buf.setCharAt(0,Character.toUpperCase(buf.charAt(0)));
		return buf.toString();
	}

}

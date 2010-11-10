/*
 * CommandFactoryInfoSectionDescriptor.java
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

import org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.rifidi.edge.client.model.sal.RemoteCommandConfigFactory;

/**
 * This class provides Descriptors for InformationSection of CommandFactories
 * for a tabbed properties view
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CommandFactoryInfoSectionDescriptor extends
		AbstractSectionDescriptor {

	/** The ID for the CommandFactoryInfo Tab */
	public static final String ID = "org.rifidi.edge.client.sal.props.commandfactories.info";
	/**The factory to describe*/
	private RemoteCommandConfigFactory remoteCommandFac;

	/**
	 * Constructor
	 * @param remoteCommandFac The factory to describe
	 */
	public CommandFactoryInfoSectionDescriptor(
			RemoteCommandConfigFactory remoteCommandFac) {
		super();
		this.remoteCommandFac = remoteCommandFac;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.ISectionDescriptor#getId()
	 */
	@Override
	public String getId() {
		return ID + remoteCommandFac.getCommandConfigFactoryID().trim();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.ISectionDescriptor#getSectionClass
	 * ()
	 */
	@Override
	public ISection getSectionClass() {
		return new CommandFactoryInfoSecion(remoteCommandFac);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.ISectionDescriptor#getTargetTab()
	 */
	@Override
	public String getTargetTab() {
		return CommandFactoryTabDescriptor.ID;
	}

	@Override
	public List getInputTypes() {
		List<String> types = new ArrayList<String>();
		types.add(RemoteCommandConfigFactory.class.getName());
		return types;
	}

}

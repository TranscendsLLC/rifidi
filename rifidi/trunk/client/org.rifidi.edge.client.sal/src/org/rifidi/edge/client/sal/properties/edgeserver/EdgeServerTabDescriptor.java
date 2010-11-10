/*
 * EdgeServerTabDescriptor.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.sal.properties.edgeserver;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISectionDescriptor;

/**
 * A TabbedProperties View TabDescriptor for displaying information about the
 * Edge Server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class EdgeServerTabDescriptor extends AbstractTabDescriptor {

	/** The ID for the Edge Server Tab */
	public static final String ID = "org.rifidi.edge.client.sal.props.edgeserver";

	/**
	 * Constructor
	 */
	public EdgeServerTabDescriptor() {
		super();
		List<ISectionDescriptor> descriptors = new ArrayList<ISectionDescriptor>();
		descriptors.add(new EdgeServerStateSectionDescriptor());
		descriptors.add(new EdgeServerConnectionInfoSectionDescriptor());
		super.setSectionDescriptors(descriptors);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.ITabDescriptor#getCategory()
	 */
	@Override
	public String getCategory() {
		return "EdgeServer";
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
		return "Edge Server";
	}

}

/*
 * ReaderTabDescriptor.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.sal.properties.readers;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanInfo;

import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;
import org.rifidi.edge.client.model.sal.RemoteReader;

/**
 * A TabbedProperties Tab Descriptor for a Reader. There is one tab created for
 * each distinct category in the MbeanInfo Attributes
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReaderTabDescriptor extends AbstractTabDescriptor {

	/** The ID of the tab */
	public final String ID;
	/** The reader that contains the properties to display */
	private RemoteReader reader;
	/** The category of properties to display in this tab */
	private String category;

	/**
	 * Constructor.
	 * 
	 * @param info
	 * @param reader
	 * @param category
	 */
	public ReaderTabDescriptor(MBeanInfo info, RemoteReader reader,
			String category) {
		super();
		this.reader = reader;
		this.category = category;
		ID = "org.rifidi.edge.client.sal.props.edgeserver.reader."
				+ reader.getID() + "." + category;
		List<ReaderSectionDescriptor> descriptors = new ArrayList<ReaderSectionDescriptor>();
		descriptors
				.add(new ReaderSectionDescriptor(ID, info, reader, category));
		super.setSectionDescriptors(descriptors);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.ITabDescriptor#getCategory()
	 */
	@Override
	public String getCategory() {
		return reader.getID() + "." + category;
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
		return category;
	}

}

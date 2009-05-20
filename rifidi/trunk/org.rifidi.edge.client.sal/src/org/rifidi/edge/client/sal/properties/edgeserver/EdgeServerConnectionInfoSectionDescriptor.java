package org.rifidi.edge.client.sal.properties.edgeserver;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;

/**
 * A TabbedProperties Section Descriptor for displaying information about
 * connecting to an edge server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class EdgeServerConnectionInfoSectionDescriptor extends
		AbstractSectionDescriptor {

	/** ID of the Edge Server Connection Section Descriptor */
	public static final String ID = "org.rifidi.edge.client.sal.props.edgeserver.connection";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.ISectionDescriptor#getId()
	 */
	@Override
	public String getId() {
		return ID;
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
		return new EdgeServerConnectionInfoSection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.ISectionDescriptor#getTargetTab()
	 */
	@Override
	public String getTargetTab() {
		return EdgeServerTabDescriptor.ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor#
	 * getAfterSection()
	 */
	@Override
	public String getAfterSection() {
		return EdgeServerStateSectionDescriptor.ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor#
	 * getInputTypes()
	 */
	@Override
	public List getInputTypes() {
		List<String> type = new ArrayList<String>();
		type.add(RemoteEdgeServer.class.getName());
		return type;
	}

}

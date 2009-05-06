/**
 * 
 */
package org.rifidi.edge.client.sal.properties.edgeserver;
//TODO: Comments
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISectionDescriptor;

/**
 * @author kyle
 * 
 */
public class EdgeServerTabDescriptor extends AbstractTabDescriptor {

	public static final String ID = "org.rifidi.edge.client.sal.props.edgeserver";

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

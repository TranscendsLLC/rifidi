package org.rifidi.edge.client.properties.sections;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.jdom.Element;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;

public class ReaderPropertySectionDescriptor extends AbstractSectionDescriptor {

	Log logger = LogFactory.getLog(ReaderPropertySectionDescriptor.class);

	private Element prop;
	String tabID;
	private boolean shouldTakeSpace;

	public ReaderPropertySectionDescriptor(String tabID, Element prop,
			boolean shouldTakeSpace) {
		super();
		this.tabID = tabID;
		this.prop = prop;
		this.shouldTakeSpace = shouldTakeSpace;
	}

	@Override
	public String getId() {
		return tabID + "." + prop;
	}

	@Override
	public ISection getSectionClass() {
		return new ReaderPropertySection(prop, shouldTakeSpace);
	}

	@Override
	public List getInputTypes() {
		ArrayList<String> inputTypes = new ArrayList<String>();
		inputTypes.add(RemoteReader.class.getName());
		return inputTypes;
	}

	@Override
	public String getTargetTab() {

		return tabID;
	}

}

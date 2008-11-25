package org.rifidi.edge.client.properties.tabs;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;
import org.jdom.Element;
import org.rifidi.edge.client.properties.sections.ReaderPropertySectionDescriptor;

public class PropertyGroupTabDescriptor extends AbstractTabDescriptor {

	private String group;
	private String tabID;
	private Log logger = LogFactory.getLog(PropertyGroupTabDescriptor.class);

	public PropertyGroupTabDescriptor(String readerType, String group,
			Collection<Element> properties) {

		super();
		this.group = group;
		tabID = readerType + "." + group;
		int count = 0;
		for (Element prop : properties) {
			if (count == (properties.size() - 1)) {
				getSectionDescriptors().add(
						new ReaderPropertySectionDescriptor(tabID, prop, true));
			} else {
				getSectionDescriptors().add(
						new ReaderPropertySectionDescriptor(tabID, prop, false));
			}
			count++;

		}

	}

	@Override
	public String getCategory() {
		return group;
	}

	@Override
	public String getId() {
		return tabID;
	}

	@Override
	public String getLabel() {
		return group;
	}

}

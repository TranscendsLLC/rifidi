/**
 * 
 */
package org.rifidi.edge.client.sal.properties.commandconfigurations;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanInfo;

import org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.sal.properties.MBeanModelObjectPropertySection;

/**
 * @author kyle
 * 
 */
public class CommandConfigSectionDescriptor extends AbstractSectionDescriptor {

	private String tabID;
	private MBeanInfo info;
	private RemoteCommandConfiguration commandConfig;
	private String category;
	private final String ID;

	public CommandConfigSectionDescriptor(String tabID, MBeanInfo info,
			RemoteCommandConfiguration commandConfig, String category) {
		this.ID = tabID + "." + "section";
		this.info = info;
		this.tabID = tabID;
		this.commandConfig = commandConfig;
		this.category = category;
	}

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
		return new MBeanModelObjectPropertySection(info, commandConfig, category);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.ISectionDescriptor#getTargetTab()
	 */
	@Override
	public String getTargetTab() {
		return tabID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor#
	 * getInputTypes()
	 */
	@Override
	public List getInputTypes() {
		List<String> types = new ArrayList<String>();
		types.add(RemoteCommandConfiguration.class.getName());
		return types;
	}

}

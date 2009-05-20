package org.rifidi.edge.client.sal.properties.commandconfigurations;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanInfo;

import org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.sal.properties.MBeanModelObjectPropertySection;

/**
 * This is a TabbedProperties Section Descriptor for a CommandConfiguration. For
 * CommandConfigurations, there is only one section per tab.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CommandConfigSectionDescriptor extends AbstractSectionDescriptor {

	/** The id of the tab this section descriptor */
	private String tabID;
	/** The MBeanInfo that describes the properties of a command configuration */
	private MBeanInfo info;
	/** The model object */
	private RemoteCommandConfiguration commandConfig;
	/** The category of properties to display on this section */
	private String category;
	/** The ID of the section descriptor */
	private final String ID;

	/**
	 * Constructor.
	 * 
	 * @param tabID
	 *            The ID of the tab this sectiondescriptor works with
	 * @param info
	 *            the MBeanInfo that contains the description of the attributes
	 *            of the command configuration
	 * @param commandConfig
	 *            The model object for this section
	 * @param category
	 *            the type of attributes to display
	 */
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
		return new MBeanModelObjectPropertySection(info, commandConfig,
				category);
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

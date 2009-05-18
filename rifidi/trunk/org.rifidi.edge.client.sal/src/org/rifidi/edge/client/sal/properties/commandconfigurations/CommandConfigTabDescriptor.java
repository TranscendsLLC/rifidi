
package org.rifidi.edge.client.sal.properties.commandconfigurations;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanInfo;

import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;

/**
 * TODO: Class level comment.  
 * 
 * @author kyle
 */
public class CommandConfigTabDescriptor extends AbstractTabDescriptor {

	public final String ID;
	private String category;

	/**
	 * Constructor.  
	 * 
	 * @param info
	 * @param commandConfig
	 * @param category
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
		return category;
	}

}

package org.rifidi.edge.client.sal.properties.readers;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanInfo;

import org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.sal.properties.MBeanModelObjectPropertySection;

/**
 * A TabbedProperties Section Descriptor for a Reader. There is only one section
 * created per Tab. Each section can contain multiple widgets
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReaderSectionDescriptor extends AbstractSectionDescriptor {

	/** The ID of the tab to display */
	private String tabID;
	/** The MBeanInfo that describes the reader */
	private MBeanInfo info;
	/** The reader that contains the properties to display */
	private RemoteReader reader;
	/** The ID of the section */
	private final String ID;
	/** The category of properties to display in this section */
	private String category;

	/**
	 * Constructor
	 * 
	 * @param tabID
	 *            The ID of the tab this section should be added to
	 * @param info
	 *            The MBeanInfo that describes the reader
	 * @param reader
	 *            The reader that contains the properties to display
	 * @param category
	 *            The category of properties to display in the section
	 */
	public ReaderSectionDescriptor(String tabID, MBeanInfo info,
			RemoteReader reader, String category) {
		this.tabID = tabID;
		this.info = info;
		this.reader = reader;
		this.ID = tabID + "." + "section";
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
		return new MBeanModelObjectPropertySection(info, reader, category);
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
		types.add(RemoteReader.class.getName());
		types.add("org.rifidi.edge.client.twodview.sfx.ReaderAlphaImageFigure");
		return types;
	}

}

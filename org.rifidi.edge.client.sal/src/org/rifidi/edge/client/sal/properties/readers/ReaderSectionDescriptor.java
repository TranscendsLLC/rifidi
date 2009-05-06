/**
 * 
 */
package org.rifidi.edge.client.sal.properties.readers;
//TODO: Comments
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanInfo;

import org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.sal.properties.MBeanModelObjectPropertySection;

/**
 * @author kyle
 * 
 */
public class ReaderSectionDescriptor extends AbstractSectionDescriptor {

	private String tabID;
	private MBeanInfo info;
	private RemoteReader reader;
	private final String ID;
	private String category;

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

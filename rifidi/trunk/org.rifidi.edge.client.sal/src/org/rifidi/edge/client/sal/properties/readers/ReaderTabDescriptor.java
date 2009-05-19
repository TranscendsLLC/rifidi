
package org.rifidi.edge.client.sal.properties.readers;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanInfo;

import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;
import org.rifidi.edge.client.model.sal.RemoteReader;

/**
 * TODO: Class level comment.  
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReaderTabDescriptor extends AbstractTabDescriptor {

	public final String ID; 
	private RemoteReader reader;
	private String category;
	
	/**
	 * Constructor.  
	 * 
	 * @param info
	 * @param reader
	 * @param category
	 */
	public ReaderTabDescriptor(MBeanInfo info, RemoteReader reader, String category) {
		super();
		this.reader = reader;
		this.category = category;
		ID = "org.rifidi.edge.client.sal.props.edgeserver.reader."+reader.getID()+"."+category;
		List<ReaderSectionDescriptor> descriptors = new ArrayList<ReaderSectionDescriptor>();
		descriptors.add(new ReaderSectionDescriptor(ID, info, reader, category));
		super.setSectionDescriptors(descriptors);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.ITabDescriptor#getCategory()
	 */
	@Override
	public String getCategory() {
		return reader.getID()+"."+category;
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

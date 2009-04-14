/**
 * 
 */
package org.rifidi.edge.client.sal.properties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.MBeanAttributeInfo;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptorProvider;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.sal.properties.edgeserver.EdgeServerTabDescriptor;
import org.rifidi.edge.client.sal.properties.readers.ReaderTabDescriptor;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SALTabDescriptorProvider implements ITabDescriptorProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.views.properties.tabbed.ITabDescriptorProvider#
	 * getTabDescriptors(org.eclipse.ui.IWorkbenchPart,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public ITabDescriptor[] getTabDescriptors(IWorkbenchPart part,
			ISelection selection) {
		Object obj = ((StructuredSelection) selection).getFirstElement();
		if (obj instanceof RemoteEdgeServer) {
			return new ITabDescriptor[] { new EdgeServerTabDescriptor() };
		} else if (obj instanceof RemoteReader) {
			return getReaderTabDescriptors((RemoteReader) obj);
		}

		return new ITabDescriptor[] {};
	}

	/**
	 * A helper method that steps through all of the MBeanAttributeInfos in a
	 * reader and creates A tab descriptor for it.
	 * 
	 * @param reader
	 * @return
	 */
	public static ITabDescriptor[] getReaderTabDescriptors(RemoteReader reader) {
		List<ITabDescriptor> tabDescriptors = new ArrayList<ITabDescriptor>();
		// create a new TabDescriptor for each MBeanAttributeCategory
		Set<String> categories = new HashSet<String>();
		for (MBeanAttributeInfo info : reader.getFactory().getMbeanInfo()
				.getAttributes()) {

			// look up category from descriptor
			String category = (String) info.getDescriptor().getFieldValue(
					"org.rifidi.edge.category");
			// if category was not set, make the group Miscellaneous
			if (category == null || category.equals("")) {
				category = "Miscellaneous";
				info.getDescriptor().setField("org.rifidi.edge.category",
						"Miscellaneous");
			}
			categories.add(category);
		}

		for (String category : categories) {
			tabDescriptors.add(new ReaderTabDescriptor(reader.getFactory()
					.getMbeanInfo(), reader, category));
		}

		ITabDescriptor[] descriptorArray = new ITabDescriptor[tabDescriptors
				.size()];
		return tabDescriptors.toArray(descriptorArray);
	}
}

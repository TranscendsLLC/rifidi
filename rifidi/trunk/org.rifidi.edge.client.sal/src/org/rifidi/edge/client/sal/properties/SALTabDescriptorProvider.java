/**
 * 
 */
package org.rifidi.edge.client.sal.properties;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptorProvider;
import org.rifidi.edge.client.sal.views.CommandView;
import org.rifidi.edge.client.sal.views.EdgeServerView;

/**
 * @author kyle
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
		ITabDescriptor[] retVal;
		System.out.println("GET THE TAB DESCRIPTORS!!!");
		if (part.getSite().getId().equals(CommandView.ID)) {
			System.out.println("COMMAND!");
		} else if (part.getSite().getId().equals(EdgeServerView.ID)) {
			
		}
		return null;
	}

}

package org.rifidi.edge.client.properties.tabs;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptorProvider;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;

public class RemoteReaderPropertyTabDescriptionProvider implements
		ITabDescriptorProvider {

	private Log logger = LogFactory
			.getLog(RemoteReaderPropertyTabDescriptionProvider.class);

	@Override
	public ITabDescriptor[] getTabDescriptors(IWorkbenchPart part,
			ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object o = sel.getFirstElement();
			if (o instanceof RemoteReader) {
				RemoteReader rr = (RemoteReader) o;

				Set<String> groups = rr.getReaderPluginWrapper().getGroups();
				ITabDescriptor[] tabs = new ITabDescriptor[groups.size()];
				Iterator<String> iter = groups.iterator();
				for (int i = 0; i < tabs.length; i++) {
					String group = iter.next();
					tabs[i] = new PropertyGroupTabDescriptor(rr
							.getReaderInfoClassName(), group, rr
							.getPropertyAnnotations(group));
				}
				return tabs;

			}
		}
		return null;
	}

}

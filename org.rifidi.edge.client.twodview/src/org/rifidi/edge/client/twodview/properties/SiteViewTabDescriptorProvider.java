
package org.rifidi.edge.client.twodview.properties;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptorProvider;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.sal.properties.SALTabDescriptorProvider;
import org.rifidi.edge.client.twodview.sfx.ReaderAlphaImageFigure;

/**
 * TODO: Class level comment.  
 * 
 * @author kyle
 */
public class SiteViewTabDescriptorProvider implements ITabDescriptorProvider {

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
		if (obj instanceof ReaderAlphaImageFigure) {
			ReaderAlphaImageFigure fig = (ReaderAlphaImageFigure) obj;
			RemoteReader reader = fig.getReader();
			if (reader != null) {
				return SALTabDescriptorProvider.getReaderTabDescriptors(reader);
			}
		}
		return new ITabDescriptor[] {};
	}

}

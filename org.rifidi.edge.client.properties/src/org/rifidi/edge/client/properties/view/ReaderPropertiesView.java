/**
 * 
 */
package org.rifidi.edge.client.properties.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.PropertySheet;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.views.EdgeServerConnectionView;

/**
 * This is the properties view that displays properties of readers.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderPropertiesView extends PropertySheet {

	@SuppressWarnings("unused")
	private Log logger = LogFactory.getLog(ReaderPropertiesView.class);

	/**
	 * Ignore view parts that are not an EdgeServerConnectionView. This way the
	 * properties view does not try to display properties that are a part of
	 * another view
	 */
	public void partActivated(IWorkbenchPart part) {
		ISelection sel = part.getSite().getSelectionProvider().getSelection();
		
		if (part instanceof EdgeServerConnectionView && sel instanceof RemoteReader) {
			super.partActivated(part);
		}
	}
}

/**
 * 
 */
package org.rifidi.edge.client.properties.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.PropertySheet;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.views.EdgeServerConnectionView;

/**
 * This is the properties view that displays properties of readers.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ReaderPropertiesView extends PropertySheet {

	private Log logger = LogFactory.getLog(ReaderPropertiesView.class);

	/**
	 * Ignore view parts that are not an EdgeServerConnectionView. This way the
	 * properties view does not try to display properties that are a part of
	 * another view
	 */
	public void partActivated(IWorkbenchPart part) {
		//TODO: improve
		if (part instanceof EdgeServerConnectionView && part != null) {
			IStructuredSelection sel = (TreeSelection) part.getSite()
					.getSelectionProvider().getSelection();
			try {
				if (((RemoteReader) sel.getFirstElement()) instanceof RemoteReader) {
					super.partActivated(part);
				}
			} catch (Exception e) {
				logger.debug("ERROR: " + e.toString());
			}

		}
	}
}

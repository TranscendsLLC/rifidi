/* 
 *  ConnectAle.java
 *  Created:	Apr 15, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.treeview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.treeview.handlers;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.Preferences;
import org.rifidi.edge.client.ale.models.aleserviceporttype.AleServicePortTypeWrapper;
import org.rifidi.edge.client.ale.models.exceptions.RifidiNoEndpointDefinedException;
import org.rifidi.edge.client.ale.treeview.Activator;
import org.rifidi.edge.client.ale.treeview.preferences.AleTreeViewPreferences;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ConnectAle extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getCurrentSelectionChecked(event);

		Object port = ((IStructuredSelection) sel).getFirstElement();

		if (port instanceof AleServicePortTypeWrapper) {
			Preferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
			String aleEp = node.get(AleTreeViewPreferences.ALE_ENDPOINT,
					AleTreeViewPreferences.ALE_ENDPOINT_DEFAULT);
			try {
				((AleServicePortTypeWrapper) port).setEndpoint(new URL(aleEp));
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				((AleServicePortTypeWrapper) port).connect();
			} catch (RifidiNoEndpointDefinedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}

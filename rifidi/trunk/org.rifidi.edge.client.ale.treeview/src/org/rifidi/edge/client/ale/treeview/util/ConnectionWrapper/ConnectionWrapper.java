/* 
 *  ConnectionWrapper.java
 *  Created:	Mar 20, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.treeview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.treeview.util.ConnectionWrapper;

import java.net.MalformedURLException;
import java.net.URL;

import javax.management.ServiceNotFoundException;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.osgi.service.prefs.Preferences;
import org.rifidi.edge.client.ale.connection.handler.ConnectionHandler;
import org.rifidi.edge.client.ale.connection.service.ConnectionService;
import org.rifidi.edge.client.ale.treeview.Activator;
import org.rifidi.edge.client.ale.treeview.preferences.AleTreeViewPreferences;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ConnectionWrapper {

	private ConnectionService service = null;

	public ConnectionService getConnectionService() {

		if (service == null) {
			ConnectionHandler conHan = new ConnectionHandler();

			try {
				service = conHan.getService();
			} catch (ServiceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Preferences node = new DefaultScope()
					.getNode(Activator.PLUGIN_ID);
			String aleEp = node.get(
					AleTreeViewPreferences.ALE_ENDPOINT,
					AleTreeViewPreferences.ALE_ENDPOINT_DEFAULT);
			String lrEp = node.get(
					AleTreeViewPreferences.ALELR_ENDPOINT,
					AleTreeViewPreferences.ALELR_ENDPOINT_DEFAULT);
			try {
				service.setLrEndpoint(new URL(lrEp));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				service.setAleEndpoint(new URL(aleEp));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			if (service.getAleEndpoint() == null||service.getLrEndpoint()==null) {
				System.err.println("Endpoint undefined.");
			}
		}
		return service;
	}


}

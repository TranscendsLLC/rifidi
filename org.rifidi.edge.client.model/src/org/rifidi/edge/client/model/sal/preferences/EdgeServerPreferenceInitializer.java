/**
 * 
 */
package org.rifidi.edge.client.model.sal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;
import org.rifidi.edge.client.model.Activator;

/**
 * @author kyle
 * 
 */
public class EdgeServerPreferenceInitializer extends
		AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		Preferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		node.put(EdgeServerPreferences.EDGE_SERVER_IP, "127.0.0.1");
		node.put(EdgeServerPreferences.EDGE_SERVER_PORT_JMS, "1099");
		node.put(EdgeServerPreferences.EDGE_SERVER_PORT_RMI, "1098");
		node.put(EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE,
				"org.rifidi.edge.external.notifications");
	}

}

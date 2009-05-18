
package org.rifidi.edge.client.model.sal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;
import org.rifidi.edge.client.model.SALModelPlugin;

/**
 * Put default values in the Preference Store
 * 
 * @author Kyle Neumeier - kyle@pramari.com
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
		Preferences node = new DefaultScope().getNode(SALModelPlugin.PLUGIN_ID);
		node.put(EdgeServerPreferences.EDGE_SERVER_IP,
				EdgeServerPreferences.EDGE_SERVER_IP_DEFAULT);
		node.put(EdgeServerPreferences.EDGE_SERVER_PORT_JMS,
				EdgeServerPreferences.EDGE_SERVER_PORT_JMS_DEFAULT);
		node.put(EdgeServerPreferences.EDGE_SERVER_PORT_RMI,
				EdgeServerPreferences.EDGE_SERVER_PORT_RMI_DEFAULT);
		node.put(EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE,
				EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE_DEFAULT);
		node.put(EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE_TAGS,
				EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE_TAGS_DEFAULT);
	}

}

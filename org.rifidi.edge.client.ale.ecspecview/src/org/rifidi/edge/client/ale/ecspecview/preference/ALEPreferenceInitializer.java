/**
 * 
 */
package org.rifidi.edge.client.ale.ecspecview.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;
import org.rifidi.edge.client.ale.ecspecview.Activator;

/**
 * @author kyle
 * 
 */
public class ALEPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		Preferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		node.put(Activator.ALE_PORT_URL_PREF_NAME,
				Activator.ALE_PORT_URL_DEFAULT_);

	}

}

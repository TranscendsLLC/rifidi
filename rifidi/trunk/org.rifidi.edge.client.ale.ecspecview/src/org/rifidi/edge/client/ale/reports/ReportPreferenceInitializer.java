
package org.rifidi.edge.client.ale.reports;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;
import org.rifidi.edge.client.ale.ecspecview.Activator;

/**
 * TODO: Class level comment.  
 * 
 * @author kyle
 */
public class ReportPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		Preferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		node.put(Activator.REPORT_RECEIVER_ADR,
				Activator.REPORT_RECEIVER_ADR_DEFAULT);

	}

}

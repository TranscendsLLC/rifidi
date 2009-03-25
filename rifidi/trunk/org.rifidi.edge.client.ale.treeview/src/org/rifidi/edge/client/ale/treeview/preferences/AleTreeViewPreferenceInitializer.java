/* 
 *  AleTreeViewPreferenceInitializer.java
 *  Created:	Mar 12, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.treeview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.treeview.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.rifidi.edge.client.ale.treeview.Activator;



/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 *
 */
public class AleTreeViewPreferenceInitializer extends
		AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		node.put(AleTreeViewPreferences.ALE_ENDPOINT, "http://localhost:8080/fc-server-0.4.0/services/ALEService");
		node.put(AleTreeViewPreferences.ALELR_ENDPOINT, "http://localhost:8080/fc-server-0.4.0/services/ALELRService");
	}

}

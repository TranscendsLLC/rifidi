
package org.rifidi.edge.client.ale.logicalreaders;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.rifidi.edge.client.ale.ecspecview.Activator;

/**
 * TODO: Class level comment.  
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class EndpointInitializer extends AbstractPreferenceInitializer {

	/**
	 * Constructor.  
	 */
	public EndpointInitializer() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences node=new DefaultScope().getNode(Activator.PLUGIN_ID);
		node.put(Activator.ALELR_ENDPOINT, Activator.ALELR_ENDPOINT_DEFAULT);
	}

}

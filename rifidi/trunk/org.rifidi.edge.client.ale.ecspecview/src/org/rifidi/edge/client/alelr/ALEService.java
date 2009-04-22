/**
 * 
 */
package org.rifidi.edge.client.alelr;

import org.eclipse.jface.util.IPropertyChangeListener;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ALEService extends IPropertyChangeListener {
	/**
	 * Register a listener for changes to the ALE stub.
	 * 
	 * @param listener
	 */
	void registerALEListener(ALEListener listener);

	/**
	 * Unregister a listener for changes to the ALE stub.
	 * 
	 * @param listener
	 */
	void unregisterALEListener(ALEListener listener);
}

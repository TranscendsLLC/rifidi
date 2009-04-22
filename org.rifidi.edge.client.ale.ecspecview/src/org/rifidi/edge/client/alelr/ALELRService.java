/**
 * 
 */
package org.rifidi.edge.client.alelr;

import org.eclipse.jface.util.IPropertyChangeListener;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ALELRService extends IPropertyChangeListener {

	/**
	 * Register a listener for changes to the ALE LR stub.
	 * 
	 * @param listener
	 */
	void registerALELRListener(ALELRListener listener);

	/**
	 * Unregister a listener for changes to the ALE LR stub.
	 * 
	 * @param listener
	 */
	void unregisterALELRListener(ALELRListener listener);
}

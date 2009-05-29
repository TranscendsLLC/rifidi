/**
 * 
 */
package org.rifidi.edge.ale.esper;

/**
 * A signal statement can inform listeners about certain events arriving.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface SignalStatement extends StatementController {
	/**
	 * Register a new listener.
	 * 
	 * @param listener
	 */
	public void registerSignalListener(SignalListener listener);

	/**
	 * Unregister a listener.
	 * 
	 * @param listener
	 */
	public void unregisterSignalListener(SignalListener listener);
}

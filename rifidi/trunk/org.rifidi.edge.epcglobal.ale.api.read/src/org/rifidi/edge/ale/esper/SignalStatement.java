/**
 * 
 */
package org.rifidi.edge.ale.esper;

/**
 * @author jochen
 * 
 */
public interface SignalStatement extends StatementController {
	public void registerSignalListener(SignalListener listener);

	public void unregisterSignalListener(SignalListener listener);
}

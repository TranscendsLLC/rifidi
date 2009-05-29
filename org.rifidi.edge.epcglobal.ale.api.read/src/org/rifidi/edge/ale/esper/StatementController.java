/**
 * 
 */
package org.rifidi.edge.ale.esper;

/**
 * Control the lifecycle of a statement.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface StatementController {
	/**
	 * Start the controlled statement.
	 */
	public void start();

	/**
	 * Stop the controlled statement.
	 */
	public void stop();

	/**
	 * True if the statement needs to be restarted for each event cycle.
	 * 
	 * @return
	 */
	boolean needsRestart();
}

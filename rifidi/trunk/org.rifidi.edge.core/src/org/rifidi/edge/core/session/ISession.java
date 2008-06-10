/**
 * 
 */
package org.rifidi.edge.core.session;

/**
 * @author jerry
 *
 */
public interface ISession {
	public void startTagStream();
	public void stopTagStream();
	public void sendCustomCommand(Object obj);
}

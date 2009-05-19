
package org.rifidi.edge.client.model.sal;

/**
 * Possible states for the Remote Edge Server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public enum RemoteEdgeServerState {
	/**State when RemoteEdgeServer is connected to the server*/
	CONNECTED, 
	/** State when RemoteEdgeServer is disconnected from the server */
	DISCONNECTED;
}

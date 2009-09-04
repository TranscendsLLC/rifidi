
package org.rifidi.edge.api;

/**
 * Enum for the status of a session.  
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public enum SessionStatus {
	CREATED,
	CONNECTING,
	DISCONNECTING,
	LOGGINGIN,
	CLOSED,
	PROCESSING,
	FAIL
}

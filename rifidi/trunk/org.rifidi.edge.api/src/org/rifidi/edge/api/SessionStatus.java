/*
 * SessionStatus.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */

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

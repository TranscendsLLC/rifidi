/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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

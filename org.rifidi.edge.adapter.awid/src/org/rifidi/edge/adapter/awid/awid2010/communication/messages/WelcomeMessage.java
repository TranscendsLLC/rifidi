/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.awid.awid2010.communication.messages;

/**
 * The Welcome message from a awid reader.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class WelcomeMessage extends AbstractAwidMessage {

	private String message = null;
	
	public static final String WELCOME_PHRASE_3014 = "3014";
	
	/**
	 * 
	 * @param rawmessage
	 */
	public WelcomeMessage(byte[] rawmessage) {
		super(rawmessage);
		message = new String(rawmessage);
	}
	
	public boolean is3014() {
		if(message != null && message.contains(WELCOME_PHRASE_3014)) {
			return true;
		}
		return false;
	}
	
	

}

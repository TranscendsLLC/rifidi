/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.alien.gpio.messages;

/**
 * This class represents a header message from the IO Stream
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienGPIOHeaderMessage extends AlienGPIOMessage {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.readerplugin.alien.gpio.messages.AlienGPIOMessage#
	 * getEventType()
	 */
	@Override
	public GPIOEvent getEventType() {
		return GPIOEvent.HEADER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.adapter.alien.gpio.messages.AlienGPIOMessage#getData
	 * ()
	 */
	@Override
	public int getData() {
		throw new RuntimeException("GPIO Header Messages do not have data");
	}

}

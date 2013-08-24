/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.alien.gpio.messages;

/**
 * The type of message sent back from the Alien Reader on the IOStream
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public enum GPIOEvent {

	/** A Digital Input event from the Alien GPIO Stream (GPI Event) */
	DI,

	/** A Digital Output event from the Alien GPIO Stream (GPO Event) */
	DO,

	/** A Header message from the ALien GPIO Stream */
	HEADER;

}

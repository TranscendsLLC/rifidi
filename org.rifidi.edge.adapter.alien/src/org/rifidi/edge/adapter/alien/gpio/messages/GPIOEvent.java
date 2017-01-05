/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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

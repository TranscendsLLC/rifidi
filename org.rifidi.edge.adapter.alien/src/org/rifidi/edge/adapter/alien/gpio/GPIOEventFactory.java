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
package org.rifidi.edge.adapter.alien.gpio;

import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.adapter.alien.gpio.messages.AlienGPIOMessage;
import org.rifidi.edge.adapter.alien.gpio.messages.GPIOEvent;
import org.rifidi.edge.notification.GPIEvent;
import org.rifidi.edge.notification.GPOEvent;

/**
 * This class is a factory that creates a GPIEvent or a GPOEvent from an
 * AlienGPIOMessage. The GPIEvent and GPOEvent can then be put into Esper.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPIOEventFactory {

	/** The ID of the reader this Factory creates events for */
	public String readerID;

	/**
	 * Constructor
	 * 
	 * @param readerID
	 *            The ID of the reader
	 */
	public GPIOEventFactory(String readerID) {
		this.readerID = readerID;
	}

	/**
	 * Create a Set of either GPIEvent or GPOEvent object from an
	 * AlienGPIOMessage
	 * 
	 * @param oldExternalInput
	 *            The old state of the Alien reader's external input
	 * @param oldExternalOutput
	 *            The old state of the Alien reader's external output
	 * @param message
	 *            The received message.
	 * @return A set of GPIEvent or GPOEvent objects to put into esper
	 */
	public Set<Object> getEvent(int oldExternalInput, int oldExternalOutput,
			AlienGPIOMessage message) {
		// create the set to return
		Set<Object> events = new HashSet<Object>();
		if (message.getEventType() == GPIOEvent.DI) {
			// get the new data byte
			int newData = message.getData();
			// set the mask bit to one
			int mask = 1;
			// for each GPI
			for (int i = 0; i < 4; i++) {
				// AND the data byte and the mask, so we have at most one 1 in
				// newDataBit
				int newDataBit = newData & mask;

				// AND the old data byte and the mask, so we have at most one 1
				// in oldDataBit
				int oldDataBit = oldExternalInput & mask;

				// If the newDataBit and the oldDataBit are not equal, something
				// has changed.
				if (newDataBit != oldDataBit) {
					// create the new GPIEvent
					GPIEvent event = new GPIEvent(readerID, i + 1,
							(newDataBit != 0));
					events.add(event);
				}
				// move the mask bit to the left.
				mask = mask << 1;
			}

		}
		// the same logic as above applies to this section.
		else if (message.getEventType() == GPIOEvent.DO) {

			int newData = message.getData();
			int mask = 1;
			for (int i = 0; i < 4; i++) {
				int newDataBit = newData & mask;
				int oldDataBit = oldExternalOutput & mask;
				if (newDataBit != oldDataBit) {
					GPOEvent event = new GPOEvent(readerID, i + 1,
							newDataBit == 1);
					events.add(event);
				}
				mask = mask << 1;
			}

		}
		return events;
	}

}

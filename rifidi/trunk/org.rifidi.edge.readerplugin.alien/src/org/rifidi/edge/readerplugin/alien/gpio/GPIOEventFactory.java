/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.gpio;

import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.services.notification.data.gpio.GPIEvent;
import org.rifidi.edge.readerplugin.alien.gpio.messages.AlienGPIOMessage;
import org.rifidi.edge.readerplugin.alien.gpio.messages.GPIOEvent;

/**
 * @author kyle
 * 
 */
public class GPIOEventFactory {

	public String readerID;

	public GPIOEventFactory(String readerID) {
		this.readerID = readerID;
	}

	public Set<Object> getEvent(int oldExternalInput, int oldExternalOutput,
			AlienGPIOMessage message) {
		Set<Object> events = new HashSet<Object>();
		if (message.getEventType() == GPIOEvent.DI) {
			int newData = message.getData();
			int mask = 1;
			for (int i = 0; i < 4; i++) {
				int newDataBit = newData & mask;
				int oldDataBit = oldExternalInput & mask;
				if (newDataBit != oldDataBit) {
					GPIEvent event = new GPIEvent(readerID, i + 1,
							(newDataBit != 0));
					events.add(event);
				}
				mask = mask << 1;
			}

		} else if (message.getEventType() == GPIOEvent.DO) {

			int newData = message.getData();
			int mask = 1;
			for (int i = 0; i < 4; i++) {
				int newDataBit = newData & mask;
				int oldDataBit = oldExternalOutput & mask;
				if (newDataBit != oldDataBit) {
					// GPOEvent event = new GPOEvent(readerID, i + 1,
					// newDataBit == 1);
					// events.add(event);
				}
				mask = mask << 1;
			}

		}
		return events;
	}

}

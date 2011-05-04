/**
 * 
 */
package org.rifidi.edge.adapter.thingmagic6.commands;

import java.util.concurrent.TimeoutException;

import org.rifidi.edge.adapter.thingmagic6.Thingmagic6SensorSession;
import org.rifidi.edge.sensors.TimeoutCommand;

/**
 * 
 * 
 * @author Matthew Dean (matt@pramari.com)
 */
public class Thingmagic6PushCommand extends TimeoutCommand {

	Thingmagic6SensorSession session = null;
	
	public Thingmagic6PushCommand(String commandID) {
		super(commandID);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		this.session = (Thingmagic6SensorSession) this.sensorSession;
		// try {
		//					
		// }
	}

}

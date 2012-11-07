/**
 * 
 */
package org.rifidi.edge.adapter.thinkify50;

import java.util.concurrent.TimeoutException;

import org.rifidi.edge.sensors.TimeoutCommand;

/**
 * @author matt
 * 
 */
public class Thinkify50PushCommand extends TimeoutCommand {

	Thinkify50SensorSession session = null;

	public Thinkify50PushCommand(String commandID) {
		super(commandID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		this.session = (Thinkify50SensorSession) this.sensorSession;
		session.startReading();
	}

}

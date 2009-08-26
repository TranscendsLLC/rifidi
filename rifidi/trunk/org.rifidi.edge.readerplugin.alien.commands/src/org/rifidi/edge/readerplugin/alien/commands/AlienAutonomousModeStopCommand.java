/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.readerplugin.alien.AbstractAlien9800Command;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienCommandObject;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienException;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienSetCommandObject;

/**
 * A command to send to the Alien Reader that turns autonomous mode off
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienAutonomousModeStopCommand extends AbstractAlien9800Command {

	private static final Log logger = LogFactory
			.getLog(AlienAutonomousModeStopCommand.class);

	public AlienAutonomousModeStopCommand(String commandID) {
		super(commandID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		AlienCommandObject setNotifyMode = new AlienSetCommandObject(
				"notifyMode", "off",
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setAutoMode = new AlienSetCommandObject("automode",
				"off", (Alien9800ReaderSession) this.sensorSession);
		try {
			setNotifyMode.execute();
			setAutoMode.execute();
		} catch (IOException e) {
			logger.warn("Exception while executing command: ", e);
		} catch (AlienException e) {
			logger.warn("Exception while executing command: ", e);
		} catch (Exception e) {
			logger.warn("Exception while executing command: ", e);
		}

	}

}

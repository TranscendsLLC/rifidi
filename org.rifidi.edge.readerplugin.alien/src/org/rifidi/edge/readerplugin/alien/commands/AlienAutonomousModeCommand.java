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
 * A command that configures an Alien reader to send out tag reads in autonomous
 * mode
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienAutonomousModeCommand extends AbstractAlien9800Command {

	private static final Log logger = LogFactory
			.getLog(AlienAutonomousModeCommand.class);
	private String notifyTrigger;
	private String notifyAddress;
	private String notifyTime;
	private String autoWaitOutput;
	private String autoStartTrigger;
	private String autoStartPause;
	private String autoWorkOutput;
	private String autoStopTrigger;
	private String autoStopTimer;
	private String autoStopPause;
	private String autoTrueOutput;
	private String autoTruePause;
	private String autoFalseOutput;
	private String autoFalsePause;

	/**
	 * Construct a new Command
	 * 
	 * @param commandID
	 */
	public AlienAutonomousModeCommand(String commandID) {
		super(commandID);
	}

	@Override
	public void run() {
		// set up all command objects
		AlienCommandObject setNotifyTrigger = new AlienSetCommandObject(
				"notifyTrigger", this.notifyTrigger,
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setNotifyAddress = new AlienSetCommandObject(
				"notifyAddress", notifyAddress,
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setNotifyTime = new AlienSetCommandObject(
				"notifyTime", notifyTime);
		AlienCommandObject setAutoWaitOutput = new AlienSetCommandObject(
				"autowaitoutput", autoWaitOutput,
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setAutoStartTrigger = new AlienSetCommandObject(
				"autostarttrigger", autoStartTrigger,
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setAutoStartPause = new AlienSetCommandObject(
				"autostartpause", autoStartPause,
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setAutoWorkOutput = new AlienSetCommandObject(
				"autoWorkOutput", autoWorkOutput,
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setAutoStopTrigger = new AlienSetCommandObject(
				"autoStopTrigger", autoStopTrigger,
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setStopTimer = new AlienSetCommandObject(
				"autoStopTimer", autoStopTimer,
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setAutoStopPause = new AlienSetCommandObject(
				"autoStopPause", autoStopPause,
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setAutoTrueOutput = new AlienSetCommandObject(
				"autoTrueOutput", autoTrueOutput,
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setAutoTruePuase = new AlienSetCommandObject(
				"autoTruePause", autoTruePause,
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setAutoFalseOutput = new AlienSetCommandObject(
				"autoFalseOutput", autoFalseOutput,
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setAutoFalsePause = new AlienSetCommandObject(
				"autoFalsePause", autoFalsePause,
				(Alien9800ReaderSession) this.sensorSession);

		AlienCommandObject setNotifyFormat = new AlienSetCommandObject(
				"notifyFormat", "text",
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setAutoAction = new AlienSetCommandObject(
				"autoAction", "acquire",
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setNotifyMode = new AlienSetCommandObject(
				"notifyMode", "on", (Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setAutoMode = new AlienSetCommandObject("automode",
				"off", (Alien9800ReaderSession) this.sensorSession);

		// execute all command objects
		try {
			setNotifyTrigger.execute();
			setNotifyAddress.execute();
			setNotifyFormat.execute();
			setNotifyTime.execute();
			setAutoWaitOutput.execute();
			setAutoStartTrigger.execute();
			setAutoStartPause.execute();
			setAutoWorkOutput.execute();
			setAutoStopTrigger.execute();
			setStopTimer.execute();
			setAutoStopPause.execute();
			setAutoTrueOutput.execute();
			setAutoTruePuase.execute();
			setAutoFalseOutput.execute();
			setAutoFalsePause.execute();
			setAutoAction.execute();
			setNotifyMode.execute();
			setAutoMode.execute();
		} catch (IOException e) {
			logger.warn("Exception while executing command: " + e);
		} catch (AlienException e) {
			logger.warn("Exception while executing command: " + e);
		}

	}

	/**
	 * @param notifyTrigger
	 *            the notifyTrigger to set
	 */
	void setNotifyTrigger(String notifyTrigger) {
		this.notifyTrigger = notifyTrigger;
	}

	/**
	 * @param notifyAddress
	 *            the notifyAddress to set
	 */
	void setNotifyAddress(String notifyAddress) {
		this.notifyAddress = notifyAddress;
	}

	/**
	 * @param notifyTime the notifyTime to set
	 */
	void setNotifyTime(String notifyTime) {
		this.notifyTime = notifyTime;
	}

	/**
	 * @param autoWaitOutput
	 *            the autoWaitOutput to set
	 */
	void setAutoWaitOutput(String autoWaitOutput) {
		this.autoWaitOutput = autoWaitOutput;
	}

	/**
	 * @param autoStartTrigger
	 *            the autoStartTrigger to set
	 */
	void setAutoStartTrigger(String autoStartTrigger) {
		this.autoStartTrigger = autoStartTrigger;
	}

	/**
	 * @param autoStartPause
	 *            the autoStartPause to set
	 */
	void setAutoStartPause(String autoStartPause) {
		this.autoStartPause = autoStartPause;
	}

	/**
	 * @param autoWorkOutput
	 *            the autoWorkOutput to set
	 */
	void setAutoWorkOutput(String autoWorkOutput) {
		this.autoWorkOutput = autoWorkOutput;
	}

	/**
	 * @param autoStopTrigger
	 *            the autoStopTrigger to set
	 */
	void setAutoStopTrigger(String autoStopTrigger) {
		this.autoStopTrigger = autoStopTrigger;
	}

	/**
	 * @param autoStopTimer
	 *            the autoStopTimer to set
	 */
	void setAutoStopTimer(String autoStopTimer) {
		this.autoStopTimer = autoStopTimer;
	}

	/**
	 * @param autoStopPause
	 *            the autoStopPause to set
	 */
	void setAutoStopPause(String autoStopPause) {
		this.autoStopPause = autoStopPause;
	}

	/**
	 * @param autoTrueOutput
	 *            the autoTrueOutput to set
	 */
	void setAutoTrueOutput(String autoTrueOutput) {
		this.autoTrueOutput = autoTrueOutput;
	}

	/**
	 * @param autoTruePause
	 *            the autoTruePause to set
	 */
	void setAutoTruePause(String autoTruePause) {
		this.autoTruePause = autoTruePause;
	}

	/**
	 * @param autoFalseOutput
	 *            the autoFalseOutput to set
	 */
	void setAutoFalseOutput(String autoFalseOutput) {
		this.autoFalseOutput = autoFalseOutput;
	}

	/**
	 * @param autoFalsePause
	 *            the autoFalsePause to set
	 */
	void setAutoFalsePause(String autoFalsePause) {
		this.autoFalsePause = autoFalsePause;
	}
}

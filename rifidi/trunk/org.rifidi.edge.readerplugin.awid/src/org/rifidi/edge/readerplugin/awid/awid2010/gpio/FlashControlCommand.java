/**
 * 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.gpio;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.commands.Command;
import org.rifidi.edge.core.sensors.messages.ByteMessage;

/**
 * This is the Command that handles the 'Flash On/Off Control'. It sends the
 * flash command then listens for the ACK and the response
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class FlashControlCommand extends Command {

	/** The flash command to send to the AWID */
	private GPOFlashCommand awidCommand;
	private static final Log logger = LogFactory
			.getLog(FlashControlCommand.class);

	/**
	 * Constructor
	 * 
	 * @param awidCommand
	 *            The flash command to send to the AWID.
	 */
	public FlashControlCommand(GPOFlashCommand awidCommand) {
		super("AWID Flash Control Command");
		this.awidCommand = awidCommand;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// Get the session
		AwidGPIOSession session = (AwidGPIOSession) super.sensorSession;
		try {
			// send the flash command
			session.sendMessage(awidCommand);

			// receive the ACK
			ByteMessage ack = session.receiveMessage(5000);
			if (ack.message[0] == 0x00) {

				// receive the Response
				GPOFlashCommandResponse response = new GPOFlashCommandResponse(
						session.receiveMessage(30000).message);
				// handle errors
				if (!response.flashSuceeded()) {
					if (response.pinBusy()) {
						logger.warn("The command did not succeed because "
								+ "the pin is busy: " + awidCommand);
					} else {
						logger.error("Command Error: " + awidCommand);
					}
				}
			} else {
				logger.error("ACK for " + awidCommand + " was 0xFF");
			}
		} catch (IOException e) {
			logger.error("IOException when sending " + awidCommand);
		}

	}
}

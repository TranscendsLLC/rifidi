/**
 * 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.gpio;

import java.io.IOException;
import java.util.BitSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.commands.Command;
import org.rifidi.edge.core.sensors.messages.ByteMessage;

/**
 * This is the Command that handles the 'Output Command'. It sends the output
 * command then listens for the ACK and the response
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SetOutputPortCommand extends Command {

	/** The GPO Ports */
	private BitSet ports;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(SetOutputPortCommand.class);

	/**
	 * Constructor
	 * 
	 * @param ports
	 *            the BitSet that represents the GPO bits to set
	 */
	public SetOutputPortCommand(BitSet ports) {
		super("AWID Set Output Ports");
		this.ports = ports;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// get the session
		AwidGPIOSession session = (AwidGPIOSession) super.sensorSession;
		try {
			// for each GPO bit
			for (int i = 0; i < 4; i++) {
				// send the output command for the GPO
				GPOOutputCommand awidCommand = new GPOOutputCommand(i, ports
						.get(i));
				session.sendMessage(awidCommand);

				// get the ack
				ByteMessage ack = session.receiveMessage(5000);
				if (ack.message[0] == 0x00) {
					// Get the response
					GPOOutputResponse outputResponse = new GPOOutputResponse(
							session.receiveMessage(5000).message);
					if (!outputResponse.GPOSetSucceeded()) {
						logger.error("Command Error: " + awidCommand);
					}
				} else {
					logger.error("ACK for " + awidCommand + " was 0xFF");
				}
			}
		} catch (IOException e) {
			logger.error("IOException when sending output command");
		}

	}
}

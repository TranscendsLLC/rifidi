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
 * This is the Command that handles the 'Status Command'. It sends the Status
 * Command then listens for the ACK and the response
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TestInputPortsCommand extends Command {

	/** The logger fort this class */
	private static Log logger = LogFactory.getLog(TestInputPortsCommand.class);
	/** The GPI ports to set */
	private BitSet gpiPorts;

	/**
	 * Constructor
	 * 
	 * @param gpiPorts
	 *            the GPI ports to configure
	 */
	public TestInputPortsCommand(BitSet gpiPorts) {
		super("AWID Test Input Ports");
		this.gpiPorts = gpiPorts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			// get the session
			AwidGPIOSession session = (AwidGPIOSession) super.sensorSession;
			GPIOStatusCommand awidCommand = new GPIOStatusCommand();
			// send the command
			session.sendMessage(awidCommand);
			// recieve the ack
			ByteMessage ack = session.receiveMessage(5000);
			if (ack.message[0] == 0x00) {
				// get the response
				GPIOStatusResponse gpioStatusMessage = new GPIOStatusResponse(
						session.receiveMessage(5000).message);
				BitSet status = gpioStatusMessage.getInputStatus();

				synchronized (gpiPorts) {
					// clear the current bits
					gpiPorts.clear();
					// set the status of the bits according to the status
					gpiPorts.or(status);
				}

			} else {
				logger.error("ACK for " + awidCommand + " was 0xFF");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

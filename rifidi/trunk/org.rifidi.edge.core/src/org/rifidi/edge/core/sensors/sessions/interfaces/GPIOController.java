/**
 * 
 */
package org.rifidi.edge.core.sensors.sessions.interfaces;

import java.util.BitSet;

/**
 * This interface can be implemented by SensorSessions that have General Purpose
 * IO Capabilities. It gives a common interface to allow Sensor Sessions to
 * easily Test GP Input ports and change the values on General Purpose Output
 * Ports
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface GPIOController {

	/**
	 * Use this method to set the value of a specified output port
	 * 
	 * @param ports
	 *            The ports to set to high. Each bit in the BitSet represents a
	 *            port, with bit 0 representing the lowest-numbered port. Each
	 *            bit that is set causes the port which it represents to be set
	 *            to high.
	 */
	public void setOutputPort(final BitSet ports) throws CannotExecuteException;
	
	/**
	 * Use this method to test an input port.
	 * 
	 * @param port
	 *            the port to test
	 * @return true if the port is high, false if it is low
	 * @throws CannotExecuteException
	 *             If there is a problem when executing the request.
	 */
	public boolean testInputPort(final int port) throws CannotExecuteException;

}

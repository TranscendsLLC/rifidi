/**
 * 
 */
package org.rifidi.edge.core.sensors.management;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.daos.ReaderDAO;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;
import org.rifidi.edge.core.sensors.sessions.AbstractSensorSession;

/**
 * This class is a service to be implemented by readers plugins which have GPIO
 * capabilities. It should be exported to the osgi registry under this interface
 * and should be exported with a service property called 'reader' which is the
 * ID of the ReaderFactory it is used with. In addition spring should inject the
 * ReaderDAO.
 * 
 * 
 * 
 * @author Kyle Neumeier
 * 
 */
public abstract class AbstractGPIOService<T extends AbstractSensorSession> {

	/** The readerDAO */
	private ReaderDAO readerDAO;

	/**
	 * This method sets the output ports on a reader
	 * 
	 * @param readerID
	 *            The readerID of the reader
	 * @param ports
	 *            The ports to set high. All other ports will be set low. Port 0
	 *            is the lowest port.
	 * @throws CannotExecuteException
	 *             If the ports cannot be set for some reason.
	 */
	public abstract void setGPO(String readerID, Collection<Integer> ports)
			throws CannotExecuteException;

	/**
	 * This method sets the output ports on a reader
	 * 
	 * @param readerID
	 *            The readerID of the reader
	 * @param ports
	 *            The ports to set high. All other ports will be set low. Port 0
	 *            is the lowest port.
	 * @throws CannotExecuteException
	 *             If the ports cannot be set for some reason.
	 */
	public void setGPO(String readerID, Integer... ports)
			throws CannotExecuteException {
		Set<Integer> set = new HashSet<Integer>();
		for (Integer i : ports) {
			set.add(i);
		}
		setGPO(readerID, set);
	}

	/**
	 * This method flashes External Output to high for a specified number of
	 * seconds.
	 * 
	 * @param readerID
	 *            The readerID of the reader
	 * @param flashTime
	 *            The time in seconds to hold the ports high
	 * @param ports
	 *            The ports to flash. Port 0 is the lowest port
	 * @throws CannotExecuteException
	 *             If the ports cannot be flashed for some reason
	 */
	public abstract void flashGPO(String readerID, int flashTime,
			Set<Integer> ports) throws CannotExecuteException;

	/**
	 * This method flashes External Output to high for a specified number of
	 * seconds.
	 * 
	 * @param readerID
	 *            The readerID of the reader
	 * @param flashTime
	 *            The time in seconds to hold the ports high
	 * @param ports
	 *            The ports to flash. Port 0 is the lowest port.
	 * @throws CannotExecuteException
	 *             If the ports cannot be flashed for some reason
	 */
	public void flashGPO(String readerID, int flashTime, Integer... ports)
			throws CannotExecuteException {
		Set<Integer> set = new HashSet<Integer>();
		for (Integer i : ports) {
			set.add(i);
		}
		flashGPO(readerID, flashTime, set);
	}

	/**
	 * Tests the current state of the port
	 * 
	 * @param readerID
	 *            The readerID of the reader
	 * @param port
	 *            The port to test. Port 0 represents the lowest port
	 * @return true if the port is high, false if it is low
	 * @throws CannotExecuteException
	 */
	public abstract boolean testGPI(String readerID, int port)
			throws CannotExecuteException;

	/**
	 * A private helper method to get the required session
	 * 
	 * @param readerID
	 * @return
	 * @throws CannotExecuteException
	 */
	protected T getSession(String readerID) throws CannotExecuteException {
		if (readerDAO == null) {
			throw new CannotExecuteException("Reader DAO is null");
		}
		AbstractSensor<?> sensor = readerDAO.getReaderByID(readerID);
		if (sensor == null) {
			throw new CannotExecuteException("Sensor with ID " + readerID
					+ " is not available");
		}
		for (SensorSession session : sensor.getSensorSessions().values()) {
			if (session instanceof AbstractSensorSession)
				return (T) session;
		}
		throw new CannotExecuteException("No session on " + readerID
				+ " can be found");
	}

	/**
	 * @param readerDAO
	 *            the readerDAO to set
	 */
	public void setReaderDAO(ReaderDAO readerDAO) {
		this.readerDAO = readerDAO;
	}

	/**
	 * Returns true if a reader with the given ID is available to this service.
	 * 
	 * @param readerID
	 * @return
	 */
	public boolean isReaderAvailable(String readerID) {
		return readerDAO.getReaderByID(readerID) != null;
	}

}

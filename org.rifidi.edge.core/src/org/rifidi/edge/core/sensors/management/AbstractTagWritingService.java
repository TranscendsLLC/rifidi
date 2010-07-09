/**
 * 
 */
package org.rifidi.edge.core.sensors.management;

import org.rifidi.edge.core.daos.ReaderDAO;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;
import org.rifidi.edge.core.sensors.sessions.AbstractSensorSession;

/**
 * @author kyle
 * 
 */
public abstract class AbstractTagWritingService<T extends AbstractSensorSession> {

	/** The readerDAO */
	private ReaderDAO readerDAO;

	public abstract void writeUser(String readerID, int antenna, byte[] data)
			throws CannotExecuteException;

	public abstract void writeEPC(String readerID, int antenna, byte[] data)
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

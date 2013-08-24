/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.sensors;

import org.rifidi.edge.daos.ReaderDAO;
import org.rifidi.edge.sensors.sessions.AbstractSensorSession;

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

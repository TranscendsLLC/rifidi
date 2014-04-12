/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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

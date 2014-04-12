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
/**
 * 
 */
package org.rifidi.edge.api;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class CommandDTO implements Serializable {
	private Long interval;
	private TimeUnit timeUnit;
	private Integer processID;
	private String commandID;

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public CommandDTO() {
	}

	/**
	 * @param interval
	 * @param timeUnit
	 * @param processID
	 * @param commandID
	 */
	public CommandDTO(Long interval, TimeUnit timeUnit, Integer processID,
			String commandID) {
		this.interval = interval;
		this.timeUnit = timeUnit;
		this.processID = processID;
		this.commandID = commandID;
	}

	/**
	 * @return the timeUnit
	 */
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	/**
	 * @param timeUnit
	 *            the timeUnit to set
	 */
	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	/**
	 * @return the repeat
	 */
	public Boolean getRepeat() {
		return interval > 0;
	}

	/**
	 * @return the interval
	 */
	public Long getInterval() {
		return interval;
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(Long interval) {
		this.interval = interval;
	}

	/**
	 * @return the commandID
	 */
	public String getCommandID() {
		return commandID;
	}

	/**
	 * @param commandID
	 *            the commandID to set
	 */
	public void setCommandID(String commandID) {
		this.commandID = commandID;
	}

	/**
	 * @return the processID
	 */
	public Integer getProcessID() {
		return processID;
	}

	/**
	 * @param processID
	 *            the processID to set
	 */
	public void setProcessID(Integer processID) {
		this.processID = processID;
	}

}

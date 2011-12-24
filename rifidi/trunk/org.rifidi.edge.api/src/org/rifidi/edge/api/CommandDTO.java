/*
 * CommandDTO.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
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

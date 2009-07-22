/**
 * 
 */
package org.rifidi.edge.api.rmi.dto;

import java.util.concurrent.TimeUnit;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class CommandDTO {
	private Boolean repeat;
	private Long interval;
	private TimeUnit timeUnit;
	private Integer processID;
	private String commandID;

	/**
	 * Constructor.
	 */
	public CommandDTO() {
	}

	/**
	 * @param repeat
	 * @param interval
	 * @param timeUnit
	 * @param processID
	 * @param commandID
	 */
	public CommandDTO(Boolean repeat, Long interval, TimeUnit timeUnit,
			Integer processID, String commandID) {
		this.repeat = repeat;
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
		return repeat;
	}

	/**
	 * @param repeat
	 *            the repeat to set
	 */
	public void setRepeat(Boolean repeat) {
		this.repeat = repeat;
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

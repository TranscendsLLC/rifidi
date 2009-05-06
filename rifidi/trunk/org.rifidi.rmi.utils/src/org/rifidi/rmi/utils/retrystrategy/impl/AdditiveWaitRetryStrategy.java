/*
 *  AdditiveWaitRetryStrategy.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */
//TODO: Comments
package org.rifidi.rmi.utils.retrystrategy.impl;

import org.rifidi.rmi.utils.retrystrategy.RetryStrategy;

/**
 * This class was inspired by the three oreilly.com articles on RMI by William
 * Grosso.
 * 
 * The most commonly used retry strategy; it extends the waiting period by a
 * constant amount with each retry. Note that the default version of this (e.g.
 * the one with a zero argument constructor) will make 3 calls and wind up
 * waiting approximately 11 seconds (zero wait for the first call, 3 seconds for
 * the second call, and 8 seconds for the third call). These wait times are
 * pretty small, and are usually dwarfed by socket timeouts when network
 * difficulties occur anyway.
 * 
 * Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AdditiveWaitRetryStrategy extends RetryStrategy {
	public static final long STARTING_WAIT_TIME = 500;

	public static final long WAIT_TIME_INCREMENT = 5000;

	private long _currentTimeToWait;
	private long _waitTimeIncrement;

	public AdditiveWaitRetryStrategy() {
		this(DEFAULT_NUMBER_OF_RETRIES, STARTING_WAIT_TIME, WAIT_TIME_INCREMENT);
	}

	public AdditiveWaitRetryStrategy(int numberOfRetries,
			long startingWaitTime, long waitTimeIncrement) {
		super(numberOfRetries);
		_currentTimeToWait = startingWaitTime;

		_waitTimeIncrement = waitTimeIncrement;

	}

	protected long getTimeToWait() {
		long returnValue = _currentTimeToWait;

		_currentTimeToWait += _waitTimeIncrement;

		return returnValue;

	}
}

/*
 *  ExponentialBackoffRetryStrategy.java
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
 * The classic
 * "if it doesn't get fixed in n seconds, wait 2n seconds and try again"
 * strategy. Using a large number of retries in this one results in enormously
 * long delays. You probably don;t want to use an
 * ExponentialBackoffRetryStrategy in a thread which needs to be responsive
 * (e.g. in the Swing event handling thread).
 * 
 * Kyle Neumeier - kyle@pramari.com
 * 
 */

public class ExponentialBackoffRetryStrategy extends RetryStrategy {
	public static final long STARTING_WAIT_TIME = 3000;

	private long _currentTimeToWait;

	public ExponentialBackoffRetryStrategy() {
		this(DEFAULT_NUMBER_OF_RETRIES, STARTING_WAIT_TIME);
	}

	public ExponentialBackoffRetryStrategy(int numberOfRetries,
			long startingWaitTime) {
		super(numberOfRetries);
		_currentTimeToWait = startingWaitTime;
	}

	protected long getTimeToWait() {
		long returnValue = _currentTimeToWait;
		_currentTimeToWait *= 2;
		return returnValue;
	}
}
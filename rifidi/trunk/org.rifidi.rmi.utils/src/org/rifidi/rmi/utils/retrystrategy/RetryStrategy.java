/*
 *  RetryStrategy.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */

package org.rifidi.rmi.utils.retrystrategy;

import org.rifidi.rmi.utils.exceptions.RetryException;

/**
 * This class was inspired by the three oreilly.com articles on RMI by William
 * Grosso.
 * 
 * Abstract base class for retry strategies.
 */

/**
 * Kyle Neumeier - kyle@pramari.com
 *
 */
public abstract class RetryStrategy {
	public static final int DEFAULT_NUMBER_OF_RETRIES = 2;
	private int _numberOfTriesLeft;

	public RetryStrategy() {
		this(DEFAULT_NUMBER_OF_RETRIES);
	}

	public RetryStrategy(int numberOfRetries) {
		_numberOfTriesLeft = numberOfRetries;
	}

	public boolean shouldRetry() {
		return (0 < _numberOfTriesLeft);
	}

	public void remoteExceptionOccurred() throws RetryException {
		_numberOfTriesLeft--;
		if (!shouldRetry()) {
			throw new RetryException();
		}
		waitUntilNextTry();
	}

	protected abstract long getTimeToWait();

	private void waitUntilNextTry() {
		long timeToWait = getTimeToWait();
		try {
			Thread.sleep(timeToWait);
		} catch (InterruptedException ignored) {
		}
	}
}
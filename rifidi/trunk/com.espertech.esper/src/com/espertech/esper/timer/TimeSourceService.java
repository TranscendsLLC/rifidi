/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.timer;

/**
 * Allow for different strategies for getting VM (wall clock) time.
 * See JIRA issue ESPER-191 Support nano/microsecond resolution for more
 * information on Java system time-call performance, accuracy and drift.
 * @author Jerry Shea
 */
public class TimeSourceService
{
	private static final long MICROS_TO_MILLIS = 1000;
	private static final long NANOS_TO_MICROS = 1000;

    /**
     * A public variable indicating whether to use the System millisecond time or
     * nano time, to be configured through the engine settings.
     */
    public static boolean IS_SYSTEM_CURRENT_TIME = true;

    private final long wallClockOffset;
    private final String description;

    /**
     * Ctor.
     */
    public TimeSourceService()
    {
        this.wallClockOffset = System.currentTimeMillis() * MICROS_TO_MILLIS - this.getTimeMicros();
        this.description = String.format("%s: resolution %d microsecs",
                           this.getClass().getSimpleName(), this.calculateResolution());
    }

    /**
	 * Convenience method to get time in milliseconds
	 * @return wall-clock time in milliseconds
	 */
	public long getTimeMillis() {
        if (IS_SYSTEM_CURRENT_TIME)
        {
            return System.currentTimeMillis();
        }
        return getTimeMicros() / MICROS_TO_MILLIS;
	}

    private long getTimeMicros() {
        return (System.nanoTime() / NANOS_TO_MICROS) + wallClockOffset;
    }


    /**
	 * Calculate resolution of this timer in microseconds i.e. what is the resolution
	 * of the underlying platform's timer.
	 * @return timer resolution
	 */
	protected long calculateResolution() {
		final int LOOPS = 5;
        long totalResolution = 0;
		long time = this.getTimeMicros(), prevTime = time;
        for (int i = 0; i < LOOPS; i++) {
            // wait until time changes
            while (time == prevTime)
                time = this.getTimeMicros();
            totalResolution += (time - prevTime);
			prevTime = time;
        }
		return totalResolution / LOOPS;
	}

	@Override
	public String toString() {
		return description;
	}
}

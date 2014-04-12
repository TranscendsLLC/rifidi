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
package org.rifidi.edge.tools.diagnostics;

import java.util.List;
import java.util.Properties;

import com.espertech.esper.client.EPRuntime;

/**
 * An exposure defines how tags from the TagSet are put into esper as events.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class Exposure extends Properties {

	/***/
	private static final long serialVersionUID = 1L;

	/*
	 * Properties that are common to all exposures
	 */

	/** The type of exposure */
	public static final String PROPERTY_TYPE = "exposureType";
	/** A delay Exposure type - 'delay' */
	public static final String EXPOSURE_TYPE_DELAY = "delay";
	/** A rate exposure type - 'rate' */
	public static final String EXPOSURE_TYPE_RATE = "rate";
	/**
	 * A trigger to stop the runner based on the number of tags sent to esper
	 * 'stop.count'
	 */
	public static final String PROPERTY_STOPCOUNT = "stop.count";
	/** A trigger to stop the runner based on a timer - 'stop.timer' */
	public static final String PROPERTY_STOPTIMER = "stop.timer";
	/**
	 * Whether or not tags should be selected from the tag set at random -
	 * 'random'
	 */
	public static final String PROPERTY_RANDOM = "random";

	/**
	 * @param defaults
	 */
	public Exposure(Properties defaults) {
		super(defaults);
	}

	/**
	 * Get the type of this exposure
	 * 
	 * @return
	 */
	public String getExposureType() {
		return getProperty(PROPERTY_TYPE);
	}

	/***
	 * Get the number of tags we should send to esper before stopping.
	 * 
	 * @return
	 */
	public int getTagCountStopTrigger() {
		String val = getProperty(PROPERTY_STOPCOUNT);
		if (val != null && !val.isEmpty()) {
			return Integer.parseInt(val);
		} else {
			return 0;
		}
	}

	/***
	 * Get the length of time this runner should run until it stops
	 * 
	 * @return
	 */
	public long getTimerStopTrigger() {
		String val = getProperty(PROPERTY_STOPTIMER);
		if (val != null && !val.isEmpty()) {
			return Long.parseLong(val);
		} else {
			return 0;
		}
	}

	/**
	 * Whether or not this exposure selects from the tag list at random
	 * 
	 * @return
	 */
	public boolean getRandom() {
		return getProperty(PROPERTY_RANDOM).equalsIgnoreCase("true");
	}

	/**
	 * Create a concrete runner for this Exposure
	 * 
	 * @param tags
	 * @param runtime
	 * @return
	 */
	public abstract ExposureRunner<?> createRunner(
			List<AbstractReadData<?>> tags, EPRuntime runtime);

}

/*
 * 
 * RifidiBoundarySpec.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.wrappers;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.ale.esper.timer.Trigger;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECBoundarySpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;

/**
 * This class parses a boundary spec and extracts the required values.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class RifidiBoundarySpec {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(RifidiBoundarySpec.class);
	/** Start and stop triggers. */
	private Set<Trigger> startTriggers;
	private Set<Trigger> stopTriggers;
	/**
	 * Start the next spec of this type after this amount of time has passed
	 * since the start of the last one.
	 */
	private long repeatInterval;
	/** Stop processing of the spec after this time has expired. */
	private long duration;
	/** If no tags arrive within this interval stop processing. */
	private long stableSetInterval;
	/** Kill the spec as soon as data becomes available. */
	private boolean whenDataAvailable;

	/**
	 * Constructor.
	 * 
	 * @param boundaryspec
	 * @param triggerFactoryService
	 * @throws ECSpecValidationExceptionResponse
	 * @throws InvalidURIExceptionResponse
	 */
	public RifidiBoundarySpec(ECBoundarySpec boundaryspec)
			throws ECSpecValidationExceptionResponse,
			InvalidURIExceptionResponse {
		startTriggers = new HashSet<Trigger>();
		// collect start triggers
		if (boundaryspec.getStartTrigger() != null
				&& !"".equals(boundaryspec.getStartTrigger())) {
			Trigger trig = new Trigger(boundaryspec
					.getStartTrigger());
			trig.setStart(true);
			startTriggers.add(trig);
		}
		if (boundaryspec.getExtension() != null
				&& boundaryspec.getExtension().getStartTriggerList() != null) {
			for (String uri : boundaryspec.getExtension().getStartTriggerList()
					.getStartTrigger()) {
				Trigger trig = new Trigger(uri);
				trig.setStart(true);
				startTriggers.add(trig);
			}
		}
		// collect stop triggers
		stopTriggers = new HashSet<Trigger>();
		if (boundaryspec.getStopTrigger() != null
				&& !"".equals(boundaryspec.getStopTrigger())) {
			Trigger trig = new Trigger(boundaryspec
					.getStopTrigger());
			trig.setStart(false);
			stopTriggers.add(trig);
		}
		if (boundaryspec.getExtension() != null
				&& boundaryspec.getExtension().getStopTriggerList() != null) {
			for (String uri : boundaryspec.getExtension().getStopTriggerList()
					.getStopTrigger()) {
				Trigger trig = new Trigger(uri);
				trig.setStart(false);
				stopTriggers.add(trig);
			}
		}

		// get the interval between the starts of two ecspecs
		if (boundaryspec.getRepeatPeriod() != null) {
			if (!"MS".equals(boundaryspec.getRepeatPeriod().getUnit())) {
				throw new ECSpecValidationExceptionResponse(
						"Only MS is accepted as time unit. Got "
								+ boundaryspec.getRepeatPeriod().getUnit());
			}
			repeatInterval = boundaryspec.getRepeatPeriod().getValue();
		}
		// time after a spec gets killed
		if (boundaryspec.getDuration() != null) {
			if (!"MS".equals(boundaryspec.getDuration().getUnit())) {
				throw new ECSpecValidationExceptionResponse(
						"Only MS is accepted as time unit. Got "
								+ boundaryspec.getDuration().getUnit());
			}
			duration = boundaryspec.getDuration().getValue();
		}
		// time after a spec gets killed if no tags have arrived
		if (boundaryspec.getStableSetInterval() != null) {
			if (!"MS".equals(boundaryspec.getStableSetInterval().getUnit())) {
				throw new ECSpecValidationExceptionResponse(
						"Only MS is accepted as time unit. Got "
								+ boundaryspec.getStableSetInterval().getUnit());
			}
			stableSetInterval = boundaryspec.getStableSetInterval().getValue();
		}

		// check if the time values are valid line 2197
		if (duration < 0) {
			throw new ECSpecValidationExceptionResponse(
					"Duration is smaller than 0.");
		}
		if (stableSetInterval < 0) {
			throw new ECSpecValidationExceptionResponse(
					"Stable set interval is smaller than 0.");
		}
		if (repeatInterval < 0) {
			throw new ECSpecValidationExceptionResponse(
					"Repeat interval is smaller than 0.");
		}
		// when data available indicates if the spec should stop as soon as the
		// first dataset has arrived
		if (boundaryspec.getExtension() != null) {
			whenDataAvailable = boundaryspec.getExtension()
					.isWhenDataAvailable();
		} else {
			whenDataAvailable = false;
		}
		// check if we can stop line 2203
		if (stopTriggers.size() == 0 && duration == 0 && stableSetInterval == 0
				&& !whenDataAvailable) {

		}
	}

	/**
	 * @return the startTriggers
	 */
	public Set<Trigger> getStartTriggers() {
		return startTriggers;
	}

	/**
	 * @return the stopTriggers
	 */
	public Set<Trigger> getStopTriggers() {
		return stopTriggers;
	}

	/**
	 * @return the repeatInterval
	 */
	public long getRepeatInterval() {
		return repeatInterval;
	}

	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * @return the stableSetInterval
	 */
	public long getStableSetInterval() {
		return stableSetInterval;
	}

	/**
	 * @return the whenDataAvailable
	 */
	public boolean isWhenDataAvailable() {
		return whenDataAvailable;
	}

}

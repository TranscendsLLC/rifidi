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


import com.espertech.esper.client.EPRuntime;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RateExposureRunner extends ExposureRunner<RateExposure> {

	private final long delayTime;
	private final int groupSize;

	/**
	 * @param epRuntime
	 * @param exposure
	 * @param tags
	 */
	public RateExposureRunner(EPRuntime epRuntime, RateExposure exposure,
			List<AbstractReadData<?>> tags) {
		super(epRuntime, exposure, tags);
		int rate = exposure.getTagRate();
		delayTime = Math.max(100, 1000 / rate);
		int numDelaysPerSec = 1000 / (int) delayTime;
		groupSize = rate / numDelaysPerSec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.diagnostics.tags.generator.ExposureRunner#calculateDelay
	 * (org.rifidi.edge.diagnostics.tags.generator.Exposure)
	 */
	@Override
	protected long calculateDelay() {
		return delayTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.diagnostics.tags.generator.ExposureRunner#calculateGroupSize
	 * (org.rifidi.edge.diagnostics.tags.generator.Exposure, java.util.List)
	 */
	@Override
	protected int calculateGroupSize() {
		return groupSize;
	}

}

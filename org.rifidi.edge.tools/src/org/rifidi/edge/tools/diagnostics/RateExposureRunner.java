/**
 * 
 */
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

/**
 * 
 */
package org.rifidi.edge.app.diag.tags.generator.exposures;

import java.util.List;

import org.rifidi.edge.app.diag.tags.generator.AbstractReadData;
import org.rifidi.edge.app.diag.tags.generator.ExposureRunner;

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
	 * org.rifidi.edge.app.diag.tags.generator.ExposureRunner#calculateDelay
	 * (org.rifidi.edge.app.diag.tags.generator.Exposure)
	 */
	@Override
	protected long calculateDelay() {
		return delayTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.app.diag.tags.generator.ExposureRunner#calculateGroupSize
	 * (org.rifidi.edge.app.diag.tags.generator.Exposure, java.util.List)
	 */
	@Override
	protected int calculateGroupSize() {
		return groupSize;
	}

}

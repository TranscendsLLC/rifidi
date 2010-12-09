package org.rifidi.edge.tools.diagnostics;

import java.util.List;


import com.espertech.esper.client.EPRuntime;

/**
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class DelayExposureRunner extends ExposureRunner<DelayExposure> {

	/**
	 * @param epRuntime
	 * @param exposure
	 * @param tags
	 */
	public DelayExposureRunner(EPRuntime epRuntime, DelayExposure exposure,
			List<AbstractReadData<?>> tags) {
		super(epRuntime, exposure, tags);
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
		return exposure.getDelay();
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
		return exposure.getGroupSize();
	}

}

/**
 * 
 */
package org.rifidi.edge.app.diag.tags.generator.exposures;

import java.util.List;
import java.util.Properties;

import org.rifidi.edge.app.diag.tags.generator.AbstractReadData;
import org.rifidi.edge.app.diag.tags.generator.Exposure;
import org.rifidi.edge.app.diag.tags.generator.ExposureRunner;
import org.rifidi.edge.app.diag.tags.generator.TagReadData;

import com.espertech.esper.client.EPRuntime;

/**
 * A DelayExposure allows you to define the number of tags to send to esper
 * (groupSize) and then the time to wait after sending that many tags ("delay").
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class DelayExposure extends Exposure {

	/***/
	private static final long serialVersionUID = 1L;
	public static final String PROPERTY_DELAY = "delay";
	public static final String PROPERTY_GROUPSIZE = "groupSize";
	
	
	public DelayExposure(Properties defaults) {
		super(defaults);
	}


	public int getDelay() {
		return Integer.parseInt(getProperty(PROPERTY_DELAY));
	}

	public int getGroupSize() {
		return Integer.parseInt(getProperty(PROPERTY_GROUPSIZE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.app.diag.tags.generator.Exposure#createRunner(java.util
	 * .List, com.espertech.esper.client.EPRuntime)
	 */
	@Override
	public ExposureRunner<DelayExposure> createRunner(List<AbstractReadData<?>> tags,
			EPRuntime epRuntime) {
		return new DelayExposureRunner(epRuntime, this, tags);
	}
}
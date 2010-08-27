package org.rifidi.edge.app.diag.tags.generator.exposures;

import java.util.List;
import java.util.Properties;

import org.rifidi.edge.app.diag.tags.generator.AbstractReadData;
import org.rifidi.edge.app.diag.tags.generator.Exposure;
import org.rifidi.edge.app.diag.tags.generator.ExposureRunner;

import com.espertech.esper.client.EPRuntime;

/**
 * A RateExposure allows you to specify how many tags per second you want to
 * expose to esper.
 * 
 * @author kyle
 * 
 */
public class RateExposure extends Exposure {

	/***/
	private static final long serialVersionUID = 1L;
	/** A property to define number of tags per second */
	public static final String PROPERTY_TAGRATE = "tagRate";

	public RateExposure(Properties defaults) {
		super(defaults);
	}

	public int getTagRate() {
		return Integer.parseInt(getProperty(PROPERTY_TAGRATE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.app.diag.tags.generator.Exposure#createRunner(java.util
	 * .List, com.espertech.esper.client.EPRuntime)
	 */
	@Override
	public ExposureRunner<RateExposure> createRunner(List<AbstractReadData<?>> tags,
			EPRuntime epRuntime) {
		return new RateExposureRunner(epRuntime, this, tags);
	}

}

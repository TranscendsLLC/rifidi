/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.tools.diagnostics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.api.AbstractRifidiApp;

/**
 * This can generate tag events and place the events in esper. It uses the
 * concept of "Tag Sets" and "Exposures". A tag set is simply the set of tags
 * that should be used. The "Exposure" is how the tags should be sent to esper
 * (time delays, number of tags at once, how long to send tags for, etc).
 * 
 * TagSets and Exposures should be placed in the data directory of the
 * Diagnostics group folder. These files should follow the convention:
 * tags-[ID].properties for tag groups and exposure-[ID].properties for
 * exposures.
 * 
 * See TagSet and Exposure classes for what information should be in the
 * property files.
 * 
 * The property files found in the data directory are loaded when this
 * application starts up (you can stop then start the application again to
 * readload them without having to restart the entire edge server).
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagGeneratorApp extends AbstractRifidiApp implements TagGenerator{

	/** The List of tagReadData */
	private HashMap<String, List<AbstractReadData<?>>> tagReadData = new HashMap<String, List<AbstractReadData<?>>>();
	private HashMap<String, Exposure> exposures = new HashMap<String, Exposure>();
	private HashMap<Integer, ExposureRunner<?>> runners = new HashMap<Integer, ExposureRunner<?>>();
	private int runnerCount = 0;
	private Log logger = LogFactory.getLog(TagGeneratorApp.class);

	/**
	 * 
	 * @param group
	 * @param name
	 */
	public TagGeneratorApp(String group, String name) {
		super(group, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#_start()
	 */
	@Override
	protected void _start() {
		// init tags
		Map<String, byte[]> tagFiles = super.getDataFiles("tags");
		for (String id : tagFiles.keySet()) {
			addTagSet(id, processTagFile(tagFiles.get(id)));
			logger.info("Loaded tag file with ID " + id);
		}

		// init exposures
		Map<String, byte[]> exposureFiles = super.getDataFiles("exposure");
		for (String id : exposureFiles.keySet()) {
			addExposure(id, processExposureFile(exposureFiles.get(id)));
			logger.info("Loaded Exposure properties with ID " + id);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#_stop()
	 */
	@Override
	protected void _stop() {
		super._stop();
		for (ExposureRunner<?> runner : runners.values()) {
			runner.cancel();
		}
		tagReadData.clear();
		exposures.clear();
		runners.clear();
		runnerCount = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#lazyStart()
	 */
	@Override
	public boolean lazyStart() {
		String lazyStart = getProperty(LAZY_START, "true");
		return Boolean.parseBoolean(lazyStart);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.diagnostics.tags.generator.TagGenerator#startRunner(java.util.List, org.rifidi.edge.diagnostics.tags.generator.Exposure)
	 */
	@Override
	public Integer startRunner(List<AbstractReadData<?>> tags, Exposure exposure) {
		if (tags == null) {
			return null;
		}
		if (exposure == null) {
			return null;
		}
		ExposureRunner<?> runner = createExposureRunner(exposure, tags);
		if (runner == null) {
			return null;
		}
		this.runners.put(runnerCount++, runner);
		Thread t = new Thread(runner, "Tag Generation Runner: " + runnerCount);
		t.start();
		return runnerCount;
	}

	@Override
	public synchronized Integer startRunner(String tagReadDataID,
			String exposureID) {
		List<AbstractReadData<?>> tags = tagReadData.get(tagReadDataID);
		Exposure exposure = exposures.get(exposureID);
		return startRunner(tags, exposure);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.diagnostics.tags.generator.TagGenerator#stopRunner(java.lang.Integer)
	 */
	@Override
	public void stopRunner(Integer runnerID) {
		// TODO Auto-generated method stub
		
	}
	
	

	/* (non-Javadoc)
	 * @see org.rifidi.edge.diagnostics.tags.generator.TagGenerator#addExposure(java.lang.String, org.rifidi.edge.diagnostics.tags.generator.Exposure)
	 */
	@Override
	public void addExposure(String exposureID, Exposure exposure) {
		exposures.put(exposureID, exposure);
		
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.diagnostics.tags.generator.TagGenerator#addTagSet(java.lang.String, java.util.List)
	 */
	@Override
	public void addTagSet(String tagSetID, List<AbstractReadData<?>> tags) {
		tagReadData.put(tagSetID, tags);
		
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#getCommandProider()
	 */
	@Override
	protected CommandProvider getCommandProvider() {
		TagGeneratorCommandProvider command = new TagGeneratorCommandProvider();
		command.setGeneratorApp(this);
		return command;
	}

	/**
	 * Helper method to create a new runner.
	 * 
	 * @param exposure
	 * @param tags
	 * @return
	 */
	private ExposureRunner<?> createExposureRunner(Exposure exposure,
			List<AbstractReadData<?>> tags) {
		return exposure.createRunner(tags, this.getEPRuntime());
	}

	/**
	 * A helper method to turn a tag file into a list of TagReadData objects
	 * 
	 * @param file
	 * @return
	 */
	private List<AbstractReadData<?>> processTagFile(byte[] file) {
		BufferedReader reader = new BufferedReader(new StringReader(new String(
				file)));
		List<AbstractReadData<?>> retVal = new ArrayList<AbstractReadData<?>>();
		try {
			String line = reader.readLine();
			while (line != null) {
				if (!line.isEmpty() && !line.startsWith("#")) {
					String[] values = line.split(",");
					retVal
							.add(new TagReadData(values[0], values[1],
									Integer.parseInt(values[2])));
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}

	/**
	 * A helper method to turn an Exposure file into an Exposure object. If
	 * there need to be more kinds of runners in the future, this should be
	 * turned into a full-blown factory.
	 * 
	 * @param file
	 * @return
	 */
	private Exposure processExposureFile(byte[] file) {
		BufferedReader reader = new BufferedReader(new StringReader(new String(
				file)));
		Properties properties = new Properties();
		try {
			properties.load(reader);
			String type = properties.getProperty(Exposure.PROPERTY_TYPE);
			if (type.equals(Exposure.EXPOSURE_TYPE_DELAY)) {
				return new DelayExposure(properties);
			} else if (type.equals(Exposure.EXPOSURE_TYPE_RATE)) {
				return new RateExposure(properties);
			} else {
				throw new IllegalStateException("Exposure type not valid: "
						+ type);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		}
	}

}

package org.rifidi.edge.core.mock;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.rifidi.edge.app.diag.tags.generator.AbstractReadData;
import org.rifidi.edge.app.diag.tags.generator.BarcodeReadData;
import org.rifidi.edge.app.diag.tags.generator.Exposure;
import org.rifidi.edge.app.diag.tags.generator.TagReadData;
import org.rifidi.edge.app.diag.tags.generator.exposures.DelayExposure;
import org.rifidi.edge.app.diag.tags.generator.exposures.RateExposure;

public class SystemSetup {
	

	protected Exposure createExposure(String timer, String tagrate) {
		Properties exposureProperties = new Properties();
		exposureProperties.setProperty(Exposure.PROPERTY_TYPE,
				Exposure.EXPOSURE_TYPE_RATE);
		exposureProperties.setProperty(Exposure.PROPERTY_RANDOM, "false");
		exposureProperties.setProperty(Exposure.PROPERTY_STOPTIMER, timer);
		exposureProperties.setProperty(RateExposure.PROPERTY_TAGRATE, tagrate);
		return new RateExposure(exposureProperties);
	}

	protected Exposure createDelayExposure(String timer, String delay,
			String groups) {
		Properties exposureProperties = new Properties();
		exposureProperties.setProperty(Exposure.PROPERTY_TYPE,
				Exposure.EXPOSURE_TYPE_DELAY);
		exposureProperties.setProperty(Exposure.PROPERTY_RANDOM, "false");
		exposureProperties.setProperty(Exposure.PROPERTY_STOPTIMER, timer);
		exposureProperties.setProperty(DelayExposure.PROPERTY_DELAY, delay);
		exposureProperties
				.setProperty(DelayExposure.PROPERTY_GROUPSIZE, groups);
		return new DelayExposure(exposureProperties);
	}

	protected List<AbstractReadData<?>> createTagReadData(
			String parentIDEnding, int numChildren, int childIDStart) {
		List<AbstractReadData<?>> tags = new LinkedList<AbstractReadData<?>>();
		tags.add(new TagReadData(createID("20", parentIDEnding, 24), "LLRP_1",
				2));
		for (String id : createIDs("60", childIDStart, numChildren, 24)) {
			tags.add(new TagReadData(id, "LLRP_1", 2));
		}
		return tags;
	}

	protected List<AbstractReadData<?>> createTagReadData(String prefix,
			int numToCreate, int idStart, String readerID, int antennaID) {
		List<AbstractReadData<?>> tags = new LinkedList<AbstractReadData<?>>();
		for (String tagID : createIDs(prefix, idStart, numToCreate, 24)) {
			tags.add(new TagReadData(tagID, readerID, antennaID));
		}
		return tags;
	}

	protected List<AbstractReadData<?>> createBarcodeReadData(int numToCreate,
			int idStart, String readerID, int antennaID) {
		List<AbstractReadData<?>> tags = new LinkedList<AbstractReadData<?>>();
		for (String tagID : createIDs("0", idStart, numToCreate, 9)) {
			tags.add(new BarcodeReadData(tagID, readerID, antennaID));
		}
		return tags;
	}

	protected List<String> createIDs(String prefix, int start, int num, int size) {
		List<String> retVal = new ArrayList<String>();
		for (int i = 0; i < num; i++) {
			retVal.add(createID(prefix, Integer.toHexString(start++), size));
		}
		return retVal;
	}

	protected String createID(String prefix, String suffix, int size) {
		StringBuilder sb = new StringBuilder(prefix);
		while (sb.length() < (size - suffix.length())) {
			sb.append("0");
		}
		sb.append(suffix);
		return sb.toString();
	}
	
	public static void setupRifidiProperitesSetup() throws Throwable {
		String userDir = System.getProperty("user.dir");
		System.setProperty("org.rifidi.home", userDir);
		String rifidiHome = System.getProperty("org.rifidi.home");
		System.setProperty("org.rifidi.edge.applications", "");
		System.setProperty("activemq.base", rifidiHome);
	}
	
	private SystemSetup() {
	}
}

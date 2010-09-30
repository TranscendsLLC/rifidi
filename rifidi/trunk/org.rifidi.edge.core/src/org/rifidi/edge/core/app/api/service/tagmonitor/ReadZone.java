/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * This class represents a Read Zone to be monitored.
 * 
 * It is safe to clone this object.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReadZone implements Cloneable {

	/** The ID of the reader */
	private String readerID;
	/** The antennas to monitor in the read zone */
	private Set<Integer> antennas = new HashSet<Integer>();
	/** The list of regex patterns to use for this read zone */
	private List<String> tagPatterns = new ArrayList<String>();
	/** If true, tags that match the regex are included */
	private boolean include = false;

	public static final String PROP_READERID = "readerID";
	public static final String PROP_TAGPATTERN = "tagPattern";
	public static final String PROP_MATCHPATTERN = "matchPattern";
	public static final String PROP_ANTENNAS = "antennas";

	/**
	 * Monitor all antennas on the reader with the given ID
	 * 
	 * @param readerID
	 *            The reader to monitor
	 */
	public ReadZone(String readerID) {
		this(readerID, new ArrayList<String>(), false, new Integer[] {});
	}

	/**
	 * Monitor the given antennas on the reader with the given ID
	 * 
	 * @param readerID
	 *            The reader to monitor
	 * @param antennas
	 *            The antennas to monitor on that reader
	 */
	public ReadZone(String readerID, Integer... antennas) {
		this(readerID, new ArrayList<String>(), false, antennas);
	}

	/**
	 * Monitor the given antennas on the reader with the given ID.
	 * 
	 * @param readerID
	 *            The reader to monitor
	 * @param tagPatterns
	 *            regexp patterns to use to filter on the tags' ID
	 * @param include
	 *            if true, include tags that match any of the patterns. If
	 *            false, exclude any tag that matches any pattern
	 * @param antennas
	 *            the annteas to monitor on that reader
	 */
	public ReadZone(String readerID, List<String> tagPatterns, boolean include,
			Integer... antennas) {
		this.readerID = readerID;
		for (Integer ant : antennas) {
			this.antennas.add(ant);
		}
		this.include = include;
		this.tagPatterns.addAll(tagPatterns);
	}

	/**
	 * RifidiApps can store ReadZone property files in a 'readzones' directory.
	 * Valid properties are:
	 * 
	 * readerID - a string that is the ID of the reader (such as LLRP_1)
	 * 
	 * tagPattern (optional)- a regular expression that tag's ID can be matched
	 * against
	 * 
	 * matchPattern (optional) - if 'true', tags that match the tagPattern are
	 * included. If 'false', tags that match the pattern are excluded. The
	 * default value is false
	 * 
	 * antennas (optional) - A comma separated list of integers. If this
	 * property is not supplied, match tags from any antenna.
	 * 
	 * @param properties
	 * @return
	 */
	public static ReadZone createReadZone(Properties properties) {
		String readerID = properties.getProperty(PROP_READERID);
		String tagPattern = properties.getProperty(PROP_TAGPATTERN);
		String match = properties.getProperty(PROP_MATCHPATTERN);
		String antennaProperty = properties.getProperty(PROP_ANTENNAS);
		List<String> tagPatterns = new ArrayList<String>();
		Integer[] antennas;
		boolean include = false;
		if (tagPattern != null && !tagPattern.isEmpty()) {
			tagPatterns.add(tagPattern);
			if (match != null && !match.isEmpty()) {
				include = Boolean.parseBoolean(match);
			}
		}
		if (antennaProperty!=null && !antennaProperty.trim().equals("")) {
			String[] antennaArray = antennaProperty.split(",");
			antennas = new Integer[antennaArray.length];
			for (int i = 0; i < antennaArray.length; i++) {
				antennas[i] = Integer.parseInt(antennaArray[i].trim());
			}
		} else {
			antennas = new Integer[] {};
		}
		return new ReadZone(readerID, tagPatterns, include, antennas);
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @return the antennas
	 */
	public Set<Integer> getAntennas() {
		return antennas;
	}

	/**
	 * @return the tagPatterns
	 */
	public List<String> getTagPatterns() {
		return tagPatterns;
	}

	/**
	 * @return the include
	 */
	public boolean isInclude() {
		return include;
	}

	/**
	 * @param readerID
	 *            the readerID to set
	 */
	public void setReaderID(String readerID) {
		this.readerID = readerID;
	}

	/**
	 * @param include
	 *            the include to set
	 */
	public void setInclude(boolean include) {
		this.include = include;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ReadZone clone() {
		try {
			ReadZone zone = (ReadZone) super.clone();
			zone.tagPatterns = new ArrayList<String>(tagPatterns);
			zone.antennas = new HashSet<Integer>(antennas);
			return zone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}

/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents a Read Zone to be monitored
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReadZone {

	/** The ID of the reader */
	private final String readerID;

	/** The antennas to monitor in the read zone */
	private final Set<Integer> antennas = new HashSet<Integer>();

	private final List<String> tagPatterns = new ArrayList<String>();

	private final boolean include;

	/**
	 * Monitor all antennas on the reader with the given ID
	 * 
	 * @param readerID
	 *            The reader to monitor
	 */
	public ReadZone(String readerID) {
		this(readerID, new ArrayList<String>(), false, new Integer[]{});
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
	 * @param tagPattens
	 *            regexp patterns to use to filter on the tags' ID
	 * @param include
	 *            if true, include tags that match any of the patterns. If
	 *            false, exclude any tag that matches any pattern
	 * @param antennas
	 *            the annteas to monitor on that reader
	 */
	public ReadZone(String readerID, List<String> tagPattens, boolean include,
			Integer... antennas) {
		this.readerID = readerID;
		for (Integer ant : antennas) {
			this.antennas.add(ant);
		}
		this.include = include;
		this.tagPatterns.addAll(tagPattens);
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

}

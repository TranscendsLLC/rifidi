/**
 * 
 */
package org.rifidi.edge.core.app.api.service.monitoring;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a Read Zone to be monitored
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReadZone {

	/** The ID of the reader */
	private String readerID;

	/** The antennas to monitor in the read zone */
	private Set<Integer> antennas = new HashSet<Integer>();

	/**
	 * Monitor all antennas on the reader with the given ID
	 * 
	 * @param readerID
	 *            The reader to monitor
	 */
	public ReadZone(String readerID) {
		this.readerID = readerID;
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
		this.readerID = readerID;
		for (Integer ant : antennas) {
			this.antennas.add(ant);
		}
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

}

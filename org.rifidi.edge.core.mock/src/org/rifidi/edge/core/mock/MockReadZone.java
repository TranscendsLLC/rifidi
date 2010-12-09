/**
 * 
 */
package org.rifidi.edge.core.mock;

import java.util.List;

import org.rifidi.edge.api.service.tagmonitor.ReadZone;

/**
 * @author percent
 *
 */
public class MockReadZone extends ReadZone {

	/**
	 * @param readerID
	 */
	public MockReadZone(String readerID) {
		super(readerID);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param readerID
	 * @param antennas
	 */
	public MockReadZone(String readerID, Integer... antennas) {
		super(readerID, antennas);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param readerID
	 * @param tagPatterns
	 * @param include
	 * @param antennas
	 */
	public MockReadZone(String readerID, List<String> tagPatterns,
			boolean include, Integer... antennas) {
		super(readerID, tagPatterns, include, antennas);
		// TODO Auto-generated constructor stub
	}

}

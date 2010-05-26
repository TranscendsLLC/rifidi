/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor.impl;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.app.api.service.RifidiAppEsperFactory;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;

/**
 * A base class for RifidiAppEsperFactory to extend which deal with the ReadZone
 * object.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractReadZoneEsperFactory implements
		RifidiAppEsperFactory {

	/**
	 * A helper method to create the "insert into" statement. Only tags seen in
	 * the listed readzones are inserted into the given window. If the List is
	 * empty, tags from all readers and antennas are inserted. The list cannot
	 * be null.
	 * 
	 * @return
	 */
	protected String buildInsertStatement(String windowName,
			List<ReadZone> readZones) {
		String s = "insert into " + windowName
				+ " select * from ReadCycle[select * from tags "
				+ whereClause(readZones) + "]";
		return s;
	}

	/**
	 * A private method to create the where clause in the select statement
	 * 
	 * @return
	 */
	private String whereClause(List<ReadZone> readzones) {
		StringBuilder builder = new StringBuilder();
		if (!readzones.isEmpty()) {
			builder.append(" where (" + buildReader(readzones.get(0)));
			builder.append("");
			for (int i = 1; i < readzones.size(); i++) {
				builder.append(" OR " + buildReader(readzones.get(i)));
			}
			builder.append(")");
		}
		return builder.toString();
	}

	/**
	 * This method builds a single reader filter. For example:
	 * 
	 * (TagReadEvent.readerID='Alien_1'
	 * 
	 * or
	 * 
	 * (tagReadEvent.readerID='Alien_1' AND (antennaID=2))
	 * 
	 * 
	 * @param zone
	 * @return
	 */
	private String buildReader(ReadZone zone) {
		StringBuilder sb = new StringBuilder("(TagReadEvent.readerID=\'"
				+ zone.getReaderID() + "\'");
		sb.append(buildAntenns(new ArrayList<Integer>(zone.getAntennas())));
		sb.append(")");
		return sb.toString();
	}

	/**
	 * This method builds a list of antennaID filters. For example:
	 * 
	 * (antennaID=1)
	 * 
	 * or
	 * 
	 * (antennaID=1 OR antennaID=2)
	 * 
	 * @param antennas
	 * @return
	 */
	private String buildAntenns(List<Integer> antennas) {
		StringBuilder sb = new StringBuilder();
		if (antennas.size() > 0) {
			sb.append(" AND (" + buildAntenna(antennas.get(0)));
			for (int i = 1; i < antennas.size(); i++) {
				sb.append(" OR " + buildAntenna(antennas.get(i)));
			}
			sb.append(")");

		}
		return sb.toString();
	}

	/**
	 * This method returns a single antennaID filter.
	 * 
	 * @param antenna
	 * @return
	 */
	private String buildAntenna(Integer antenna) {
		return "antennaID=" + antenna;
	}

}

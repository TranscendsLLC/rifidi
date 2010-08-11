/**
 * 
 */
package org.rifidi.edge.core.app.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;

/**
 * This class has some static util methods to help forming esper strings.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EsperUtil {
	private final static HashMap<TimeUnit, String> timeUnitToEsperString;
	static {
		timeUnitToEsperString = new HashMap<TimeUnit, String>();
		timeUnitToEsperString.put(TimeUnit.DAYS, "days");
		timeUnitToEsperString.put(TimeUnit.HOURS, "hours");
		timeUnitToEsperString.put(TimeUnit.MINUTES, "min");
		timeUnitToEsperString.put(TimeUnit.SECONDS, "sec");
		timeUnitToEsperString.put(TimeUnit.MILLISECONDS, "msec");
	}
	private final static HashMap<String, TimeUnit> esperTimeUnitToJavaTimeUnit;
	static {
		esperTimeUnitToJavaTimeUnit = new HashMap<String, TimeUnit>();
		esperTimeUnitToJavaTimeUnit.put("day", TimeUnit.DAYS);
		esperTimeUnitToJavaTimeUnit.put("days", TimeUnit.DAYS);
		esperTimeUnitToJavaTimeUnit.put("hour", TimeUnit.HOURS);
		esperTimeUnitToJavaTimeUnit.put("minutes", TimeUnit.MINUTES);
		esperTimeUnitToJavaTimeUnit.put("minute", TimeUnit.MINUTES);
		esperTimeUnitToJavaTimeUnit.put("min", TimeUnit.MINUTES);
		esperTimeUnitToJavaTimeUnit.put("seconds", TimeUnit.SECONDS);
		esperTimeUnitToJavaTimeUnit.put("second", TimeUnit.SECONDS);
		esperTimeUnitToJavaTimeUnit.put("sec", TimeUnit.SECONDS);
		esperTimeUnitToJavaTimeUnit.put("milliseconds", TimeUnit.MILLISECONDS);
		esperTimeUnitToJavaTimeUnit.put("millisecond", TimeUnit.MILLISECONDS);
		esperTimeUnitToJavaTimeUnit.put("msec", TimeUnit.MILLISECONDS);
	}

	/**
	 * This method converts between a time and timeunit to an esper time string.
	 * For example 1.5 TimeUnit.Seconds would be converted to the string '1.5
	 * sec'.
	 * 
	 * @param time
	 *            The time must be greater than 0
	 * @param timeUnit
	 *            Valid TimeUnits are DAYS, HOURS, MINUTES, SECONDS, and
	 *            MILLISECONDS.
	 * @return
	 */
	public static String timeUnitToEsperTime(float time, TimeUnit timeUnit) {
		String esperUnit = timeUnitToEsperString.get(timeUnit);
		if (esperUnit == null)
			throw new IllegalArgumentException("Invalid time unit: " + timeUnit);
		if (time <= 0f)
			throw new IllegalArgumentException("Time cannot be < 0");
		return time + " " + esperUnit;
	}

	/**
	 * This method takes in an esper expression for time (such as '1.5 min') and
	 * returns the number as a float.
	 * 
	 * @param esperTime
	 * @return
	 */
	public static Float esperTimetoTime(String esperTime) {
		try {
			return Float.parseFloat(esperTime.trim().split(" ")[0]);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Invalid Esper time expression: " + esperTime);
		}
	}

	/**
	 * This method takes in an esper expression for time (such as '1.5 min') and
	 * returns the TimeUnit object for the unit of time (such as
	 * TimeUnit.Minute)
	 * 
	 * @param esperTime
	 * @return
	 */
	public static TimeUnit esperTimetoTimeUnit(String esperTime) {
		try {
			String esperTimeUnit = esperTime.trim().split(" ")[1];
			TimeUnit t = esperTimeUnitToJavaTimeUnit.get(esperTimeUnit);
			if (t != null)
				return t;
		} catch (Exception e) {
		}
		throw new IllegalArgumentException("Invalid Esper time expression: "
				+ esperTime);
	}

	/**
	 * A helper method to create the "insert into" statement. Only tags seen in
	 * the listed readzones are inserted into the given window. If the List is
	 * empty, tags from all readers and antennas are inserted. The list cannot
	 * be null.
	 * 
	 * @return
	 */
	public static String buildInsertStatement(String windowName,
			List<ReadZone> readZones) {
		String s = "insert into " + windowName
				+ " select * from ReadCycle[select * from tags "
				+ whereClause(readZones) + "]";
		return s;
	}

	/**
	 * A helper method to create the "insert into" statement. Only tags seen in
	 * the given readzone are inserted into the given window. The readzone
	 * cannot be null.
	 * 
	 * @param windowname
	 * @param readZone
	 * @return
	 */
	public static String buildInsertStatement(String windowname,
			ReadZone readZone) {
		List<ReadZone> readZoneList = new ArrayList<ReadZone>();
		readZoneList.add(readZone);
		return buildInsertStatement(windowname, readZoneList);
	}

	/**
	 * A private method to create the where clause in the select statement
	 * 
	 * @return
	 */
	private static String whereClause(List<ReadZone> readzones) {
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
	private static String buildReader(ReadZone zone) {
		StringBuilder sb = new StringBuilder("(TagReadEvent.readerID=\'"
				+ zone.getReaderID() + "\'");
		sb.append(buildAntenns(new ArrayList<Integer>(zone.getAntennas())));
		sb.append(buildTagFilter(zone.getTagPatterns(), zone.isInclude()));
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
	private static String buildAntenns(List<Integer> antennas) {
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
	private static String buildAntenna(Integer antenna) {
		return "antennaID=" + antenna;
	}

	private static String buildTagFilter(List<String> filters, boolean include) {
		StringBuilder sb = new StringBuilder();
		if (filters.size() > 0) {
			sb.append(" AND (" + buildTagFilter(filters.get(0), include));
			for (int i = 1; i < filters.size(); i++) {
				sb.append(" OR " + buildTagFilter(filters.get(i), include));
			}
			sb.append(")");

		}
		return sb.toString();
	}

	private static String buildTagFilter(String filter, boolean include) {
		if (include) {
			return " tag.formattedID regexp '" + filter + "'";
		} else {
			return " tag.formattedID not regexp '" + filter + "'";
		}
	}
}

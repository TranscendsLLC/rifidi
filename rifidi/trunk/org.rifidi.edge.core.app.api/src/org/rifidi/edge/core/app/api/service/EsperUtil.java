/**
 * 
 */
package org.rifidi.edge.core.app.api.service;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
}

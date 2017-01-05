/*
 * 12/12/2015
 * The original work can be found in
 * https://github.com/Fosstrak/fosstrak-fc/blob/master/fc-server/src/main/java/org/fosstrak/ale/server
 * 
 */

package org.rifidi.edge.server.epcglobal.ale;

import org.apache.commons.lang3.StringUtils;

public enum ECReportSetEnum {
	
	/** all the current tags. */
	CURRENT,
	
	/** all the new tags. */
	ADDITIONS,
	
	/** all the tags that left the previous cycle. */
	DELETIONS;
	
	/** logger. */
	//private static final Logger LOG = Logger.getLogger(ECReportSetEnum.class);
	
	/**
	 * compare a given input string to the report set enumerations values. 
	 * the method is save for illegal input arguments (eg. null or not existing report set type).
	 * further the method is case insensitive
	 * @param setEnum the enumeration value to compare to.
	 * @param name the value to check from the enumeration.
	 * @return true if same enumeration value, false otherwise.
	 */
	public static boolean isSameECReportSet(ECReportSetEnum setEnum, String name) {
		try {
			return setEnum.equals(ECReportSetEnum.valueOf(StringUtils.upperCase(name)));
		} catch (Exception ex) {
			//LOG.error("you provided an illegal report set enum value: " + name, ex);
		}
		return false;
	}
}

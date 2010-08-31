/*
 *  URIUtility.java
 *
 *  Created:	Aug 31, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.init.utility;

/**
 * This class acts as a utility to format strings in such a way that it can be
 * parsed by a URI.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class URIUtility {
	/**
	 * Format the given string into a URI-readable form. Currently, backslashes
	 * are replaced with forward slashes, and spaces are replaced by %20s.  
	 * 
	 * @return
	 */
	public static String createURI(String s) {
		String retVal = new String(s);
		retVal = retVal.replace("\\", "/");
		retVal = retVal.replace(" ", "%20");
		return retVal;
	}
}

/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.init;

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

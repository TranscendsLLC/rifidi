/*
 *  ALEFormat.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.datatypes.builtin.formats;

/**
 * This interface should be implemented by all format enums so that we can deal
 * with formats abstractly in the datatypes
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ALEFormat {

	/**
	 * 
	 * @return the name of the format. Equivalent to the name() call on an enum
	 */
	public String getFormatName();
}

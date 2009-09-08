/*
 * 
 * PatternMatcher.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.filters;

/**
 * A matcher implementing this interface checks if a given string is a match.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface PatternMatcher {
	/**
	 * Match the string.
	 * 
	 * @param matchee
	 * @return
	 */
	public boolean match(String matchee);
}

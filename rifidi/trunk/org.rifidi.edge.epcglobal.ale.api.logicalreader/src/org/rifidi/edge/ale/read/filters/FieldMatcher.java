/*
 * 
 * FieldMatcher.java
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
package org.rifidi.edge.ale.read.filters;

/**
 * Matcher implementing this interface are used to identify fields in a pattern.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface FieldMatcher {
	/**
	 * Check if the given String is a field.
	 * 
	 * @param field
	 * @return
	 */
	public boolean match(String field);
}

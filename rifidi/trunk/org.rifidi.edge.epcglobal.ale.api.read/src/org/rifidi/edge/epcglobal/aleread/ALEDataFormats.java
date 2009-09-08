/*
 * 
 * ALEDataFormats.java
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
package org.rifidi.edge.epcglobal.aleread;

/**
 * Formats of data stored on epc data sources.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public enum ALEDataFormats {
	EPC_TAG, EPC_PURE, EPC_HEX, EPC_DECIMAL, HEX, DECIMAL, STRING, DEFAULT;
}

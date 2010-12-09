/*
 * 
 * ALEFields.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.ale.read;

/**
 * Fields on a tag according to the ALE spec.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public enum ALEFields {
	EPC, KILLPWD, ACCESSPWD, EPCBANK, TIDBANK, USERBANK, AFI, NSI, ABSOLUTEADDRESS, VARIABLEFIELD;
}

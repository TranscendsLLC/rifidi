/*
 * AwidDefaultValues.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.adapter.awid.awid2010;

/**
 * Default values for the Awid
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AwidDefaultValues {

	/** The Default IP address */
	public final static String HOST = "192.168.1.91";
	/** The default port */
	public final static String PORT = "4000";
	/** Default max number of reconnection attempts */
	public final static String MAX_NUM_RECON_ATTEMPS = "-1";
	/** The default interval between reconnection attempts */
	public final static String RECON_INTERVAL = "1000";

}

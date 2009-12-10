/*
 *  AcuraProXTagHandler.java
 *
 *  Created:	Dec 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.acura;

import org.springframework.jms.core.JmsTemplate;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class AcuraProXTagHandler {
	/**
	 * 
	 */
	public AcuraProXTagHandler(JmsTemplate template) {
		
	}
	
	public void processTag(byte[] tagdata) {
		System.out.println("Length of data: " + tagdata.length);
		
		
	}
}

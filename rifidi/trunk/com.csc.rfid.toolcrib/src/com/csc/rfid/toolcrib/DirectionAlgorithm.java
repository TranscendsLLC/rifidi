/*
 *  DirectionAlgorithm.java
 *
 *  Created:	Feb 5, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package com.csc.rfid.toolcrib;

import java.util.List;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class DirectionAlgorithm {
	
	/**
	 * 
	 */
	public DirectionAlgorithm() {
	}
	
	/**
	 * We will look at the relative speeds of the object, and we will add up the
	 * velocity values to see which way the box is going.
	 * 
	 * @param speed
	 * @return
	 */
	public Float getSpeed(List<Float> speed) {
		float retVal = 0.0f;

		for (Float f : speed) {
			retVal += f;
		}

		return retVal;
	}
}

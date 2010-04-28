/*
 *  AmbientBarcodeMessageParsingStrategyF.java
 *
 *  Created:	Apr 28, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.ambient.barcode;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy;

/**
 * All messages coming back from the barcode reader are 10 characters long.  So any 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AmbientBarcodeMessageParsingStrategy implements
		MessageParsingStrategy {

	/** The message currently being processed. */
	private List<Byte> messagebuilder = new ArrayList<Byte>();
	/** Once we hit this size, we have a completed message */
	private static final int MSGSIZE = 10;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy#isMessage
	 * (byte)
	 */
	@Override
	public byte[] isMessage(byte message) {
		if (messagebuilder.size() == MSGSIZE) {
			byte retval[] = new byte[MSGSIZE];
			for (int i = 0; i < MSGSIZE; i++) {
				retval[i]=messagebuilder.get(i);
			}
			messagebuilder.clear();
			return retval;
		}
		return null;
	}

}

/*
 *  GenericSensorSessionTagHandler.java
 *
 *  Created:	Aug 4, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.generic;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
class GenericSensorSessionTagHandler {

	/**
	 * All values we will be searching for upon message parsing: Tag ID,
	 * antenna, and timestamp. Any other key will be put into the extra
	 * information hashmap.
	 */
	public static final String ID_KEY = "ID";
	public static final String ANTENNA_KEY = "Antenna";
	public static final String TIMESTAMP_KEY = "Timestamp";

	private static final String PAIR_DELIM = "\\|";
	private static final String KEY_VAL_DELIM = ":";

	private String readerID = null;

	public GenericSensorSessionTagHandler(String readerID) {

	}

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(GenericSensorSessionTagHandler.class);

	/**
	 * 
	 * @return
	 */
	public TagReadEvent parseTag(String message) {
		//System.out.println("Starting to process the message");
		try {
			Map<String, String> extrainfo = new HashMap<String, String>();
			Integer antenna = null;
			Long timestamp = null;
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			String strmessage = new String(message);
			for (String key_val_pair : strmessage.split(PAIR_DELIM)) {
				String[] key_val = key_val_pair.split(KEY_VAL_DELIM);
				String key = key_val[0];
				String val = key_val[1];
				//System.out.println("Have a pair: " + key + ", " + val);
				if (key.equalsIgnoreCase(ID_KEY)) {
					//System.out.println("Have a tag!  ");
					int numbits = val.length() * 4;
					BigInteger epc = null;
					try {
						epc = new BigInteger(Hex.decodeHex(val.toCharArray()));
					} catch (DecoderException e) {
						throw new RuntimeException("Cannot decode tag: " + val);
					}
					gen2event.setEPCMemory(epc, numbits);
				} else if (key.equalsIgnoreCase(ANTENNA_KEY)) {
					//System.out.println("In the antenna!");
					antenna = Integer.parseInt(val);
				} else if (key.equalsIgnoreCase(TIMESTAMP_KEY)) {
					System.out.println("In the timestamp!");
					timestamp = Long.parseLong(val);
				} else {
					//System.out.println("processing the extra information!");
					extrainfo.put(key, val);
				}
			}
			if (timestamp == null) {
				timestamp = System.currentTimeMillis();
			}
			if (antenna == null) {
				antenna = -1;
			}
			// System.out.println("Getting ready to create the tagevent, ID: "
			// + gen2event.getEpc() + ", antenna:" + antenna
			// + ", timestamp: " + timestamp);
			TagReadEvent retVal = new TagReadEvent(readerID, gen2event,
					antenna, timestamp);
			for (String extrakey : extrainfo.keySet()) {
				retVal.addExtraInformation(extrakey, extrainfo.get(extrakey));
			}
			return retVal;

		} catch (Exception e) {
			logger.error("There was an exception when processing an "
					+ "incoming message for reader " + readerID + "\n "
					+ e.getMessage());
			//e.printStackTrace();
		}
		return null;
	}
}

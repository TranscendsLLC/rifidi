/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.adapter.generic;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.notification.EPCGeneration2Event;
import org.rifidi.edge.notification.TagReadEvent;

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
		this.readerID = readerID;
	}

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(GenericSensorSessionTagHandler.class);

	/**
	 * 
	 * @return
	 */
	public TagReadEvent parseTag(String message) {
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
				if (key.equalsIgnoreCase(ID_KEY)) {
					int numbits = val.length() * 4;
					BigInteger epc;
					try {
						epc = new BigInteger(val, 16);
					} catch (Exception e) {
						throw new RuntimeException("Cannot decode ID: "
								+ val);
					}
					gen2event.setEPCMemory(epc, val, numbits);
				} else if (key.equalsIgnoreCase(ANTENNA_KEY)) {
					antenna = Integer.parseInt(val);
				} else if (key.equalsIgnoreCase(TIMESTAMP_KEY)) {
					timestamp = Long.parseLong(val);
				} else {
					extrainfo.put(key, val);
				}
			}
			if (timestamp == null) {
				timestamp = System.currentTimeMillis();
			}
			if (antenna == null) {
				antenna = -1;
			}
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
			// e.printStackTrace();
		}
		return null;
	}
}

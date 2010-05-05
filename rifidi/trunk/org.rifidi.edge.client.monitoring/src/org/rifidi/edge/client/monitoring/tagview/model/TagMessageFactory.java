/**
 * 
 */
package org.rifidi.edge.client.monitoring.tagview.model;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class parses messages coming in from JMS into TagModel objects. The
 * format of this string is a pipe-deliminated (i.e. '|' ) string. Each value is
 * a name-value pair separated by a colon (i.e. ':'). Order of name-value pairs
 * within the string is not important (e.g. 'tag' could come before or after
 * 'date', for example.)
 * 
 * Example: tag:12345|time:1234556899|readerid:alien_1|antennaid:2|rssi:24.2
 * 
 * At a minimum, the string must contain the following: tag, rssi, time,
 * readerid, antenna.
 * 
 * The time is given as a long value that is obtained via
 * System.currentTimeMillis.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagMessageFactory {

	private static final Log logger = LogFactory
			.getLog(TagMessageFactory.class);

	/**
	 * 
	 * @param message
	 *            The message to parse
	 * @return
	 */
	public static TagModel parse(String message) {

		// split the string into name-value pairs based on the pipe character
		String[] nameValues = message.trim().split("\\|");
		HashMap<String, String> nameValueMap = new HashMap<String, String>();

		// step through each name-value pair and add them to the hashmap
		for (String s : nameValues) {
			String[] pair = s.trim().split(":");
			if (pair.length == 2) {
				nameValueMap.put(pair[0], pair[1]);

				// if length is not 2, something is wrong
			} else {
				logger.error("Error when parsing field. "
						+ "Does not contain exactly 2 entries: " + s);
			}
		}

		// if we dont' have tag and time fields, do not proceed
		if (!nameValueMap.containsKey("tag")
				|| !nameValueMap.containsKey("time")) {
			throw new IllegalArgumentException(
					"Message does not contain all necessary fields " + message);
		}

		String tagID = nameValueMap.get("tag");
		String time = nameValueMap.get("time");

		// create the TagModel object
		TagModel model = new TagModel(tagID, Long.parseLong(time));

		// add rssi if we have it
		if (nameValueMap.containsKey("rssi")) {
			model.setRssi(Float.parseFloat(nameValueMap.get("rssi")));
		}

		// add readerID if we have it
		if (nameValueMap.containsKey("readerid")) {
			model.setReaderID(nameValueMap.get("readerid"));
		}

		// add antennaid if we have it.
		if (nameValueMap.containsKey("antennaid")) {
			model.setAntennaID(Integer.parseInt(nameValueMap.get("antennaid")));
		}

		return model;
	}
}

package org.rifidi.edge.readerplugin.motorola.mc9090.tags;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class to help parse raw tag messages from the MC9090.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class MC9090TagMessage {

	/** The ID of the tag in hex */
	private String hexID;
	/** The type of the tag (e.g. gen2) */
	private String type;
	/** The time that the tag was seen */
	private Date lastSeenTime;
	/** The antenna */
	private int antenna;

	/***
	 * Constructor
	 * 
	 * @param rawMessage
	 *            The raw message sent by the MC9090 reader
	 */
	public MC9090TagMessage(String rawMessage) {
		String[] fields = rawMessage.split("\\|");
		for (String field : fields) {
			if (field.startsWith("TAG:")) {
				hexID = field.substring(field.indexOf(":") + 1).trim();
			} else if (field.startsWith("TYPE:")) {
				type = field.substring(field.indexOf(":") + 1).trim();
			} else if (field.startsWith("ANT")) {
				antenna = 1;
			} else if (field.startsWith("DATE")) {
				String dateString = field.substring(field.indexOf(":") + 1)
						.trim();
				DateFormat formatter = new SimpleDateFormat(
						"yyyy/MM/dd hh:mm:ss");
				try {
					lastSeenTime = (Date) formatter.parse(dateString);
				} catch (ParseException e) {
					lastSeenTime = new Date(System.currentTimeMillis());
				}

			}
		}
	}

	/**
	 * @return the hexID
	 */
	public String getHexID() {
		return hexID;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the lastSeenTime
	 */
	public Date getLastSeenTime() {
		return lastSeenTime;
	}

	/**
	 * @return the antenna
	 */
	public int getAntenna() {
		return antenna;
	}

}

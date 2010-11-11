/*
 * 
 * AlienTag.java
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
package org.rifidi.edge.adapter.alien.messages;

import java.util.Date;

/**
 * This class represents a tag read by an Alien Reader.
 * 
 * Added the extrainformation the Alien can sometimes have, including speed and
 * rssi. This class makes no attempt to parse the extra data recieved, it only
 * puts the data in a HashMap, to be used by whoever wants it. - Matt
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Matthew Dean - matt@pramari.com
 */
public class AlienTag {

	/** The FACTORY_ID of the tag as a hex string */
	private String id_hex;
	/** The date (on the reader) the tag was discovered */
	private Date discoverDate;
	/** The date (on the reader) the tag was last seen */
	private Date lastSeenDate;
	/** Number of times the tag has been seen by the reader */
	private int count;
	/** The antenna the tag was seen on */
	private int antenna;
	/** 1=Gen1, 2=Gen2 */
	private int protocol;
	/** Sets the */
	private Float speed;

	private String direction;

	private Float rssi;

	/**
	 * Creates a new AlienTag from the raw string received from the Alien
	 * reader. The format should be text
	 * 
	 * @param rawTag
	 */
	public AlienTag(String rawTag) {
		String[] fields = rawTag.split(",");
		for (String field : fields) {
			field = field.trim();
			if (field.startsWith("Tag:")) {
				String idHex = field.substring(field.indexOf(':') + 1).trim();
				this.id_hex = idHex.replaceAll(" ", "");
			} else if (field.startsWith("Disc:")) {
				String dateString = field.substring(field.indexOf(':') + 1)
						.trim();
				this.discoverDate = AlienMessage.parseAlienDate(dateString);
			} else if (field.startsWith("Last:")) {
				String dateString = field.substring(field.indexOf(':') + 1)
						.trim();
				this.lastSeenDate = AlienMessage.parseAlienDate(dateString);
			} else if (field.startsWith("Count:")) {
				String count = field.substring(field.indexOf(':') + 1).trim();
				this.count = Integer.parseInt(count);
			} else if (field.startsWith("Ant:")) {
				String antenna = field.substring(field.indexOf(':') + 1).trim();
				this.antenna = Integer.parseInt(antenna);
			} else if (field.startsWith("Proto")) {
				String proto = field.substring(field.indexOf(':') + 1).trim();
				this.protocol = Integer.parseInt(proto);
			} else if (field.startsWith("Speed:")) {
				try {
					String speed = field.substring(field.indexOf(':') + 1)
							.trim();
					this.speed = Float.parseFloat(speed);
				} catch (Exception e) {
					// ignore exceptions here
					this.speed = 0.0f;
				}
			} else if (field.startsWith("Rssi:")) {
				try {
					String rssi = field.substring(field.indexOf(':') + 1)
							.trim();
					this.rssi = Float.parseFloat(rssi);
				} catch (Exception e) {
					// ignore exceptions here
					this.rssi = 0.0f;
				}
			} else if (field.startsWith("Dir:")) {
				String dir = field.substring(field.indexOf(':') + 1).trim();
				this.direction = dir;
			}
		}
	}

	/**
	 * @return the id_hex
	 */
	public String getId_hex() {
		return id_hex;
	}

	/**
	 * @param idHex
	 *            the id_hex to set
	 */
	public void setId_hex(String idHex) {
		id_hex = idHex;
	}

	/**
	 * @return the discoverDate
	 */
	public Date getDiscoverDate() {
		return discoverDate;
	}

	/**
	 * @param discoverDate
	 *            the discoverDate to set
	 */
	public void setDiscoverDate(Date discoverDate) {
		this.discoverDate = discoverDate;
	}

	/**
	 * @return the lastSeenDate
	 */
	public Date getLastSeenDate() {
		return lastSeenDate;
	}

	/**
	 * @param lastSeenDate
	 *            the lastSeenDate to set
	 */
	public void setLastSeenDate(Date lastSeenDate) {
		this.lastSeenDate = lastSeenDate;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the antenna
	 */
	public int getAntenna() {
		return antenna;
	}

	/**
	 * @param antenna
	 *            the antenna to set
	 */
	public void setAntenna(int antenna) {
		this.antenna = antenna;
	}

	/**
	 * @return the protocol
	 */
	public int getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol
	 *            the protocol to set
	 */
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	/**
	 * Gets the speed for this tag.
	 * 
	 * @return
	 */
	public Float getSpeed() {
		return speed;
	}

	/**
	 * Sets the speed for this tag.
	 * 
	 * @param speed
	 */
	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	/**
	 * Gets the RSSI information for this tag.
	 * 
	 * @return
	 */
	public Float getRssi() {
		return rssi;
	}

	/**
	 * 
	 * 
	 * @param rssi
	 */
	public void setRssi(Float rssi) {
		this.rssi = rssi;
	}

	/**
	 * 
	 * @return
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}

}

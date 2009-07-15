/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.messages;

import java.util.Date;

/**
 * This class represents a tag read by an Alien Reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienTag {

	/** The ID of the tag as a hex string */
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

}

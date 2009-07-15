/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.messages;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a parsed notification received from an Alien reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienMessage {

	/** The name of the reader */
	private String readerName;
	/** The type of the reader */
	private String readerType;
	/** The IP address of the reader */
	private String IPAddress;
	/** The port used to communicate with the reader in interactive mode */
	private int commandPort;
	/** The MAC Address of the reader */
	private String MACAddress;
	/** The time (on the reader) the message was sent */
	private Date time;
	/** The reason for sending the notification */
	private String reason;
	/** The start trigger */
	private String startTriggerLines;
	/** The stop trigger */
	private String stopTriggerLines;
	/** The tags seen */
	private Set<AlienTag> tagList;

	/**
	 * Construct a new AlienMessage from the raw message from the Alien reader.
	 * The notifyformat should be text.
	 * 
	 * @param rawMessage
	 */
	public AlienMessage(String rawMessage) {
		this.tagList = new HashSet<AlienTag>();
		String[] lines = rawMessage.split("\n");
		for (String line : lines) {
			line = line.trim();
			if (line.startsWith("Tag:")) {
				tagList.add(new AlienTag(line));
			} else if (line.startsWith("#ReaderName:")) {
				this.readerName = line.substring(line.indexOf(':') + 1).trim();
			} else if (line.startsWith("#ReaderType:")) {
				this.readerType = line.substring(line.indexOf(':') + 1).trim();
			} else if (line.startsWith("#IPAddress")) {
				this.IPAddress = line.substring(line.indexOf(':') + 1).trim();
			} else if (line.startsWith("#CommandPort")) {
				String commandPort = line.substring(line.indexOf(':') + 1)
						.trim();
				this.commandPort = Integer.parseInt(commandPort);
			} else if (line.startsWith("#MACAddress:")) {
				this.MACAddress = line.substring(line.indexOf(':') + 1).trim();
			} else if (line.startsWith("#Reason:")) {
				this.reason = line.substring(line.indexOf(':') + 1).trim();
			} else if (line.startsWith("#Time")) {
				String dateString = line.substring(line.indexOf(':') + 1)
						.trim();
				this.time = AlienMessage.parseAlienDate(dateString);
			} else if (line.startsWith("#StartTriggerLines:")) {
				this.startTriggerLines = line.substring(line.indexOf(':') + 1)
						.trim();
			} else if (line.startsWith("#StopTriggerLines:")) {
				this.stopTriggerLines = line.substring(line.indexOf(':') + 1)
						.trim();
			}
		}
	}

	/**
	 * @return the readerName
	 */
	public String getReaderName() {
		return readerName;
	}

	/**
	 * @return the readerType
	 */
	public String getReaderType() {
		return readerType;
	}

	/**
	 * @return the iPAddress
	 */
	public String getIPAddress() {
		return IPAddress;
	}

	/**
	 * @return the commandPort
	 */
	public int getCommandPort() {
		return commandPort;
	}

	/**
	 * @return the mACAddress
	 */
	public String getMACAddress() {
		return MACAddress;
	}

	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @return the startTriggerLines
	 */
	public String getStartTriggerLines() {
		return startTriggerLines;
	}

	/**
	 * @return the stopTriggerLines
	 */
	public String getStopTriggerLines() {
		return stopTriggerLines;
	}

	/**
	 * @return the tagList
	 */
	public Set<AlienTag> getTagList() {
		return tagList;
	}

	/**
	 * This method takes in an "alien date" with the format
	 * "2009/07/14 10:20:02", parses it and returns a java.util.Date. It returns
	 * null if there was a problem parsing the string
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date parseAlienDate(String dateString) {
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		try {
			return (Date) formatter.parse(dateString);

		} catch (ParseException e) {
			// TODO:LOG warn
			return null;
		}
	}
}

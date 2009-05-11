/*
 *  AlienGetTagListCommand.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.alien.commands;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.messages.EPCGeneration2Event;
import org.rifidi.edge.core.messages.ReadCycle;
import org.rifidi.edge.core.messages.TagReadEvent;
import org.rifidi.edge.readerplugin.alien.AbstractAlien9800Command;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienCommandObject;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienException;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienGetCommandObject;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienSetCommandObject;
import org.rifidi.edge.readerplugin.alien.commandobject.GetTagListCommandObject;
import org.springframework.jms.core.MessageCreator;

/**
 * A command which gets and returns tags from an Alien reader.  
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class AlienGetTagListCommand extends AbstractAlien9800Command {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AlienGetTagListCommand.class);
	/** Tagtypes. */
	private Integer[] tagTypes = new Integer[] { 7, 16, 31 };
	/** Tagtypes to query for: 0 - GEN1 1 - GEN2 2 - ALL */
	private Integer tagType = 2;
	/** Timezone from the readerSession. */
	private TimeZone timeZone;
	/** Calendar for creating timestamps. */
	private Calendar calendar;
	private String antennasequence = "0";
	private int persistTime = -1;
	private final String reader;

	/**
	 * Constructor
	 * 
	 * @param commandID
	 */
	public AlienGetTagListCommand(String commandID, String readerID) {
		super(commandID);
		this.reader = readerID;
	}

	/**
	 * @return the tagType
	 */
	public int getTagType() {
		return tagType;
	}

	/**
	 * Sets the antennasequence.  
	 * 
	 * @param antennasequence
	 *            the antennasequence to set
	 */
	public void setAntennasequence(String antennasequence) {
		this.antennasequence = antennasequence;
	}

	/**
	 * Sets the PersistTime.  
	 * 
	 * @param persistTime
	 *            the persistTime to set
	 */
	public void setPersistTime(int persistTime) {
		this.persistTime = persistTime;
	}

	/**
	 * Sets the TagType.  
	 * 
	 * @param tagType
	 *            the tagType to set
	 */
	public void setTagType(int tagType) {
		this.tagType = tagType;
		if (tagType > 2 || tagType < 0) {
			throw new RuntimeException("Tagtype must be 0, 1, or 2");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			AlienCommandObject timeZoneCommand = new AlienGetCommandObject(
					Alien9800ReaderSession.COMMAND_TIME_ZONE,
					(Alien9800ReaderSession) this.readerSession);
			String tz = timeZoneCommand.execute();
			String timeZoneString = "GMT" + tz;
			timeZone = TimeZone.getTimeZone(timeZoneString);
			calendar = Calendar.getInstance(timeZone);

			// sending TagType
			AlienCommandObject tagTypeCommand = new AlienSetCommandObject(
					Alien9800ReaderSession.COMMAND_TAG_TYPE,
					tagTypes[getTagType()].toString(),
					(Alien9800ReaderSession) readerSession);
			tagTypeCommand.execute();

			// sending TagListFormat
			AlienCommandObject tagListFormat = new AlienSetCommandObject(
					Alien9800ReaderSession.COMMAND_TAG_LIST_FORMAT, "custom",
					(Alien9800ReaderSession) readerSession);
			tagListFormat.execute();
			// sending TagListFormat
			AlienCommandObject tagListCustomFormat = new AlienSetCommandObject(
					Alien9800ReaderSession.COMMAND_TAG_LIST_CUSTOM_FORMAT,
					"%k|%T|%a", (Alien9800ReaderSession) readerSession);
			tagListCustomFormat.execute();
			GetTagListCommandObject getTagListCommandObject = new GetTagListCommandObject(
					(Alien9800ReaderSession) readerSession);
			List<String> tags = getTagListCommandObject.executeGet();

			template.send(destination, new ObjectMessageCreator(
					parseString(tags)));

		} catch (AlienException ex) {
			logger.warn("Exception while executing command: " + ex);
		} catch (IOException ex) {
			logger.warn("IOException while executing command: " + ex);
		} catch (Exception e) {
			logger.warn("Exception while executing command: " + e);
		}
	}

	/**
	 * Parse the given string for results.
	 * 
	 * @param input
	 * @return
	 */
	private Set<TagReadEvent> parseString(List<String> input) {

		Set<TagReadEvent> retVal = new HashSet<TagReadEvent>();

		try {
			for (String s : input) {
				s = s.trim();
				String[] splitString2 = s.split("\\|");
				if (splitString2.length > 1) {
					String tagData = splitString2[0];
					String timeStamp = splitString2[1];
					String antennaID = splitString2[2];
					int epcLength = tagData.length();
					BigInteger data = new BigInteger(tagData, 16);

					EPCGeneration2Event gen2event = new EPCGeneration2Event();
					// make some wild guesses on the length of the epc field
					if (epcLength > 96) {
						gen2event.setEPCMemory(data, 192);
					} else if (data.bitLength() > 64) {
						gen2event.setEPCMemory(data, 96);
					} else {
						gen2event.setEPCMemory(data, 64);
					}
					int antennaID_int = 0;

					try {
						antennaID_int = Integer.parseInt(antennaID);
					} catch (NumberFormatException ex) {

					}

					// TODO: parse timestamp

					TagReadEvent tag = new TagReadEvent(reader, gen2event,
							antennaID_int, System.currentTimeMillis());
					retVal.add(tag);

				} else {
					// logger.error("Something isreaders invalid: " +
					// splitString2[0]);
				}
			}
		} catch (Exception e) {
			logger.warn("There was a problem when parsing Alien Tags.  "
					+ "tag has not been added", e);
		}
		return retVal;
	}

	private class ObjectMessageCreator implements MessageCreator {

		private ActiveMQObjectMessage objectMessage;

		/**
		 * Constructor.
		 * 
		 * @param message
		 */
		public ObjectMessageCreator(Set<TagReadEvent> tags) {
			super();
			objectMessage = new ActiveMQObjectMessage();

			try {
				objectMessage.setObject(new ReadCycle(tags, reader, System
						.currentTimeMillis()));
			} catch (JMSException e) {
				logger.warn("Unable to set tag event: " + e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.jms.core.MessageCreator#createMessage(javax.jms
		 * .Session)
		 */
		@Override
		public Message createMessage(Session arg0) throws JMSException {
			return objectMessage;
		}

	}

}

/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.messages.EPCGeneration2Event;
import org.rifidi.edge.core.readers.Command;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienCommandObject;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienException;
import org.rifidi.edge.readerplugin.alien.commandobject.GetTagListCommandObject;
import org.springframework.jms.core.MessageCreator;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class AlienGetTagListCommand extends Command {
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

	/**
	 * @return the tagType
	 */
	public int getTagType() {
		return tagType;
	}

	/**
	 * @param antennasequence
	 *            the antennasequence to set
	 */
	public void setAntennasequence(String antennasequence) {
		this.antennasequence = antennasequence;
	}

	/**
	 * @param persistTime
	 *            the persistTime to set
	 */
	public void setPersistTime(int persistTime) {
		this.persistTime = persistTime;
	}

	/**
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
			AlienCommandObject timeZoneCommand = new AlienCommandObject(
					Alien9800ReaderSession.COMMAND_TIME_ZONE,
					(Alien9800ReaderSession) this.readerSession);
			String tz = timeZoneCommand.executeGet();
			String timeZoneString = "GMT" + tz;
			timeZone = TimeZone.getTimeZone(timeZoneString);
			calendar = Calendar.getInstance(timeZone);

			// sending TagType
			AlienCommandObject tagTypeCommand = new AlienCommandObject(
					Alien9800ReaderSession.COMMAND_TAG_TYPE,
					(Alien9800ReaderSession) readerSession);
			tagTypeCommand.executeSet(tagTypes[getTagType()].toString());

			// sending TagListFormat
			AlienCommandObject tagListFormat = new AlienCommandObject(
					Alien9800ReaderSession.COMMAND_TAG_LIST_FORMAT,
					(Alien9800ReaderSession) readerSession);
			tagListFormat.executeSet("custom");
			// sending TagListFormat
			AlienCommandObject tagListCustomFormat = new AlienCommandObject(
					Alien9800ReaderSession.COMMAND_TAG_LIST_CUSTOM_FORMAT,
					(Alien9800ReaderSession) readerSession);
			tagListCustomFormat.executeSet("%k|%T|%a");
			GetTagListCommandObject getTagListCommandObject = new GetTagListCommandObject(
					(Alien9800ReaderSession) readerSession);
			List<String> tags = getTagListCommandObject.executeGet();
			for (BigInteger m : parseString(tags)) {
				template.send(destination, new ObjectMessageCreator(m));
			}
		} catch (AlienException ex) {
			logger.warn("Exception while executing command: " + ex);
		} catch (IOException ex) {
			logger.warn("IOException while executing command: " + ex);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Parse the given string for results.
	 * 
	 * @param input
	 * @return
	 */
	private List<BigInteger> parseString(List<String> input) {

		List<BigInteger> retVal = new ArrayList<BigInteger>();

		try {
			for (String s : input) {
				s = s.trim();
				String[] splitString2 = s.split("\\|");
				if (splitString2.length > 1) {
					String tagData = splitString2[0];
					String timeStamp = splitString2[1];
					String antennaID = splitString2[2];

					retVal.add(new BigInteger(tagData, 16));

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

		/** The message that should be part of the object. */
		private BigInteger message;
		private ActiveMQObjectMessage objectMessage;

		/**
		 * Constructor.
		 * 
		 * @param message
		 */
		public ObjectMessageCreator(BigInteger message) {
			super();
			this.message = message;
			objectMessage = new ActiveMQObjectMessage();
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			gen2event.setEPCMemory(message);
			try {
				objectMessage.setObject(gen2event);
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

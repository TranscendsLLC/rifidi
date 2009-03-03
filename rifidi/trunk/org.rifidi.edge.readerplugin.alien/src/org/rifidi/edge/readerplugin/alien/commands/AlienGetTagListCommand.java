/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands;

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
import org.rifidi.edge.core.commands.Command;
import org.rifidi.edge.core.commands.CommandState;
import org.rifidi.edge.core.messages.EPCGeneration2Event;
import org.rifidi.edge.readerplugin.alien.Alien9800Reader;
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
	/** Time between two polls. */
	private Integer pollInterval = 10;
	/** Timezone from the reader. */
	private TimeZone timeZone;
	/** Calendar for creating timestamps. */
	private Calendar calendar;
	/** Set to falls to kill the callable. */
	private boolean running = true;
	private String antennasequence="0";
	private int persistTime=-1;
	
	/**
	 * @return the tagType
	 */
	public int getTagType() {
		return tagType;
	}

	/**
	 * @param antennasequence the antennasequence to set
	 */
	public void setAntennasequence(String antennasequence) {
		this.antennasequence = antennasequence;
	}

	/**
	 * @param persistTime the persistTime to set
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

	/**
	 * @param pollInterval the pollInterval to set
	 */
	public void setPollInterval(Integer pollInterval) {
		this.pollInterval = pollInterval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.Command#stop()
	 */
	@Override
	public void stop() {
		running = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public CommandState call() throws Exception {
		logger.info("Starting GetTagList Command");
		getReader().sendMessage(Alien9800Reader.GET_TIME_ZONE);
		String temp = ((String) getReader().receiveMessage());
		if (temp.contains("=")) {
			String temp1[] = temp.split("=");
			temp = temp1[1];
		}
		String timeZoneString = "GMT" + temp.trim();

		timeZone = TimeZone.getTimeZone(timeZoneString);

		calendar = Calendar.getInstance(timeZone);

		// sending TagType
		String command = Alien9800Reader.TAG_TYPE_COMMAND
				+ tagTypes[getTagType()] + Alien9800Reader.NEWLINE;
		logger.debug("Sending command: " + command);
		getReader().sendMessage(command);
		getReader().receiveMessage();

		// sending TagListFormat
		command = Alien9800Reader.TAG_LIST_FORMAT;
		logger.debug("Sending command: " + command);
		getReader().sendMessage(command);
		getReader().receiveMessage();

		// sending custom format
		command = Alien9800Reader.TAG_LIST_CUSTOM_FORMAT;
		logger.debug("Sending command: " + command);
		getReader().sendMessage(command);
		getReader().receiveMessage();

		while (running) {
			// sending get tag list
			command = Alien9800Reader.TAG_LIST;
			logger.debug("Sending command: " + command);
			getReader().sendMessage(command);
			String tag_msg = (String) getReader().receiveMessage();
			for (BigInteger m : parseString(tag_msg)) {
				template.send(destination, new ObjectMessageCreator(m));
			}

			try {
				Thread.sleep(pollInterval);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return CommandState.KILLED;
			}
		}
		if (Thread.interrupted()) {
			return CommandState.KILLED;
		}
		return CommandState.DONE;
	}

	/**
	 * Parse the given string for results.
	 * 
	 * @param input
	 * @return
	 */
	private List<BigInteger> parseString(String input) {
		String[] splitString = input.split("\n");

		List<BigInteger> retVal = new ArrayList<BigInteger>();

		try {
			for (String s : splitString) {
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

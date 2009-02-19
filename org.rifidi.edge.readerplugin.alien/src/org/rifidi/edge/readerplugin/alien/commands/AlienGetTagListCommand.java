/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.api.readerplugin.messages.impl.EnhancedTagMessage;
import org.rifidi.edge.core.api.readerplugin.messages.impl.TagMessage;
import org.rifidi.edge.core.commands.Command;
import org.rifidi.edge.core.commands.CommandState;
import org.rifidi.edge.readerplugin.alien.Alien9800Reader;

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
	private Integer tagType = 0;
	/** Time between two polls. */
	private Integer pollInterval = 10;
	/** Timezone from the reader. */
	private TimeZone timeZone;
	/** Calendar for creating timestamps. */
	private Calendar calendar;
	/** Set to falls to kill the callable. */
	private boolean running = true;

	/**
	 * @return the tagType
	 */
	public int getTagType() {
		return tagType;
	}

	/**
	 * @param tagType
	 *            the tagType to set
	 */
	public void setTagType(int tagType) {
		this.tagType = tagType;
		if (tagType > 2 || tagType < 0) {
			throw new RuntimeException("Sucker!");
		}
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
			logger.debug("received: " + tag_msg);

			List<TagMessage> tagList = this.parseString(tag_msg);

			for (TagMessage m : tagList) {
//				System.out.println("Message: " + new String(m.getId()));
				// getMessageQueue().addMessage(m);
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
	private List<TagMessage> parseString(String input) {
		String[] splitString = input.split("\n");

		List<TagMessage> retVal = new ArrayList<TagMessage>();

		try {
			for (String s : splitString) {
				s = s.trim();
				String[] splitString2 = s.split("\\|");
				if (splitString2.length > 1) {
					String tagData = splitString2[0];
					String timeStamp = splitString2[1];
					String antennaID = splitString2[2];

					Time time = Time.valueOf(timeStamp);
					calendar.setTime(time);
					Calendar currentDate = Calendar.getInstance(timeZone);
					calendar.set(currentDate.get(Calendar.YEAR), currentDate
							.get(Calendar.MONTH), currentDate
							.get(Calendar.DATE));

					EnhancedTagMessage newTagRead = new EnhancedTagMessage();
					newTagRead.setId(ByteAndHexConvertingUtility
							.fromHexString(tagData.trim()));
					newTagRead.setLastSeenTime(calendar.getTimeInMillis());

					// newTagRead.setVelocity(Float.parseFloat(velocity));

					newTagRead.setAntennaId(Integer.parseInt(antennaID));
					// newTagRead.setSignalStrength(Float.parseFloat(signalStrength));
					retVal.add(newTagRead);

				} else {
//					logger.error("Something isreaders invalid: " + splitString2[0]);
				}
			}
		} catch (Exception e) {
			logger.error("There was a problem when parsing Alien Tags.  "
					+ "tag has not been added", e);
		}
		return retVal;
	}
	
}

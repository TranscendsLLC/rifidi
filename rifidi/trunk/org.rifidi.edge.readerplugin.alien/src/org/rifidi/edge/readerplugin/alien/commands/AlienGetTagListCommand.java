package org.rifidi.edge.readerplugin.alien.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.readerplugin.alien.AbstractAlien9800Command;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienCommandObject;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienException;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienSetCommandObject;
import org.rifidi.edge.readerplugin.alien.commandobject.GetTagListCommandObject;
import org.rifidi.edge.readerplugin.alien.messages.AlienTag;
import org.rifidi.edge.readerplugin.alien.messages.AlienTagReadEventFactory;
import org.rifidi.edge.readerplugin.alien.messages.ReadCycleMessageCreator;

/**
 * An Command that runs on an AlienSession to get Tags back from an AlienReader
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class AlienGetTagListCommand extends AbstractAlien9800Command {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AlienGetTagListCommand.class);
	/** Tagtypes. */
	private Integer[] tagTypes = new Integer[] { 7, 16, 31 };
	/** Tagtypes to query for: 0 - GEN1 1 - GEN2 2 - ALL */
	private Integer tagType = 2;
	/** Antenna Sequence */
	private String antennasequence = "0";
	/** The readeriD */
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
	 * @param antennasequence
	 *            the antennasequence to set
	 */
	public void setAntennasequence(String antennasequence) {
		this.antennasequence = antennasequence;
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
			// send antennaSequence
			AlienCommandObject antennaCommand = new AlienSetCommandObject(
					Alien9800ReaderSession.ANTENNA_SEQUENCE_COMMAND,
					this.antennasequence,
					(Alien9800ReaderSession) this.sensorSession);
			antennaCommand.execute();

			// sending TagType
			AlienCommandObject tagTypeCommand = new AlienSetCommandObject(
					Alien9800ReaderSession.COMMAND_TAG_TYPE,
					tagTypes[getTagType()].toString(),
					(Alien9800ReaderSession) sensorSession);
			tagTypeCommand.execute();

			// sending TagListFormat
			AlienCommandObject tagListFormat = new AlienSetCommandObject(
					Alien9800ReaderSession.COMMAND_TAG_LIST_FORMAT, "text",
					(Alien9800ReaderSession) sensorSession);
			tagListFormat.execute();

			GetTagListCommandObject getTagListCommandObject = new GetTagListCommandObject(
					(Alien9800ReaderSession) sensorSession);

			AlienTagReadEventFactory factory = new AlienTagReadEventFactory(
					reader);
			Set<TagReadEvent> events = new HashSet<TagReadEvent>();
			List<AlienTag> tagList = getTagListCommandObject.executeGet();
			for (AlienTag tag : tagList) {
				events.add(factory.getTagReadEvent(tag));
			}
			ReadCycle cycle = new ReadCycle(events, reader, System
					.currentTimeMillis());
			sensorSession.getSensor().send(cycle);
			template.send(destination, new ReadCycleMessageCreator(cycle));

		} catch (AlienException e) {
			logger.warn("Exception while executing command: " + e);
		} catch (IOException e) {
			logger.warn("IOException while executing command: " + e);
		} catch (Exception e) {
			logger.error("Exception while executing command: " + e);
		}
	}

}

/*
 *  AlienVelocityTagListCommand.java
 *
 *  Created:	Dec 17, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.alien.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.ReadCycleMessageCreator;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.readerplugin.alien.AbstractAlien9800Command;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienCommandObject;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienException;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienSetCommandObject;
import org.rifidi.edge.readerplugin.alien.commandobject.GetTagListCommandObject;
import org.rifidi.edge.readerplugin.alien.messages.AlienTag;
import org.rifidi.edge.readerplugin.alien.messages.AlienVelocityTagReadEventFactory;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class AlienVelocityTagListCommand extends AbstractAlien9800Command {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AlienVelocityTagListCommand.class);
	/** Tagtypes. */
	private Integer[] tagTypes = new Integer[] { 7, 16, 31 };
	/** Tagtypes to query for: 0 - GEN1 1 - GEN2 2 - ALL */
	private Integer tagType = 2;
	/** Antenna Sequence */
	private String antennasequence = "0";
	/** The readeriD */
	private AtomicReference<String> reader = new AtomicReference<String>();

	public AlienVelocityTagListCommand(String commandID) {
		super(commandID);
	}

	/**
	 * Set the name of the reader this command is associated with.
	 * 
	 * @param reader
	 */
	public void setReader(String reader) {
		this.reader.set(reader);
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
			antennaCommand.setSession((Alien9800ReaderSession) sensorSession);
			antennaCommand.execute();

			// sending TagType
			AlienCommandObject tagTypeCommand = new AlienSetCommandObject(
					Alien9800ReaderSession.COMMAND_TAG_TYPE,
					tagTypes[getTagType()].toString(),
					(Alien9800ReaderSession) sensorSession);
			tagTypeCommand.setSession((Alien9800ReaderSession) sensorSession);
			tagTypeCommand.execute();

			// sending TagListFormat
			AlienCommandObject tagListFormat = new AlienSetCommandObject(
					Alien9800ReaderSession.COMMAND_TAG_LIST_FORMAT, "custom",
					(Alien9800ReaderSession) sensorSession);
			tagListFormat.setSession((Alien9800ReaderSession) sensorSession);
			tagListFormat.execute();

			// Setting the custom tag list format. This lets us get the velocity
			// and rssi information, in addition to all the regular information.
			// This tag list looks exatcly like the list coming back if the
			// taglistformat was set to "Text", except it has the speed and RSSI
			// information as well.
			AlienCommandObject tagListCustomFormat = new AlienSetCommandObject(
					Alien9800ReaderSession.COMMAND_TAG_LIST_CUSTOM_FORMAT,
					"Tag:%i, Disc:%d %T, Last:%d %T, Count:%r, "
							+ "Ant:%a, Proto:%p, Rssi:%m, Speed:%s",
					(Alien9800ReaderSession) sensorSession);
			tagListCustomFormat
					.setSession((Alien9800ReaderSession) sensorSession);
			tagListCustomFormat.execute();

			GetTagListCommandObject getTagListCommandObject = new GetTagListCommandObject(
					(Alien9800ReaderSession) sensorSession);

			AlienVelocityTagReadEventFactory factory = new AlienVelocityTagReadEventFactory(
					reader.get());
			Set<TagReadEvent> events = new HashSet<TagReadEvent>();
			List<AlienTag> tagList = getTagListCommandObject.executeGet();
			for (AlienTag tag : tagList) {
				events.add(factory.getTagReadEvent(tag));
			}
			// FIXME: parse the tags properly
			ReadCycle cycle = new ReadCycle(events, reader.get(), System
					.currentTimeMillis());
			sensorSession.getSensor().send(cycle);
			template.send(destination, new ReadCycleMessageCreator(cycle));
		} catch (AlienException e) {
			logger.error("Exception while executing command: ", e);
		} catch (IOException e) {
			logger.debug("IOException while executing command: ", e);
		} catch (Exception e) {
			logger.error("Exception while executing command: ", e);
		}
	}

}

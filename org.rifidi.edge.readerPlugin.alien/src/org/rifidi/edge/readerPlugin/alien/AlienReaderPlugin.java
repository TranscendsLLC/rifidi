/*
 *  AlienReaderPlugin.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.alien;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.CommunicationService;
import org.rifidi.edge.core.communication.CommunicationBuffer;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;
import org.rifidi.edge.readerPlugin.alien.command.AlienCustomCommand;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This class represents a plugin that will communicate with an Alien ALR-9800
 * reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AlienReaderPlugin implements IReaderPlugin {

	/**
	 * The log4j logger.
	 */
	private static final Log logger = LogFactory
			.getLog(AlienReaderPlugin.class);

	/**
	 * Communication connection.
	 */
	private CommunicationBuffer communicationConnection;

	/**
	 * Communication service.
	 */
	private CommunicationService communicationService;

	/**
	 * The connection info for this reader
	 */
	private AlienReaderInfo aci;

	/**
	 * Adapter for the Alien reader.
	 * 
	 * @param aci
	 *            The connection info for the Alien Reader.
	 */
	public AlienReaderPlugin(AlienReaderInfo aci) {
		this.aci = aci;

		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#connect()
	 */
	@Override
	public void connect() throws RifidiConnectionException {
		if (communicationService == null) {
			throw new RifidiConnectionException(
					"CommunicationSerivce Not Found!");
		}

		// Connect to the Alien Reader
		try {

			communicationConnection = communicationService.createConnection(
					this, aci, new AlienProtocol());

			logger.debug(aci.getIPAddress() + ", " + aci.getPort());
			String welcome = (String) communicationConnection.receive();
			if (!welcome.contains(AlienResponseList.WELCOME)) {
				logger.debug("RifidiConnectionException was thrown,"
						+ " reader is not an alien reader: " + welcome);
				throw new RifidiConnectionException(
						"Reader is not an alien reader");
			}
			communicationConnection.send(new String('\1' + aci.getUsername()
					+ "\n"));
			communicationConnection.receive();

			communicationConnection.send(new String('\1' + aci.getPassword()
					+ "\n"));

			String passwordResponse = (String) communicationConnection
					.receive();
			if (passwordResponse.contains(AlienResponseList.INVALID)) {
				logger.debug("RifidiConnectionException was thrown");
				throw new RifidiConnectionException(
						"Username/Password combination is invalid");
			}
		} catch (UnknownHostException e) {
			logger.debug("UnknownHostException.", e);
			throw new RifidiConnectionException(e);
		} catch (IOException e) {
			logger.debug("IOException.", e);
			throw new RifidiConnectionException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#disconnect()
	 */
	@Override
	public void disconnect() throws RifidiConnectionException {
		if (communicationService == null)
			throw new RifidiConnectionException(
					"CommunicationSerivce Not Found!");

		try {
			communicationConnection.send("q");
			communicationService.destroyConnection(communicationConnection);
		} catch (IOException e) {
			logger.debug("IOException.", e);
			throw new RifidiConnectionException(e);
		}
		logger.debug("Successfully Disconnected.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#sendCommand(byte[])
	 */
	@Override
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand)
			throws RifidiConnectionIllegalStateException,
			RifidiIIllegialArgumentException {

		try {
			AlienCustomCommand acc = (AlienCustomCommand) customCommand;
			communicationConnection.send(acc.getCommand());
			communicationConnection.receive();
		} catch (IOException e) {
			logger.debug("IOException.", e);
			throw new RifidiConnectionIllegalStateException(e);
		} catch (ClassCastException e) {
			logger.debug("ClassCastException.", e);
			throw new RifidiIIllegialArgumentException(e);
		} catch (NullPointerException e) {
			logger.debug("NullPointerException.", e);
			throw new RifidiIIllegialArgumentException(e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerPlugin.IReaderPlugin#getNextTags()
	 */
	@Override
	public List<TagRead> getNextTags()
			throws RifidiConnectionIllegalStateException {

		logger.debug("starting the getnexttags");

		List<TagRead> retVal = null;

		try {
			logger.debug("Sending the taglistformat to custom format");
			communicationConnection.send(AlienCommandList.TAG_LIST_FORMAT);
			String resp = (String) communicationConnection.receive();
			logger.debug("TAG_LIST_FORMAT response: " + resp);

			logger.debug("Sending the custom format");
			communicationConnection
					.send(AlienCommandList.TAG_LIST_CUSTOM_FORMAT);
			String cust = (String) communicationConnection.receive();

			logger.debug("TAG_LIST_CUSTOM_FORMAT response: " + cust);

			logger.debug("Reading tags");
			communicationConnection.send(AlienCommandList.TAG_LIST);

			String tags = (String) communicationConnection.receive();
			logger.debug("TAG_LIST response: " + tags);

			logger.debug("tags:" + tags);
			retVal = parseString(tags);
		} catch (IOException e) {
			logger.debug("IOException.", e);
			throw new RifidiConnectionIllegalStateException(e);
		}

		logger.debug("finishing the getnexttags");

		return retVal;
	}

	/**
	 * Parses the string.
	 * 
	 * @param input
	 *            The input string, consisting of all of the tag data.
	 * @return A list of TagRead objects parsed from the input string.
	 */
	private List<TagRead> parseString(String input) {
		String[] splitString = input.split("\n");

		logger.debug("Trying to parse this tag data: " + input);

		List<TagRead> retVal = new ArrayList<TagRead>();

		for (String s : splitString) {
			s = s.trim();
			String[] splitString2 = s.split("\\|");
			if (splitString2.length > 1) {
				String tagData = splitString2[0];
				// String timeStamp=splitString2[1];

				// TODO: Get the actual timestamp
				TagRead newTagRead = new TagRead();
				newTagRead.setId(ByteAndHexConvertingUtility
						.fromHexString(tagData.trim()));
				newTagRead.setLastSeenTime(System.nanoTime());
				retVal.add(newTagRead);

			}
		}

		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerPlugin.IReaderPlugin#isBlocking()
	 */
	@Override
	public boolean isBlocking() {
		return false;
	}

	/**
	 * List of Alien commands.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private static class AlienCommandList {
		/**
		 * Tag list command.
		 */
		public static final String TAG_LIST = ('\1' + "get taglist\n");

		/**
		 * Tag list format command.
		 */
		public static final String TAG_LIST_FORMAT = ('\1' + "set TagListFormat=Custom\n");

		/**
		 * Tag list custom format command.
		 */
		public static final String TAG_LIST_CUSTOM_FORMAT = ('\1' + "set TagListCustomFormat=%k|%t\n");
	}

	/**
	 * List of Alien commands.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private static class AlienResponseList {
		/**
		 * Tag list command.
		 */
		public static final String INVALID = "Invalid";

		/**
		 * Tag list command.
		 */
		public static final String WELCOME = "Alien";
	}

	/**
	 * Inject the communication service.
	 * 
	 * @param communicationService
	 *            The communication service to inject.
	 */
	@Inject
	public void setCommunicationService(
			CommunicationService communicationService) {
		logger.debug("communicationService set");
		this.communicationService = communicationService;

	}
}

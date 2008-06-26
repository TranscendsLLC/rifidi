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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.common.utilities.thread.AbstractThread;
import org.rifidi.edge.core.communication.buffer.CommunicationBuffer;
import org.rifidi.edge.core.communication.service.CommunicationService;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;
import org.rifidi.edge.readerPlugin.alien.command.AlienCommandResponse;
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

	private AlienReadThread alienReadThread = null;

	private AlienKeepAlive aka = null;

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

			this.aka = new AlienKeepAlive(communicationConnection);
			this.alienReadThread = new AlienReadThread(communicationConnection);
			alienReadThread.start();

			logger.debug(aci.getIPAddress() + ", " + aci.getPort());
			String welcome = alienReadThread.getWelcome();
			if (welcome == null || !welcome.contains(AlienResponseList.WELCOME)) {
				logger.debug("RifidiConnectionException was thrown,"
						+ " reader is not an alien reader: " + welcome);
				throw new RifidiConnectionException(
						"Reader is not an alien reader");
			}
			write(new String('\1' + aci.getUsername() + "\n"),
					communicationConnection);

			write(new String('\1' + aci.getPassword() + "\n"),
					communicationConnection);

			String passwordResponse = alienReadThread.getInvalid();

			if (passwordResponse != null) {
				if (passwordResponse.contains(AlienResponseList.INVALID)) {
					logger.debug("RifidiConnectionException was thrown");
					throw new RifidiConnectionException(
							"Username/Password combination is invalid");
				}
			} else {
				pause(1000);
				aka.start();
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
			aka.stop();
			pause(1000);
			write("q",communicationConnection);
			pause(500);
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
			write(acc.getCommand(), communicationConnection);
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
			write(AlienCommandList.TAG_LIST_FORMAT, communicationConnection);

			logger.debug("Sending the custom format");
			write(AlienCommandList.TAG_LIST_CUSTOM_FORMAT,
					communicationConnection);

			logger.debug("Reading tags");
			write(AlienCommandList.TAG_LIST, communicationConnection);

			String tags = alienReadThread.getNextTags();
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
	 * Writes a message out to an alien reader.
	 * 
	 * @param message
	 * @throws IOException
	 */
	private synchronized void write(String message,
			CommunicationBuffer connection) throws IOException {
		connection.send(message);
	}

	/**
	 * A class that reads in messages from the buffers.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class AlienReadThread extends Thread {

		/**
		 * A queue to store incoming LLRP Messages
		 */
		private LinkedBlockingQueue<String> commandQueue = null;

		/**
		 * A queue to store incoming LLRP Messages
		 */
		private LinkedBlockingQueue<String> tagQueue = null;

		/**
		 * A queue to store incoming LLRP Messages
		 */
		private LinkedBlockingQueue<String> welcomeQueue = null;

		/**
		 * A queue to store incoming LLRP Messages
		 */
		private LinkedBlockingQueue<String> invalidQueue = null;

		/**
		 * The connection object.
		 */
		private CommunicationBuffer connection;

		/**
		 * 
		 */
		public AlienReadThread(CommunicationBuffer comm) {
			commandQueue = new LinkedBlockingQueue<String>();
			tagQueue = new LinkedBlockingQueue<String>();
			welcomeQueue = new LinkedBlockingQueue<String>();
			invalidQueue = new LinkedBlockingQueue<String>();
			this.connection = comm;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			super.run();
			boolean isRunning = true;
			while (isRunning) {
				AlienCommandResponse message = null;
				try {
					message = read();
					if (message != null) {
						if (message.getType().equals(
								AlienCommandResponse.AlienMessageType.TAG_TYPE)) {
							logger.debug("Adding a tagType message");
							tagQueue.add(message.getCommand());
						} else if (message
								.getType()
								.equals(
										AlienCommandResponse.AlienMessageType.OTHER_TYPE)) {
							// commandQueue.add(message.getCommand());
						} else if (message
								.getType()
								.equals(
										AlienCommandResponse.AlienMessageType.WELCOME_TYPE)) {
							welcomeQueue.add(message.getCommand());
						} else if (message
								.getType()
								.equals(
										AlienCommandResponse.AlienMessageType.INVALID_TYPE)) {
							invalidQueue.add(message.getCommand());
						}
					} else {
						logger.info("closing socket");
						isRunning = false;
					}
				} catch (IOException e) {
					logger.error("Error while reading message", e);
					isRunning = false;
				}
			}
		}

		/**
		 * Read everything from the stream until the socket is closed
		 * 
		 * @throws InvalidLLRPMessageException
		 */
		public AlienCommandResponse read() throws IOException {
			AlienCommandResponse m = (AlienCommandResponse) connection
					.receive();
			return m;
		}

		/**
		 * Gets the next tag message
		 */
		public String getNextTags() {
			String retVal = null;
			try {
				retVal = tagQueue.take();
			} catch (InterruptedException e) {
			}
			return retVal;
		}

		/**
		 * Gets the next tag message
		 */
		public String getNextCommand() {
			String retVal = null;
			try {
				retVal = commandQueue.take();
			} catch (InterruptedException e) {
			}
			return retVal;
		}

		/**
		 * Gets the next tag message
		 */
		public String getWelcome() {
			String retVal = null;
			try {
				retVal = welcomeQueue.poll(2000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
			}
			return retVal;
		}

		/**
		 * Gets the next tag message
		 */
		public String getInvalid() {
			String retVal = null;
			try {
				retVal = invalidQueue.poll(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
			}
			return retVal;
		}
	}

	/**
	 * This thread keeps alive the Alien by sending it a version command every 5
	 * seconds.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class AlienKeepAlive extends AbstractThread {

		/**
		 * The connection thread.
		 */
		private CommunicationBuffer connection = null;

		public static final String READER_VERSION = "get ReaderVersion";

		/**
		 * 
		 * 
		 * @param connection
		 */
		public AlienKeepAlive(CommunicationBuffer connection) {
			super("Alien Keep Alive Thread, AKA \"Cuddles\"");
			this.connection = connection;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			try {
				while (running) {
					write(READER_VERSION, connection);
					pause(5000);
				}
			} catch (InterruptedException e) {
				running = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * This method causes the calling thread to sleep for a specified number
		 * of milliseconds
		 * 
		 * @param ms
		 *            How many milliseconds to pause.
		 * @throws InterruptedException
		 */
		private void pause(long ms) throws InterruptedException {
			Thread.sleep(ms);
		}
	}
	
	/**
	 * This method causes the calling thread to sleep for a specified number
	 * of milliseconds
	 * 
	 * @param ms
	 *            How many milliseconds to pause.
	 */
	private void pause(long ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

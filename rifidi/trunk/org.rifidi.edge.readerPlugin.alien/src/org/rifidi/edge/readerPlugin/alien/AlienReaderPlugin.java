/*
 *  AlienReaderAdapter.java
 *
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.alien;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
/**
 * @author Matthew Dean - matt@pramari.com
 *
 */
public class AlienReaderPlugin implements IReaderPlugin {

	private static final Log logger = LogFactory
			.getLog(AlienReaderPlugin.class);
	/**
	 * The connection info for this reader
	 */
	private AlienReaderInfo aci;

	private Socket connection = null;
	private BufferedReader in = null;
	private PrintWriter out = null;

	/**
	 * Adapter for the Alien reader.
	 * 
	 * @param aci
	 *            The connection info for the Alien Reader.
	 */
	public AlienReaderPlugin(AlienReaderInfo aci) {
		this.aci = aci;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#connect()
	 */
	@Override
	public void connect() throws RifidiConnectionException {
		// Connect to the Alien Reader
		try {
			logger.debug(aci.getIPAddress() + ", " + aci.getPort());
			connection = new Socket(aci.getIPAddress(), aci.getPort());
			out = new PrintWriter(connection.getOutputStream());
			in = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
			out.write("alien\n");
			out.flush();
			readFromReader(in);
			out.write("password\n");
			out.flush();
			readFromReader(in);

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
	public void disconnect() throws RifidiConnectionException{
		out.write("q");
		out.flush();
		try {
			connection.close();
		} catch (IOException e) {
			logger.debug("IOException.", e);
			throw new RifidiConnectionException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#sendCommand(byte[])
	 */
	@Override
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand) 
			throws RifidiConnectionIllegalStateException, RifidiIIllegialArgumentException {
		/*try {
			out.write(new String(command));
			readFromReader(in);
		} catch (IOException e) {
			logger.debug("IOException.", e);
		}*/
		return null;
	}

	/**
	 * Gets the next tag
	 * 
	 */
	@Override
	public List<TagRead> getNextTags() throws RifidiConnectionIllegalStateException {

		logger.debug("starting the getnexttags");

		List<TagRead> retVal = null;

		try {
			logger.debug("Sending the taglistformat to custom format");
			out.write('\1' + "set TagListFormat=Custom\n");
			out.flush();
			readFromReader(in);

			logger.debug("Sending the custom format");
			out.write('\1' + "set TagListCustomFormat=%k|%t\n");
			out.flush();
			readFromReader(in);

			logger.debug("Reading tags");
			out.write('\1' + "get taglist\n");
			out.flush();

			// TODO: This is a bit of a hack,
			String tags = readFromReader(in);
			tags = readFromReader(in);
			logger.debug("tags:" + tags);
			retVal = parseString(tags);
		} catch (IOException e) {
			logger.debug("IOException.", e);
		} catch (NullPointerException e) {
			logger.debug("NullPointerException.", e);
		}

		logger.debug("finishing the getnexttags");

		return retVal;
	}

	/**
	 * Parses the string.
	 * 
	 * @param input
	 * @return
	 */
	private List<TagRead> parseString(String input) {
		String[] splitString = input.split("\n");

		List<TagRead> retVal = new ArrayList<TagRead>();

		for (String s : splitString) {
			s = s.trim();
			String[] splitString2 = s.split("|");
			String tagData = splitString2[0];
			// String timeStamp=splitString2[1];

			// TODO: Get the actual timestamp

			TagRead newTagRead = new TagRead();
			newTagRead
					.setId(ByteAndHexConvertingUtility.fromHexString(tagData));
			newTagRead.setLastSeenTime(System.nanoTime());
			retVal.add(newTagRead);
		}
		return retVal;
	}

	/**
	 * Read responses from the socket
	 * 
	 * @param inBuf
	 * @return
	 * @throws IOException
	 */
	public static String readFromReader(BufferedReader inBuf)
			throws IOException {
		StringBuffer buf = new StringBuffer();
		logger.debug("Reading...");
		int ch = inBuf.read();
		while ((char) ch != '\0') {
			buf.append((char) ch);
			ch = inBuf.read();
		}
		logger.debug("Done reading!");
		logger.debug("Reading in: " + buf.toString());
		return buf.toString();
	}

	@Override
	public boolean isBlocking() {
		return false;
	}
}

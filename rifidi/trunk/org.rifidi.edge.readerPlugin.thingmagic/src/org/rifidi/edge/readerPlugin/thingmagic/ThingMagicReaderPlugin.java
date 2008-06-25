/*
 *  ThingMagicReaderPlugin.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.thingmagic;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.buffer.CommunicationBuffer;
import org.rifidi.edge.core.communication.service.CommunicationService;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;
import org.rifidi.edge.readerPlugin.thingmagic.commands.ThingMagicCustomCommand;
import org.rifidi.edge.readerPlugin.thingmagic.commands.ThingMagicCustomCommandResult;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;


//TODO: Try this junit test on a /real/ thing magic reader
/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class ThingMagicReaderPlugin implements IReaderPlugin {
	
	private static final Log logger = LogFactory.getLog(ThingMagicReaderPlugin.class);	
	
	boolean connected = false;

	// the communication buffer used to talk to the reader
	private CommunicationBuffer communicationBuffer;

	// The information about a specific reader.
	private ThingMagicReaderInfo tmci;

	// Communication services
	private CommunicationService communicationService;
	
	public ThingMagicReaderPlugin(ThingMagicReaderInfo connectionInfo){
		tmci = connectionInfo;
		ServiceRegistry.getInstance().service(this);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPlugin.IReaderPlugin#connect()
	 */
	@Override
	public void connect() throws RifidiConnectionException {
		if (communicationService == null)
			throw new RifidiConnectionException("CommunicationSerivce Not Found!");
		
		try {
			logger.debug("Connecting: " + tmci.getIPAddress() + ":" + tmci.getPort() + " ...");

			communicationBuffer = communicationService.createConnection(this, tmci, new ThingMagicProtocol());
	
			connected = true;
		} catch (UnknownHostException e) {
			logger.error("Unknown host.", e);
			throw new RifidiConnectionException("Unknown host.", e);
		} catch (ConnectException e){
			logger.error("Connection to reader refused.");
			logger.error("Please check if the reader is properly turned on and connected to the network.");
			//System.out.println("Stack trace follows...");
			//e.printStackTrace();
			
			//logger.error("ConnectException...",e);
			throw new RifidiConnectionException(
					"Connection to reader refused. " +
					"Please check if the reader is properly turned on and connected to the network.", e);
		} catch (IOException e) {
			logger.error("IOException occured.",e);
			throw new RifidiConnectionException("IOException occured.", e);
		}
		logger.debug("Successfully Connected.");
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPlugin.IReaderPlugin#disconnect()
	 */
	@Override
	public void disconnect() throws RifidiConnectionException {
		connected=false;
		if (communicationService == null)
			throw new RifidiConnectionException("CommunicationSerivce Not Found!");
		
		try {
			communicationService.destroyConnection(communicationBuffer);
		} catch (IOException e){
			logger.debug("IOException.", e);
			throw new RifidiConnectionException(e);
		}
		logger.debug("Successfully Disconnected.");
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPlugin.IReaderPlugin#getNextTags()
	 */
	@Override
	public List<TagRead> getNextTags() throws RifidiConnectionIllegalStateException{
		String input = null;
		List<TagRead> tags = new ArrayList<TagRead>();
		
		if(connected){
			logger.debug("Getting next tags.");
			
			
			try {
				communicationBuffer.send("select id, timestamp from tag_id;\n");
				
				 input = (String) communicationBuffer.receive();
				 logger.debug(input);
			} catch (IOException e) {
				logger.debug("IOException has accured.", e);
				throw new RifidiConnectionIllegalStateException(e);
			}
			
			if (input.equals("\n"))				
				return tags;

			// All tags returned are separated by newlines
			String[] rawTags = input.split("\n");
			
			
			
			for (String rawTag: rawTags){
				logger.debug(rawTag);
				
				//All tag data sent back is separated by vertical bars.
				String[] rawTagItems = rawTag.split("\\|");
				
				TagRead tag = new TagRead();
				
				
				logger.debug(rawTagItems[0]);
				
				tag.setId(ByteAndHexConvertingUtility.fromHexString(rawTagItems[0].substring(2, rawTagItems[0].length())));
				
				//TODO: correct the time stamps.
				tag.setLastSeenTime(System.nanoTime()); 
				tags.add(tag);
			}
			return tags;
		}
		throw new RifidiConnectionIllegalStateException("Adapter not Connected to Reader.");
	}


	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPlugin.IReaderPlugin#sendCustomCommand(org.rifidi.edge.core.readerPlugin.commands.ICustomCommand)
	 */
	@Override
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand)
			throws RifidiConnectionIllegalStateException, RifidiIIllegialArgumentException
	{
		ThingMagicCustomCommand command;
		ThingMagicCustomCommandResult result = null;
		if(customCommand == null)
			throw new RifidiIIllegialArgumentException();
		
		if(customCommand instanceof ThingMagicCustomCommand){
			command = (ThingMagicCustomCommand) customCommand;
			if(command.getCustomCommand() == null)
				throw new RifidiIIllegialArgumentException();
			else 
				if (command.getCustomCommand().equals("") || command.getCustomCommand().endsWith(";"))
					throw new RifidiIIllegialArgumentException();
		} else {
			throw new RifidiIIllegialArgumentException();
		}
		
		if (!connected ){
			try {
				communicationBuffer.send(command.getCustomCommand());

				//TODO check if result is actually an error
				result = new ThingMagicCustomCommandResult((String) communicationBuffer.receive());
			} catch (IOException e) {
				logger.debug("IOException has accured.", e);
				throw new RifidiConnectionIllegalStateException(e.getClass().getName(), e);
			}
			
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPlugin.IReaderPlugin#isBlocking()
	 */
	@Override
	public boolean isBlocking() {
		return false;
	}

	@Inject
	public void setCommunicationService(
			CommunicationService communicationService) {
		logger.debug("communicationService set");
		this.communicationService = communicationService;
	}
}

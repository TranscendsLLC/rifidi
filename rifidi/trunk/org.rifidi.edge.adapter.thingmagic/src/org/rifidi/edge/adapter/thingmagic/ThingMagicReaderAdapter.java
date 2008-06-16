package org.rifidi.edge.adapter.thingmagic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.thingmagic.commands.ThingMagicCustomCommand;
import org.rifidi.edge.adapter.thingmagic.commands.ThingMagicCustomCommandResult;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.adapter.RifidiAdapterIllegalStateException;
import org.rifidi.edge.core.exception.adapter.RifidiConnectionException;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommand;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;


//TODO: Try this junit test on a /real/ thing magic reader
public class ThingMagicReaderAdapter implements IReaderAdapter {
	
	private static final Log logger = LogFactory.getLog(ThingMagicReaderAdapter.class);	
	
	boolean connected = false;
	
	private Socket connection = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	
	private ThingMagicConnectionInfo tmci;
	public ThingMagicReaderAdapter(ThingMagicConnectionInfo connectionInfo){
		tmci = connectionInfo;
	}
	
	@Override
	public void connect() throws RifidiConnectionException {
		try {
			logger.debug("Connecting: " + tmci.getIPAddress() + ":" + tmci.getPort() + " ...");
			connection = new Socket(tmci.getIPAddress(), tmci.getPort());
			
			out = new PrintWriter(connection.getOutputStream());
			
			in = new BufferedReader(new InputStreamReader(connection
				.getInputStream()));
			
			//logger.debug(readFromReader(in));
			
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

	@Override
	public void disconnect() throws RifidiConnectionException {
		connected=false;
		out.flush();
		try {
			connection.close();
		} catch (IOException e){
			logger.debug("IOException.", e);
			throw new RifidiConnectionException(e);
		}
		logger.debug("Successfully Disconnected.");
	}

	@Override
	public List<TagRead> getNextTags() throws RifidiAdapterIllegalStateException{
		String input = null;
		List<TagRead> tags = new ArrayList<TagRead>();
		if(connected){
			
			out.write("select id, timestamp from tag_id;\n");
			out.flush();
			
			try {
				 input = readFromReader(in);
			} catch (IOException e) {
				logger.debug("IOException.", e);
				throw new RifidiAdapterIllegalStateException(e);
			}
			
			if (input.equals("\n"))				
				return tags;
			
			//chew up last new lines.
			input = input.substring(0, input.lastIndexOf("\n\n"));
			
			//logger("Input: " + input.replace("\n", "\\n"));
			
			String[] rawTags = input.split("\n");
			
			
			
			for (String rawTag: rawTags){
				logger.debug(rawTag);
				
				String[] rawTagItems = rawTag.split("\\|");
				
				TagRead tag = new TagRead();
				
				tag.setId(ByteAndHexConvertingUtility.fromHexString(rawTagItems[0].substring(2, rawTagItems[0].length())));
				
				//TODO: correct the time stamps.
				tag.setLastSeenTime(System.nanoTime()); 
				tags.add(tag);
			}
			return tags;
		}
		throw new RifidiAdapterIllegalStateException("Adapter not Connected to Reader.");
	}


	@Override
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand)
			throws RifidiAdapterIllegalStateException, RifidiIIllegialArgumentException
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
				out.write(command.getCustomCommand());
				out.flush();
				//TODO check if result is actually an error
				result = new ThingMagicCustomCommandResult(readFromReader(in));
			} catch (IOException e) {
				logger.debug("IOException has accured.", e);
				throw new RifidiAdapterIllegalStateException(e.getClass().getName(), e);
			}
			
		}
		return result;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	
	public static String readFromReader(BufferedReader inBuf) throws IOException{
		StringBuffer buf=new StringBuffer();
		
		//String temp = inBuf.readLine();
		/*while(temp != null){
			buf.append(temp);
			temp = inBuf.readLine();
		}*/
		
		//TODO: See if this is really needed on the real thing magic reader.
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.debug("InterruptedException.", e);
		}
		while(inBuf.ready()){
			int ch=inBuf.read();
			buf.append((char)ch);
		}
		/*
		int ch=inBuf.read();
		while(ch != -1){
			buf.append((char)ch);
			ch=inBuf.read();
		}*/
		
		return buf.toString();
	}
}

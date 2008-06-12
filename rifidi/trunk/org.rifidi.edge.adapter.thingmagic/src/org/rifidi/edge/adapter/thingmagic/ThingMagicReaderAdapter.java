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
import org.rifidi.common.utilities.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
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
	public boolean connect() {
		try {
			logger.debug("Connecting: " + tmci.getIPAddress() + ":" + tmci.getPort() + " ...");
			connection = new Socket(tmci.getIPAddress(), tmci.getPort());
			
			out = new PrintWriter(connection.getOutputStream());
			
			in = new BufferedReader(new InputStreamReader(connection
				.getInputStream()));
			
			//logger.debug(readFromReader(in));
			
			connected = true;
		} catch (UnknownHostException e) {
			logger.debug("UnknownHostException.", e);
			return false;
		} catch (ConnectException e){
			logger.debug("Connection to reader refused.");
			logger.debug("Please check if the reader is properly turned on and connected to the network.");
			//System.out.println("Stack trace follows...");
			//e.printStackTrace();
			return false;
		} catch (IOException e) {
			logger.debug("IOException occured.",e);
			return false;
		}
		logger.debug("Successfully Connected.");
		return true;
	}

	@Override
	public boolean disconnect() {
		connected=false;
		out.flush();
		try {
			connection.close();
		} catch (IOException e){
			logger.debug("IOException.", e);
			return false;
		}
		logger.debug("Successfully Disconnected.");
		return true;
	}

	@Override
	public List<TagRead> getNextTags() {
		String input = null;
		List<TagRead> tags = new ArrayList<TagRead>();
		if(connected){
			
			out.write("select id, timestamp from tag_id;\n");
			out.flush();
			
			try {
				 input = readFromReader(in);
			} catch (IOException e) {
				//TODO print stack trace to log4j
				logger.debug("IOException.", e);
				return null;
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
		return null;
	}

	@Override
	public void sendCommand(byte[] command) {
		if (!connected ){
			// TODO This needs to be implemented more fully.
			try {
				out.write(new String(command));
				readFromReader(in);
			} catch (IOException e) {
				//TODO print stack trace to log4j
				logger.debug("IOException.", e);
			}
			
		}

	}

	@Override
	public boolean isBlocking() {
		// TODO Auto-generated method stub
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

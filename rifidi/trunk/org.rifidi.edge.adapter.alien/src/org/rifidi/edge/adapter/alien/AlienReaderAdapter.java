/**
 * 
 */
package org.rifidi.edge.adapter.alien;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.rifidi.common.utilities.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.tag.TagRead;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class AlienReaderAdapter implements IReaderAdapter {

	/**
	 * The connection info for this reader
	 */
	private AlienConnectionInfo aci;

	private Socket connection = null;
	private BufferedReader in = null;
	private PrintWriter out = null;

	/**
	 * Adapter for the Alien reader.
	 * 
	 * @param aci
	 *            The connection info for the Alien Reader.
	 */
	public AlienReaderAdapter(AlienConnectionInfo aci) {
		this.aci = aci;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#connect()
	 */
	@Override
	public void connect() {
		// Connect to the Alien Reader
		try {
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#disconnect()
	 */
	@Override
	public void disconnect() {
		out.write("q");
		out.flush();
		try {
			connection.close();
		} catch (IOException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#sendCommand(byte[])
	 */
	@Override
	public void sendCommand(byte[] command) {
		try {
			out.write(new String(command));
			readFromReader(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#startStreamTags()
	 */
	@Override
	public void startStreamTags() {
		// Nothing needed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#stopStreamTags()
	 */
	@Override
	public void stopStreamTags() {
		// Nothing needed
	}

	/**
	 * Gets the next tag
	 * 
	 */
	@Override
	public List<TagRead> getNextTags() {
		// TODO Auto-generated method stub
		List<TagRead> retVal = null;

		try {
			out.write("set TagListFormat=Custom");
			out.flush();
			readFromReader(in);
			
			out.write("set TagListCustomFormat=%k|%t");
			out.flush();
			readFromReader(in);
			out.write("t");
			
			retVal = parseString(readFromReader(in));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			
			//TODO: Get the actual timestamp
			
			TagRead newTagRead = new TagRead();
			newTagRead
					.setId(ByteAndHexConvertingUtility.fromHexString(tagData));
			newTagRead.setLastSeenTime(System.nanoTime());
		}
		return retVal;
	}
	
	/**
	 * Read responses from the socket
	 * @param inBuf
	 * @return
	 * @throws IOException
	 */
	public static String readFromReader(BufferedReader inBuf) throws IOException{
		StringBuffer buf=new StringBuffer();
		int ch=inBuf.read();
		while((char)ch!='\0'){
			buf.append((char)ch);
			ch=inBuf.read();
		}
		return buf.toString();
	}
}

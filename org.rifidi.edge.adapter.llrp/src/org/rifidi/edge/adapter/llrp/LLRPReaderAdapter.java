/**
 * 
 */
package org.rifidi.edge.adapter.llrp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.tag.TagRead;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class LLRPReaderAdapter implements IReaderAdapter {
	
	private static final Log logger = LogFactory.getLog(LLRPReaderAdapter.class);	
	
	/**
	 * The connection info for this reader
	 */
	private LLRPConnectionInfo aci;

	private Socket connection = null;
	private BufferedReader in = null;
	private PrintWriter out = null;

	/**
	 * Adapter for the Alien reader.
	 * 
	 * @param aci
	 *            The connection info for the Alien Reader.
	 */
	public LLRPReaderAdapter(LLRPConnectionInfo aci) {
		this.aci = aci;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#connect()
	 */
	@Override
	public boolean connect() {
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
			return false;
		} catch (IOException e) {
			logger.debug("IOException.", e);
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#disconnect()
	 */
	@Override
	public boolean disconnect() {
		out.write("q");
		out.flush();
		try {
			connection.close();
		} catch (IOException e) {
			return false;
		}
		return true;
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
			logger.debug("IOException.", e);
		}
	}

	/**
	 * Gets the next tag
	 * 
	 */
	@Override
	public List<TagRead> getNextTags() {
		// TODO Auto-generated method stub
		
		logger.debug("starting the getnexttags");
		
		List<TagRead> retVal = null;

		try {
			logger.debug("Sending the taglistformat to custom format");
			out.write('\1'+"set TagListFormat=Custom\n");
			out.flush();
			readFromReader(in);
			
			logger.debug("Sending the custom format");
			out.write('\1'+"set TagListCustomFormat=%k|%t\n");
			out.flush();
			readFromReader(in);
			
			logger.debug("Reading tags");
			out.write('\1'+"get taglist\n");
			out.flush();
			
			
			//TODO: This is a bit of a hack, 
			String tags = readFromReader(in);
			tags=readFromReader(in);
			logger.debug("tags:" + tags);
			retVal = parseString(tags);
		} catch (IOException e) {
			logger.debug("IOException.", e);
		} catch(NullPointerException e) {
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
		
		return null;
	}
	
	/**
	 * Read responses from the socket
	 * @param inBuf
	 * @return
	 * @throws IOException
	 */
	public static String readFromReader(BufferedReader inBuf) throws IOException{
		StringBuffer buf=new StringBuffer();
		logger.debug("Reading...");
		int ch=inBuf.read();
		while((char)ch!='\0'){
			buf.append((char)ch);
			ch=inBuf.read();
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

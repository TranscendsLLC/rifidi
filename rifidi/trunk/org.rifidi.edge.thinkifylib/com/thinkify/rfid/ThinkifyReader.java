package com.thinkify.rfid;

import javax.comm.*;
import java.io.*;
import java.util.concurrent.TimeoutException;


/**
 * ThinkifyReader connects to a Thinkify RFID reader over a serial port, and handles
 * the basic send, receive, send-receive functionality required to communicate with
 * Thinkify readers.
 * <p>
 * Many convenience methods are provided, to abstract out many of the RFID details.
 * For instance, to get the firmware version, call getVersion() and you will get a
 * string-representation of the reader's response. To read the strongest tag in view
 * of the reader, call find_strongest_tag() and you will get a ThinkifyTag back, which
 * contains all of the details of the tag that was read. Use the setAutoMode(true/false)
 * method to start and stop background tag-reading. In AutoMode, the reader will
 * read tags and they will be collected by the API. Access the ThinkifyTaglist directly
 * from the ThinkifyReader object (not very thread-safe at this point) to process the
 * reads that are collected this way.
 * <p>
 * Of course, you could always use send_receive("t63") and parse the resulting list
 * of tag data from the reader. Most of the other sundry reader commands must be
 * executed this way.
 * 
 * @author David Krull
 */
public class ThinkifyReader implements SerialPortEventListener {
	
	private boolean                isConnected;
	private boolean                isAutoMode = false;
	private SerialManager          sm;
	private int                    timeoutMillis = 5000;
	private BufferedReader         inReader;
	private BufferedWriter         outWriter;
	private StringBuffer           inBuffer;
	private String                 readerResponse;
	private boolean                responseIsComplete;
	private static final String    PROMPT = "READY>";

	
	/**
	 * Control the amount of debugging output to see. Leaving debugLevel=0
	 * allows only the basic application startup/shutdown and error messages.
	 * Setting debugLevel=1 enables top-level read/write logging, and higher
	 * values of debugLevel may offer even more granular logging of data.
	 */
	public int                 debugLevel = 0;
	
	/**
	 * This is direct access to the internal ThinkifyTaglist. This list is
	 * used to accumulate tag data received in a streaming mode from the reader.
	 * It may not be a particularly thread-safe way to do things.
	 */
	public ThinkifyTaglist     taglist;
	
	/**
	 * Static String used to indicate the version number of this build of the API.
	 */
	public static final String VERSION = "0.8"; 
	
	
	/**
	 * Create an object representing a ThinkifyReader connected via a serial
	 * interface. The address of the reader, it's serial port name, is provided
	 * as the sole argument to this class's constructor.
	 * <p>
	 * The reader normally communicates at 115,200 baud. The accompanying SerialManager
	 * class is used to handle the details of the serial interface. There are
	 * also a static getAvailableSerialPorts() method for asking the JRE what
	 * serial port names are available. My reader shows up as "usbmodem411",
	 * which happens to be mounted at /dev/tty.usbmodem411.
	 * 
	 * @param address the name of the serial port where the reader is connected
	 */
	public ThinkifyReader(String address) {
		this.isConnected = false;
		inBuffer = new StringBuffer();
		responseIsComplete = false;
		
		sm = new SerialManager();
		sm.setPortName(address);
		sm.setBaudRate(230400);
		
		isAutoMode = false;
		taglist = new ThinkifyTaglist();
	}
	

	private PrintStream originalStdOut = System.out;
	private PrintStream nullStdOut = new PrintStream(new OutputStream() { public void write(int b) {} } );
	private void suppressStdOut(boolean suppress) {
		System.setOut(suppress ? nullStdOut : originalStdOut);
	}
	
	/**
	 * Opens a serial connection to the reader.
	 * 
	 * @throws Exception
	 */
	public void open() throws Exception {
		if (isOpen()) return;

		// Just in case something is still open...
		close();

		if (sm == null) {
			throw new Exception("Can't open serial connection - no Java serial libraries were found.");
		}
		sm.setSerialTimeout(timeoutMillis);

		if (debugLevel > 0) System.out.println("Opening reader");
		try {
			suppressStdOut(true);
			sm.open();
		} catch(Exception e) {
			close();
			throw e;
		} finally {
			suppressStdOut(false);
		}

		// Plug into the serial manager's in/out streams
		// The Reader & Writer wrap the streams and provide characters instead of bytes
		this.inReader = new BufferedReader(new InputStreamReader(sm.getInputStream()));
		this.outWriter = new BufferedWriter(new OutputStreamWriter(sm.getOutputStream()));
		
		// Tell the serial port to send read events to us
		sm.getSerialPort().addEventListener(this);
        sm.getSerialPort().notifyOnDataAvailable(true);

		// Indicate that we are now in the connected state
		isConnected = true;
	}
	
	
	/**
	 * Reports whether the connection to the reader has been opened yet.
	 * 
	 * @return the connected state of the reader
	 */
	public boolean isOpen() {
		return isConnected;
	}
	
	
	/**
	 * Closes the serial connection with the reader.
	 */
	public void close() {
		if (debugLevel > 0) System.out.println("Closing Port");
		
		sm.close();
		isConnected = false;
	}

	
	/**
	 * Sends a text string to the reader. The line terminator, <cr><lf> is automatically
	 * appended to your command string.
	 * 
	 * @param text the string to send to the reader
	 * @throws IOException
	 */
	public void send(String text) throws IOException {
		if (!isOpen()) return;
		if (text == null) text = "";
		
		if (debugLevel > 0) System.out.println("Sending " + text);
		outWriter.write(text + "\r\n");
		outWriter.flush();
	}
	
	
	/**
	 * Waits for, and returns, a response from the reader. Exits with a TimeoutException if
	 * a complete response isn't received within timeoutMillis. A complete response from a
	 * reader always ends with the "READY>" prompt.
	 * <p>
	 * A timeout of 0 means "just take a peek", and a timeout of -1 means "wait forever".
	 * 
	 * @param timeoutMillis maximum time to wait for the response
	 * @return the reader's response
	 * @throws TimeoutException if the response isn't received within the timeout
	 */
	public String receive(int timeoutMillis) throws TimeoutException {
		String response = null;

		long startTime = System.currentTimeMillis();
		boolean done = false;
		while (!done) {
			if (responseIsComplete) {
				// Return it - we're done!
				response = this.readerResponse;
				responseIsComplete = false;
				break;
			}
			
			if (timeoutMillis == 0) {
				// 0 means "just checking"
				done = true;
			} else if (timeoutMillis > 0) {
				// Positive timeout - check the clock
				long now = System.currentTimeMillis();
				if (now-startTime > timeoutMillis) {
					throw new TimeoutException("Receive timeout of " + timeoutMillis + " was exceeded.");
				}
			} // -1 means "forever"
			
			try { Thread.sleep(10);	} catch (InterruptedException e) {}
		};

		if (debugLevel > 0) System.out.println("Received:\n" + response);
		return response;
	}
	
	
	/**
	 * Sends the given cmd (also appending the line terminator before sending), and then waits
	 * for, and returns, the reader's entire response.
	 *
	 * @param cmd the text to send to the reader
	 * @return the reader's response
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public String send_receive(String cmd) throws IOException, TimeoutException {
		if (cmd == null) cmd = "";
		cmd = cmd.trim();
		
		boolean automodeOn = isAutoMode;
		
		if (automodeOn)	setAutoMode(false);
		send(cmd + "\r\n");
		String response = receive(timeoutMillis);
		if (automodeOn) setAutoMode(true);

		return response;
	}
	
	
	/**
	 * Performs a send_receive() operation with the given cmd string, but parses the 
	 * reader's reply, returning only the "value" part of the response (everything
	 * after the first =). This is useful for commands like "ra" (get RF attenuation),
	 * where you only want the numeric part of the response, and not the echoing-back
	 * of the parameter you are requesting.
	 * 
	 * @param cmd the text to send to the reader
	 * @return the "value" portion of the reader's response
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public String execute(String cmd) throws IOException, TimeoutException {
		String response = send_receive(cmd);
		
		if (response.indexOf("=") >= 0) {
			return response.split("=")[1].trim();
		} else {
			return response;
		}
	}
	
	
	/**
	 * Set's the reader's four front LEDs to match the bit-map of the argument, ledState.
	 * The first bit of ledState corresponds to the first LED, the second bit corresponds to
	 * the 2nd LED, etc. all the way from 0 (no LEDs on) to 15 (all LEDs on).
	 * 
	 * @param ledState the bit-map representing the desired state of the 4 LEDs
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public void setLEDs(int ledState) throws IOException, TimeoutException {
		if (ledState > 0xF || ledState < 0) throw new IOException("New LED State " + ledState + " is out of range (0..15).");

		send_receive("slm" + Integer.toString(ledState, 16));
	}

	
	/**
	 * Turns on/off the APIs "automode" functionality. When AutoMode is turned on, the reader
	 * is set to run a continuous inventory, streaming tag data to the API. The API catched this
	 * asynchronous data from the reader, parses the tag info, and accumulates it in an
	 * internal ThinkifyTaglist.
	 * <p>
	 * Automode is interrupted by any call to execute() or send_receive(), but the API
	 * will restart Automode when those interruptions are finished. You can stop AutoMode
	 * by passing false to setAutoMode(), or by directly sending data to the reader with send().
	 * 
	 * @param onOffState the new on/off state of AutoMode
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public void setAutoMode(boolean onOffState) throws IOException, TimeoutException {
		this.isAutoMode = onOffState;
		if (isAutoMode) {
			send("t6\r\n"); // don't wait for response
		} else {
			send(" ");
			receive(timeoutMillis);
		}
	}
	
	/**
	 * Returns the current on/off state of AutoMode.
	 * 
	 * @return the current on/off state of AutoMode.
	 */
	public boolean getAutoMode() {
		return isAutoMode;
	}
	
	
	/**
	 * Executes the reader's "v" command to get the firmware version, and returns
	 * the reader's version string.
	 * 
	 * @return the reader's firmware version string
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public String getVersion() throws IOException, TimeoutException {
		return execute("v");
	}
	
	/**
	 * Sends the reader's "i" command to get all of the inventory params,
	 * and returns the reader's entire response, as a String.
	 * 
	 * @return the reader's entire inventory params
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public String getInventoryParams() throws IOException, TimeoutException {
		return send_receive("i");
	}
	
	/**
	 * Performs a quick scan of tags in the field (up to 3 of them), and returns
	 * a ThinkifyTaglist containing the data from the reads.
	 * 
	 * @return a ThinkifyTaglist containing the data from the tags that were detected.
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public ThinkifyTaglist acquire() throws IOException, TimeoutException {
		String taglistString = this.send_receive("t63");
		ThinkifyTaglist taglist = new ThinkifyTaglist(taglistString);
		
		return taglist;
	}
	
	
	/**
	 * Performs a quick scan of tags in the field, and returns a ThinkifyTag with
	 * data on the strongest tag that was read (i.e. closer or better-presented
	 * to the antenna).
	 * 
	 * @return a ThinkifyTag representing the strongest/closest tag to the reader
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public ThinkifyTag find_strongest_tag() throws IOException, TimeoutException {
		ThinkifyTaglist tl = acquire();
		ThinkifyTag strongestTag = null;
		for (ThinkifyTag tag : tl) {
			if (strongestTag == null || (tag.getRSSI() > strongestTag.getRSSI())) {
				strongestTag = tag;
			}
		}
		return strongestTag;
	}

    
	/**
	 * Called by the serial library when incoming data is available.
	 * This implements the SerialPortEventListener interface.
	 */
	public void serialEvent(SerialPortEvent serialEvent) {
    	char[] lineBuffer = new char[1024];
        
        // Only handle "data available" events here
        if (serialEvent.getEventType() != SerialPortEvent.DATA_AVAILABLE) return;

        boolean doneReading = false;
        while (!doneReading) {
	        String lineStr = null;
	        int bufferPos = 0;
	        try {
	            int charRead;
	            while ((charRead=inReader.read()) >= 0) {
	            	lineBuffer[bufferPos++] = (char)charRead;
	            	if (debugLevel > 1) System.out.println("read char: " + (char)charRead);
	            	if ((charRead == '\n') ||
	            		(charRead == '>' && new String(lineBuffer, 0, bufferPos).startsWith("READY>")))
	            		break;
	            }
	            if (charRead < 0) doneReading = true; // might hit end of data this way
	        } catch (IOException e) {
	            doneReading = true; // or this way
	        }
	        
	        // Pull out whatever data we did read
	        lineStr = new String(lineBuffer, 0, bufferPos);
			if (debugLevel > 0) System.out.println("Read line: " + lineStr);
			
			// Examine lineBuffer for things that special lines from the reader
			if (lineStr.startsWith("STARTINVENTORY")) {
				// This isn't just streamed tag data, so start looking for the next prompt
		    	responseIsComplete = false;
		    	
		    	// Might also want to clear a internal taglist here, or maybe something similar
		    	
	        } else if (lineStr.startsWith("STOPINVENTORY")) {
	        	// Might want to finalize the internal taglist, or do metrics on the inventory here
	        	
	        } else if (lineStr.startsWith(PROMPT)) {
	        	// Reader's response is complete, so save the response and get ready for the next read
	        	this.readerResponse = inBuffer.toString().trim();
	        	inBuffer = new StringBuffer();
	        	
	        	// Wave the global "response is ready!" flag
	        	responseIsComplete = true;
	        } else {
				if (lineStr.startsWith("TAG=") && isAutoMode) {
		        	// Create a tag object out of it and add it to an internal taglist
					ThinkifyTag tag = new ThinkifyTag(lineStr);
					this.taglist.updateTag(tag);
				} else {
			    	// This isn't just streamed tag data, so add it to the global buffer and start looking for the next prompt
			    	responseIsComplete = false;
					inBuffer.append(lineStr);
				}
	        }
        } // end while
	}

    
    
    /**
     * Main/test routine for ThinkifyReader. Opens a connection, prints the strongest tag,
     * closes the connection, and exits. Connects to the serial port name given by
     * the first command-line argument, or defaults to "usbmodem411" if no address is given.
     * 
     * @param args the first arg can be the serial port name where the reader is connected
     */
    public static void main(String[] args) {
    	try {
    		String address = "usbmodem411";
    		if (args != null && args.length > 0) {
    			address = args[0];
    		}
    		
			ThinkifyReader reader = new ThinkifyReader(address);
			reader.debugLevel = 0;
			
			System.out.println("ThinkifyReader v." + ThinkifyReader.VERSION);
			System.out.println("====================");
			System.out.println("Opening " + address + "...");
			
			reader.open();
			
			System.out.println("\nStrongest Tag:");
			System.out.println(reader.find_strongest_tag());

			System.out.println("\nClosing");
			reader.close();
			
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
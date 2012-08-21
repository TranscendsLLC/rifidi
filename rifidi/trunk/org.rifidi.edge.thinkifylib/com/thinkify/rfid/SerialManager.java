package com.thinkify.rfid;

import javax.comm.*;
import java.io.*;
import java.util.*;


public class SerialManager {
	
	private SerialPort   serialPort;
	private InputStream  inStream;
	private OutputStream outStream;
	private String       portName;
	private int          baudRate;
	private int          serialTimeout;
	
	
	// No constructor (uses default constructor)
	
	
	/**
	 * @return the serialPort
	 */
	public SerialPort getSerialPort() {
		return serialPort;
	}
	/**
	 * @param serialPort the serialPort to set
	 */
	public void setSerialPort(SerialPort serialPort) {
		this.serialPort = serialPort;
	}


	/**
	 * @return the inStream
	 */
	public InputStream getInputStream() {
		return inStream;
	}
	/**
	 * @param inStream the inStream to set
	 */
	public void setInputStream(InputStream inStream) {
		this.inStream = inStream;
	}


	/**
	 * @return the outStream
	 */
	public OutputStream getOutputStream() {
		return outStream;
	}
	/**
	 * @param outStream the outStream to set
	 */
	public void setOutputStream(OutputStream outStream) {
		this.outStream = outStream;
	}


	/**
	 * @return the portName
	 */
	public String getPortName() {
		return portName;
	}
	/**
	 * @param portName the portName to set
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}


	/**
	 * @return the baudRate
	 */
	public int getBaudRate() {
		return baudRate;
	}
	/**
	 * @param baudRate the baudRate to set
	 */
	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}


	/**
	 * @return the serialTimeout
	 */
	public int getSerialTimeout() {
		return serialTimeout;
	}
	/**
	 * @param serialTimeout the serialTimeout to set
	 */
	public void setSerialTimeout(int serialTimeout) {
		this.serialTimeout = serialTimeout;
	}
	
	
	public void open() throws Exception {
		// Be careful, in case there's already an open connection.
		serialPort = getOpenSerialPort(portName);
		
		// Set up the serial port with the latest settings
		serialPort.setSerialPortParams(baudRate,
				                       SerialPort.DATABITS_8,
				                       SerialPort.STOPBITS_1,
				                       SerialPort.PARITY_NONE);
		// Set the read timeout (not needed here if reading asynchronously
		//serialPort.enableReceiveTimeout(serialPortTimeout);
		
		// Connect this serialPort with out gozintas and gozoutas
		setInputStream(serialPort.getInputStream());
		setOutputStream(serialPort.getOutputStream());
	}
	
	
	public void close() {
		// If there's no connection, we're done!
		if (serialPort == null) return;
		
		// Close the serial port
		try {
			if (inStream  != null) inStream.close();

			if (outStream != null) outStream.close();
			
			//System.out.println("Closing serialPort");
			serialPort.close();
		} catch (Throwable t) {}
		
		// Nullify everything
		setOutputStream(null);
		setInputStream(null);
		serialPort = null;
	}
		
	
	public SerialPort getOpenSerialPort(String address) throws IOException, PortInUseException, NoSuchPortException {
		//System.out.println("Checking port address: " + address);

		// Make sure the port address is valid
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier == null) {
			throw new IOException("Unable to get a serial portID for \"" + address + "\".");
		} else if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("The serial port, \"" + address + "\", is currently in use.");
			throw new PortInUseException();
        } else {
            SerialPort serialPort = (SerialPort)portIdentifier.open("Thinkify RFID Java Library", 1000);
            if (serialPort == null) {
            	throw new IOException("Unable to open serial connection to \"" + address + "\".");
            }
            return serialPort;
        }
	}

	

	/**
     * @return    A HashSet containing the CommPortIdentifier for all serial ports that are not currently being used.
     */
    @SuppressWarnings("unchecked")
	public static HashSet<CommPortIdentifier> getAvailableSerialPorts() {
    	boolean testConnection = false;
    	
        HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
		Enumeration<CommPortIdentifier> thePorts = CommPortIdentifier.getPortIdentifiers();
        
		while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier)thePorts.nextElement();
            switch (com.getPortType()) {
            case CommPortIdentifier.PORT_SERIAL:
            	if (testConnection) {
            		try {
            			CommPort thePort = com.open("CommUtil", 50);
	                    //System.out.println("Port, "  + com.getName() + ", LOOKS GOOD!");
	                    thePort.close();
	                    h.add(com);
	                } catch (PortInUseException e) {
	                    //System.out.println("Port, "  + com.getName() + ", is in use.");
	                } catch (Exception e) {
	                    System.err.println("Failed to open port " +  com.getName());
	                    e.printStackTrace();
	                }
            	} else {
            		h.add(com);
            	}
            }
        }
        return h;
    }
    
    
    public static void main(String[] args) {
    	System.out.println("Here are the serial ports:");
    	
    	HashSet<CommPortIdentifier> ports = SerialManager.getAvailableSerialPorts();
    	ports = SerialManager.getAvailableSerialPorts();
    	
    	for (CommPortIdentifier port : ports) {
    		System.out.println("Found port: " + port.getName());
    	}
    	System.out.println("That's All");
    	
    	String serialPort = "usbmodem411";
    	SerialManager sm = new SerialManager();
    	sm.setPortName(serialPort);
    	sm.setBaudRate(115200);
    	try {
			sm.open();
			sm.outStream.write("t\r\n".getBytes());
			Thread.sleep(5000);
			sm.close();

			sm.open();
			sm.outStream.write("t\r\n".getBytes());
			Thread.sleep(5000);
			sm.close();
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	System.exit(0);
    }
    
}
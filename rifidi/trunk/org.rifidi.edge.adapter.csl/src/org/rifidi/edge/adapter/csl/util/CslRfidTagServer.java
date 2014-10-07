/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.adapter.csl.util;

import java.io.*;
import java.lang.Thread.State;
import java.net.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/*
 *
 * @author Carlson Lam - Convergence Systems Ltd
 *
 * CS468/CS203 Low-level API demo code in Java
 * Please make sure that you firmware version  on the reader is at least:
 * TCP firmware: 2.18.11
 * RFID firmware: 1.3.99
 *
 * Here is the flow of the program
 *
 1) Open socket connection with CS203 on port 1515 (iport), 1516 (cport) and 3041 (uport)
 2) Turn on RFID board - send to cport (80000001) and wait 2 seconds
 3) Set Low Level API Mode - send to uport (80XXXXXXXX010C) - where XXXXXXXX is the ip address of the CS203 in hex
 4) Close socket with CS203
 5) Wait for 2 seconds
 6) Open socket connection with CS203 on port 1515 (iport), 1516 (cport) and 3041 (uport)
 7) Turn on RFID board - send to cport (80000001) and wait 2 seconds

 8) Enable TCP notification - send to cport (8000011701)
 9) Send AbortCmd to iport (4003000000000000)
 10) select antenna port 1 ANT_PORT_SEL - send to iport (70010107000000)
 11) Set RF power 30dBm - send to iport (700106072C010000)
 12) Set channel to iport (optional)
 13) set link profile 2 - send to iport (7001600b02000000)
 14) Send HST_CMD to iport (700100f019000000)
 15) Send ANT_CYCLES (set continuous mode) to iport (70010007ffff0000)
 16) Send QUERY_CFG to iport (7001000900000000)
 17) Set DynamicQ algorithm (INV_SEL) - send to iport (7001020901000000)
 18) Set DynamicQ values (INV_ALG_PARM_0) - send to iport (70010309f7005003)
 19) Send INV_CFG to iport (7001010901000000)
 20) start inventory - send (HST_CMD) to iport (700100f00f000000)
 21) stop inventory - send (ABORT) to iport (4003000000000000)
 22) Go back to high-level mode (80XXXXXXXX010D)
 22) Turn off RFID board - send to cport (80000002)
 23) close socket connection
 */

public class CslRfidTagServer {

	private Socket TCPCtrlSocket, TCPDataSocket = null;
	private DataOutputStream TCPCtrlOut, TCPDataOut = null;
	private DataInputStream TCPCtrlIn, TCPDataIn = null;
	private DatagramSocket clientSocket = null;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private Date date = null;
	private Queue<TagInfo> TagBuffer;

	private byte[] inData = new byte[4096];
	private int len = 0;
	private Boolean inventoryStarted = false;
	private Boolean tcpClientStarted = false;
	private Thread tcpClientThread = new Thread();
	private Thread inventoryThread = new Thread();
	private static final Log logger = LogFactory.getLog(CslRfidTagServer.class);
	
	private CslFreqTable CslFreqTab = new CslFreqTable();

	public String IPAddress;
	public int readerPower;
	public int notifyAddrPort;
	public int populaton;
	public String connString;
	public ReaderState state = ReaderState.NOT_CONNECTED;
	
	public String sel_Country;
	public int dwellTime;
	public int inventoryRound;
	public int algorithm_Q;
	public int link_Profile;
	public int startQ;

	public CslRfidTagServer(String IPAddress, int readerPower,
			int notifyAddrPort, int population, String connString, int dwellTime, String sel_Country, int inventoryRound, int algorithm_Q,
			int link_Profile, int startQ) {
		this.IPAddress = IPAddress;
		this.readerPower = readerPower;
		this.notifyAddrPort = notifyAddrPort;
		this.populaton = population;
		this.connString = connString;
		this.TagBuffer = new LinkedList<TagInfo>();
		this.sel_Country = sel_Country;
		this.dwellTime = dwellTime;
		
		this.inventoryRound = inventoryRound;
		this.algorithm_Q = algorithm_Q;
		this.link_Profile = link_Profile;
		this.startQ = startQ;
	}

	public void StartInventoryAsync() throws IOException {

		if ((inventoryThread.getState() != State.TERMINATED && inventoryThread
				.getState() != State.NEW)
				|| (tcpClientThread.getState() != State.TERMINATED && tcpClientThread
						.getState() != State.NEW)) {
			return;
		}

		Runnable task = new Runnable() {

			@Override
			public void run() {
				try {
					StartInventory();
				} catch (Exception ex) {
					// handle error which cannot be thrown back
				}
			}
		};
		Runnable task2 = new Runnable() {

			@Override
			public void run() {
				try {
					StartTCPClient();
				} catch (Exception ex) {
					// handle error which cannot be thrown back
				}
			}
		};
		tcpClientThread = new Thread(task2, "TCPClientThread");
		tcpClientThread.setDaemon(true);
		tcpClientThread.start();

		inventoryThread = new Thread(task, "InventoryThread");
		inventoryThread.setDaemon(true);
		inventoryThread.start();

		long timer = System.currentTimeMillis();
		while (System.currentTimeMillis() - timer < 10000) {
			if (state == ReaderState.BUSY)
				break;
		}
		
		if(state == ReaderState.NOT_CONNECTED)
		{
			StopInventory();
			String cmdString = String.format("Cannot connect to the reader with IP Address : %s \n", this.IPAddress);
			System.out.println(cmdString);
			logger.error(cmdString);
			throw new IOException();
		}
	}

	public boolean StopInventory() {
		long timer = System.currentTimeMillis();
		inventoryStarted = false;
		tcpClientStarted = false;
		while (System.currentTimeMillis() - timer < 10000) // time out in
															// 10seconds
		{
			if (state != ReaderState.BUSY)
				return true;
		}
		return false;
	}

	public int GetBufferCondition() {
		synchronized (TagBuffer) {
			return TagBuffer.size();
		}
	}

	private void StartTCPClient() {
		Socket tagServerSocket;
		
		synchronized (tcpClientStarted) {
			if (tcpClientStarted)
				return;
			else
				tcpClientStarted = true;
		}

		while (true) {
			try {
				String data;				

				tagServerSocket = new Socket("localhost", this.notifyAddrPort);
				DataOutputStream outToServer = new DataOutputStream(tagServerSocket.getOutputStream());

				while (true) {

					try {
						// Thread.sleep(200);
						Thread.sleep(20);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}					

					int cnt_rd=0;
					// while((cnt_rd < 100) && (TagBuffer.size() > 0))
					while((cnt_rd < 10) && (TagBuffer.size() > 0))
					{
						synchronized (TagBuffer) {						
							if (TagBuffer.size() != 0) {
								data = "";
								TagInfo tag = TagBuffer.peek();
								data += "ReaderIP:" + tag.addr;
								data += "|ID:" + tag.epc;
								data += "|Antenna:" + tag.antennaPort;
								data += "|Timestamp:" + tag.timestamp;
								data += "|PC:" + tag.pc;
								data += "|RSSI:" + tag.rssi + "\n";
							
//								logger.info(String.format("Return: %s \n", data));
//								logger.info(String.format("TagBuffer Size: %s \n", TagBuffer.size()));

								TagBuffer.remove();
								outToServer.writeBytes(data);
								System.out.println(data);
							
								cnt_rd++;
							}
						}
					}
					synchronized (tcpClientStarted) {
						if (!tcpClientStarted)
							break;
					}
				}
				tagServerSocket.close();
				tagServerSocket = null;

			} catch (UnknownHostException e) {
				tagServerSocket = null;
				logger.error("Unable to connect to port " + this.notifyAddrPort);
			} catch (IOException e) {
				// tagServerSocket = null;
				String data1 = String.format("00");
				logger.error("Unable to send tag data to server" + data1);
			}
			synchronized (tcpClientStarted) {
				if (!tcpClientStarted)
					break;
			}
		}

	}

	void StartInventory() {
		String cmdString;
		int OEMCountryCode, OEMModelCode;
		int ret_val = 0;
		
		while (true) {

			try {
				synchronized (inventoryStarted) {
					if (inventoryStarted)
						return;
					else
						inventoryStarted = true;
				}

				String IPAddress = this.IPAddress;

				// open cport (1516)
				TCPCtrlSocket = new Socket(IPAddress, 1516);
				TCPCtrlOut = new DataOutputStream(
						TCPCtrlSocket.getOutputStream());
				TCPCtrlIn = new DataInputStream(new BufferedInputStream(
						TCPCtrlSocket.getInputStream()));
				// open iport (1515)
				TCPDataSocket = new Socket(IPAddress, 1515);
				TCPDataOut = new DataOutputStream(
						TCPDataSocket.getOutputStream());
				TCPDataIn = new DataInputStream(new BufferedInputStream(
						TCPDataSocket.getInputStream()));
				// open uport (3041)
				clientSocket = new DatagramSocket();
				String returnString = "";

				// Power up RFID module
				logger.info("Power up RFID module with command 0x80000001");
				returnString = CslRfid_SendCtrlCMD("80000001", 5, 2000);
				logger.info(String.format("Return: %s \n", returnString));
				Thread.sleep(2000);

				ReaderMode mode = checkMode();
				if (mode != ReaderMode.lowLevel) {
					// change to low-level
					setMode(ReaderMode.lowLevel);
					// System.out.println("Could not change reader to low-level mode.  Operation abort");

					// reconnect
					TCPCtrlSocket.close();
					TCPDataSocket.close();
					clientSocket.close();
					Thread.sleep(2000);
					// open cport (1516)
					TCPCtrlSocket = new Socket(IPAddress, 1516);
					TCPCtrlOut = new DataOutputStream(
							TCPCtrlSocket.getOutputStream());
					TCPCtrlIn = new DataInputStream(new BufferedInputStream(
							TCPCtrlSocket.getInputStream()));
					// open iport (1515)
					TCPDataSocket = new Socket(IPAddress, 1515);
					TCPDataOut = new DataOutputStream(
							TCPDataSocket.getOutputStream());
					TCPDataIn = new DataInputStream(new BufferedInputStream(
							TCPDataSocket.getInputStream()));
					// open uport (3041)
					clientSocket = new DatagramSocket();

					logger.info("Power up RFID module with command 0x80000001");
					returnString = CslRfid_SendCtrlCMD("80000001", 5, 2000);
					logger.info(String.format("Return: %s \n", returnString));
					mode = checkMode();
					if (mode != ReaderMode.lowLevel) {
						TCPCtrlSocket.close();
						TCPDataSocket.close();
						clientSocket.close();
						Thread.sleep(2000);
						inventoryStarted = false;
						return;
					}
					Thread.sleep(2000);
				}

				// Enable TCP Notifications
				logger.info("Enable TCP Notifications with command 0x8000011701");
				returnString = CslRfid_SendCtrlCMD("8000011701", 5, 2000);
				logger.info(String.format("Return: %s \n", returnString));
				Thread.sleep(500);

				// Send Abort command
				clearReadBuffer(TCPDataIn);
				logger.info("Send Abort command 0x4003000000000000");
				returnString = CslRfid_SendDataCMD("4003000000000000", 8, 2000);
				logger.info(String.format("Return: %s \n", returnString));

				this.state = ReaderState.CONNECTED;

				// Select Antenna port 0 ANT_PORT_SEL
				clearReadBuffer(TCPDataIn);
				TCPDataOut.write(hexStringToByteArray("7001010700000000"));
				logger.info("Send ANT_PORT_SEL command 0x7001010700000000");

				Thread.sleep(5);
				// Select RF power Max up to 30dBm
				clearReadBuffer(TCPDataIn);
				if((readerPower < 0) || (readerPower > 300))
					ret_val = -11;
				else
				{
					// TCPDataOut.write(hexStringToByteArray("700106072C010000"));
					cmdString = String.format("70010607%02X%02X0000", readerPower & 0xFF, ((readerPower >> 8) & 0xFF));
					TCPDataOut.write(hexStringToByteArray(cmdString));
					logger.info(String.format("Send RF Power command %s \n", cmdString));
				}

				Thread.sleep(5);
				// Set Dwell Time
				clearReadBuffer(TCPDataIn);
				// dwellTime = 0 for CS203 reader
				cmdString = String.format("70010507%02X%02X%02X%02X", dwellTime & 0xff, (dwellTime >> 8) & 0xff, (dwellTime >> 16) & 0xff, (dwellTime >> 24) & 0xff);
				TCPDataOut.write(hexStringToByteArray(cmdString));
				logger.info(String.format("Send Dwell Time command %s \n", cmdString));
				
				Thread.sleep(5);
				// Set Inventory Round  (ANT_PORT_INV_CNT)
				clearReadBuffer(TCPDataIn);
				// inventoryRound = 0xffffffff for CS203 reader
				inventoryRound = 0xffffffff;
				cmdString = String.format("70010707%02X%02X%02X%02X", inventoryRound & 0xff, (inventoryRound >> 8) & 0xff, (inventoryRound >> 16) & 0xff, (inventoryRound >> 24) & 0xff);
				TCPDataOut.write(hexStringToByteArray(cmdString));
				logger.info(String.format("Send Inventory Round command %s \n", cmdString));

				
				Thread.sleep(5);
				// Link Profile
				clearReadBuffer(TCPDataIn);
				if((link_Profile < 0) || (link_Profile > 4))
					ret_val = -12;
				else
				{
					// TCPDataOut.write(hexStringToByteArray("7001600b02000000"));
					// link_Profile = 2;  		// Link Profile
					cmdString = String.format("7001600b%02X000000", link_Profile & 0xff);
					TCPDataOut.write(hexStringToByteArray(cmdString));
					logger.info(String.format("Send Link Profile 2 command %s \n", cmdString));
				}
				
				Thread.sleep(5);
				// HST Command
				clearReadBuffer(TCPDataIn);
				TCPDataOut.write(hexStringToByteArray("700100f019000000"));
				logger.info("HST_CMD command 700100f019000000");

				Thread.sleep(5);
				// QUERY_CFG Command for continuous inventory
				clearReadBuffer(TCPDataIn);
				TCPDataOut.write(hexStringToByteArray("70010007ffff0000"));
				logger.info("QUERY_CFG (continuous mode) command 70010007ffff0000");

				Thread.sleep(5);
				// Set DynamicQ algorithm (INV_SEL)
				clearReadBuffer(TCPDataIn);
				// TCPDataOut.write(hexStringToByteArray("70010309f7005003"));
				if((algorithm_Q < 0) || (algorithm_Q > 3))
					ret_val = -14;
				else
				{
					cmdString = String.format("70010209%02X000000", algorithm_Q & 0xff);
					TCPDataOut.write(hexStringToByteArray(cmdString));
					logger.info(String.format("Send Q Algorithm command %s \n", cmdString));
				}
				
				Thread.sleep(5);
				// start Q
				clearReadBuffer(TCPDataIn);
				if((startQ < 0) || (startQ > 15))
					ret_val = -15;
				else
				{
					cmdString = String.format("70010309F%01X400000", startQ & 0xf);
					TCPDataOut.write(hexStringToByteArray(cmdString));
					logger.info(String.format("Send Start Q command %s \n", cmdString));
				}
				
				Thread.sleep(5);
				// Send INV_CFG
				clearReadBuffer(TCPDataIn);
				TCPDataOut.write(hexStringToByteArray("7001010901000000"));
				logger.info("Send INV_CFG command 7001010901000000");
				
				
				Thread.sleep(5);
                //---------------------------------------- Read Country code
				clearReadBuffer(TCPDataIn);
				TCPDataOut.write(hexStringToByteArray("7001000502000000"));
                Thread.sleep(5);
                TCPDataOut.write(hexStringToByteArray("700100f003000000"));
                Thread.sleep(5);
                while(true)
                {
                    if(TCPDataIn.available() != 0)
                    {
                        len=TCPDataIn.read(inData);
                        break;
                    }
                }
                OEMCountryCode = inData[28];

				Thread.sleep(5);
				//---------------------------------------- Read Model code
				clearReadBuffer(TCPDataIn);
				TCPDataOut.write(hexStringToByteArray("70010005A4000000"));
                Thread.sleep(5);
                TCPDataOut.write(hexStringToByteArray("700100f003000000"));
                Thread.sleep(5);
                while(true)
                {
                    if(TCPDataIn.available() != 0)
                    {
                        len=TCPDataIn.read(inData);
                        break;
                    }
                }
                OEMModelCode = inData[28];
                 
                Thread.sleep(5);
                switch (OEMModelCode)
                {
                	case 0:
                		cmdString = String.format("Reader Model: CS101-%d \n", OEMCountryCode);
                		break;
                	case 1:
                		cmdString = String.format("Reader Model: CS203-%d \n", OEMCountryCode);
                		break;
                	case 3:
                		cmdString = String.format("Reader Model: CS468-%d \n", OEMCountryCode);
                		break;
                	case 5:
                		cmdString = String.format("Reader Model: CS468INT-%d \n", OEMCountryCode);
                		break;
                	case 7:
                		cmdString = String.format("Reader Model: CS469-%d \n", OEMCountryCode);
                		break;
                	default:
                		cmdString = String.format("Unidentified reader model \n");
                		ret_val = -17;
                		break;
                }
                System.out.println(cmdString);

                
                // sel_Country = "G800";
                int t_index = 0;
                // String cty = CslFreqTab.OEMCountryTable[1];
				if(ret_val == 0)
				{
	           //---------------------------------------------------  Set Frequency Table for the Country Select
					ret_val = VerifySelectCountry(sel_Country, OEMCountryCode);
					if(ret_val == 0)
					{
						t_index = Arrays.asList(CslFreqTab.OEMCountryTable).indexOf(sel_Country);

						int[] t_freqTable = CslFreqTab.countryFreqTable[t_index][0];
						int t_numCh = CslFreqTab.countryFreqTable[t_index][1][0];
						int t_freqVal;

						for(int i=0; i<t_numCh; i++)
						{
							Thread.sleep(5);
							//---------------------------------------- Read Model code
							clearReadBuffer(TCPDataIn);
							cmdString = String.format("7001010C%02X000000", i); 		// Select Channel
							TCPDataOut.write(hexStringToByteArray(cmdString));
			                Thread.sleep(5);
			                
			                t_freqVal = swapMSBLSB32bit(t_freqTable[i]);
							cmdString = String.format("7001030C%08X", t_freqVal); 		// Set Freq
			                TCPDataOut.write(hexStringToByteArray(cmdString));
			                Thread.sleep(5);
			                TCPDataOut.write(hexStringToByteArray("7001020C01000000")); // Enable Channel
			                Thread.sleep(5);
						}
			
					}
					else
					{
						cmdString = String.format("The reader -%d does not support selected country!! \n", OEMCountryCode);
						System.out.println(cmdString);
					}					
				}

				
				//-----------------------------------------  quit if error value returned
				boolean sentAbortCmd = false;
				if(ret_val < 0)
				{
					inventoryStarted = false;
					tcpClientStarted = false;
					TCPCtrlSocket.close();
					TCPDataSocket.close();
					clientSocket.close();
					Thread.sleep(1000);
					sentAbortCmd = true;
				}
				else
				{
					// Start inventory - send (HST_CMD)
					long timer = System.currentTimeMillis();
					clearReadBuffer(TCPDataIn);
					TCPDataOut.write(hexStringToByteArray("700100f00f000000"));
					logger.info("Start inventory - send (HST_CMD) 700100f00f000000");
				
					while (true) {
						if (!inventoryStarted && !sentAbortCmd) {
							// Send Abort command
							clearReadBuffer(TCPDataIn);
							TCPDataOut.write(hexStringToByteArray("4003000000000000"));
							logger.info("Send Abort command 0x4003000000000000");
							sentAbortCmd = true;
						}

						if (TCPDataIn.available() != 0) {
							timer = System.currentTimeMillis();
							len = TCPDataIn.read(inData, 0, 8);

							if (len < 8)
								continue;

							if (byteArrayToHexString(inData, len).startsWith(
								"9898989898989898")) {
								// clearReadBuffer(TCPDataIn);
								// date = new Date();
								// System.out.println(dateFormat.format(date) +
								// " TCP Notification Received.");
								continue;
							}

							if (byteArrayToHexString(inData, len).startsWith(
								"02000780")) {
								// clearReadBuffer(TCPDataIn);
								// /date = new Date();
								// System.out.println(dateFormat.format(date) +
								// " Antenna Cycle End Notification Received");
								continue;
							}

							if (byteArrayToHexString(inData, len).startsWith(
								"4003BFFCBFFCBFFC")) {
								TCPCtrlSocket.close();
								TCPDataSocket.close();
								clientSocket.close();
								date = new Date();
								logger.info("Abort cmd response received "
									+ byteArrayToHexString(inData, len));
								Thread.sleep(2000);
								inventoryStarted = false;
								break;
							}

							// int pkt_ver = (int) inData[0];
							// int flags = (int) inData[1];
							int pkt_type = (int) (inData[2] & 0xFF)
								+ ((int) (inData[3] & 0xFF) << 8);
							int pkt_len = (int) (inData[4] & 0xFF)
								+ ((int) (inData[5] & 0xFF) << 8);
							int datalen = pkt_len * 4;

							// wait until the full packet data has come in
							while (TCPDataIn.available() < datalen) {
							}
							// finish reading
							TCPDataIn.read(inData, 8, datalen);

							if (pkt_type == 0x8001) {
								TCPCtrlSocket.close();
								TCPDataSocket.close();
								clientSocket.close();
								this.state = ReaderState.NOT_CONNECTED;
								date = new Date();
								logger.info("Command End Packet: "
										+ byteArrayToHexString(inData, len
											+ datalen));
								Thread.sleep(2000);
								break;
							}
							if (pkt_type == 0x8000) {
								this.state = ReaderState.BUSY;
								date = new Date();
								logger.info(dateFormat.format(date)
									+ " Command Begin Packet: "
									+ byteArrayToHexString(inData, len
									+ datalen));
								continue;
							}
							if (pkt_type == 0x8005) {
								date = new Date();
								// System.out.println(dateFormat.format(date) +
								// " Inventory Packet: " +
								// byteArrayToHexString(inData,len+datalen));
							
								// logger.info("pkt_type == 0x8005 : " + dateFormat.format(date));    // For Test Only
							
								byte[] EPC = new byte[1000];
								TagInfo tag = new TagInfo();

								tag.pc = byteArrayToHexString(Arrays.copyOfRange(inData, 20, 22), 2);

								tag.rssi = (float) (inData[13] * 0.8);
								tag.antennaPort = inData[18];
								for (int cnt = 0; cnt < (datalen - 16); cnt++) {
									EPC[cnt] = inData[22 + cnt];
								}
								tag.addr = this.IPAddress;
								tag.epc = byteArrayToHexString(EPC, datalen - 16);
								tag.timestamp = System.currentTimeMillis();

								synchronized (TagBuffer) {
									if (TagBuffer.size() >= 10000)
										TagBuffer.remove();
									TagBuffer.add(tag);
								}
							}
						
						} else {						
							if (System.currentTimeMillis() - timer >= 8000) {													
							
								this.state = ReaderState.NOT_CONNECTED;
								logger.error("Connection lost.  To be reconnected");
								logger.error("Close Connections");

								TCPCtrlSocket.close();
								TCPDataSocket.close();
								clientSocket.close();
								Thread.sleep(2000);
								inventoryStarted = false;
								break;
							}
						}
					}
				}

				if (sentAbortCmd) {
					// exit thread
					logger.info("Inventory Stopped");
					this.state = ReaderState.NOT_CONNECTED;
					inventoryStarted = false;
					break;
				}
			} catch (UnknownHostException e) {
				System.err.println(e.getMessage());
				inventoryStarted = false;
			} catch (IOException e) {
				System.err.println(e.getMessage());
				inventoryStarted = false;
			} catch (java.lang.InterruptedException e) {
				System.err.println(e.getMessage());
				inventoryStarted = false;
			} catch (java.lang.IndexOutOfBoundsException e) {
				System.err.println(e.getMessage());
				inventoryStarted = false;
			}
		}
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String charArrayToHexString(char[] a, int length) {
		String returnString = "";
		for (int j = 0; j < length; j++) {
			byte c = (byte) a[j];
			int uc = (int) (c & 0xFF);
			if (Integer.toHexString(uc).length() == 1)
				returnString += "0";
			returnString += Integer.toHexString(uc);
		}
		returnString = returnString.toUpperCase();
		return returnString;
	}

	public static String byteArrayToHexString(byte[] a, int length) {
		String returnString = "";
		for (int j = 0; j < length; j++) {
			int uc = (int) (a[j] & 0xFF);
			if (Integer.toHexString(uc).length() == 1)
				returnString += "0";
			returnString += Integer.toHexString(uc);
		}
		returnString = returnString.toUpperCase();
		return returnString;
	}

	public static void clearReadBuffer(DataInputStream data) {
		byte[] inData = new byte[1024];
		try {
			while (data.available() != 0)
				data.read(inData);
		} catch (IOException e) {
			logger.error("Could not clear buffer: " + e.getMessage());
		}
	}

	private String CslRfid_SendDataCMD(String SendBuf, int RecvSize,
			int RecvTimeOut) throws IOException {
		String RecvString = "";
		int len = 0;
		try {
			TCPDataOut.write(hexStringToByteArray(SendBuf));
			if (RecvSize == 0)
				return RecvString;
			long timer = System.currentTimeMillis();
			while (System.currentTimeMillis() - timer < RecvTimeOut) {
				if (TCPDataIn.available() != 0) {
					len += TCPDataIn.read(inData, len, RecvSize - len);
					if (len >= RecvSize)
						break;
				}
			}
			RecvString = byteArrayToHexString(inData, len);
		} catch (IOException e) {
			logger.info("Error writing/reading TCP data socket");
		}
		return RecvString;

	}

	private String CslRfid_SendCtrlCMD(String SendBuf, int RecvSize,
			int RecvTimeOut) throws IOException {
		String RecvString = "";
		int len = 0;
		try {
			TCPCtrlOut.write(hexStringToByteArray(SendBuf));
			if (RecvSize == 0)
				return RecvString;
			long timer = System.currentTimeMillis();
			while (System.currentTimeMillis() - timer < RecvTimeOut) {
				if (TCPCtrlIn.available() != 0) {
					len += TCPCtrlIn.read(inData, len, RecvSize - len);
					if (len >= RecvSize)
						break;
				}
			}
			RecvString = byteArrayToHexString(inData, len);
		} catch (IOException e) {
			logger.info("Error writing/reading TCP data socket");
		}
		return RecvString;

	}

	private boolean setMode(ReaderMode mode) {
		try {

			InetAddress IPInet = InetAddress.getByName(this.IPAddress);

			// Set low-level API mode
			String cmd = "80"
					+ byteArrayToHexString(IPInet.getAddress(),
							IPInet.getAddress().length)
					+ (mode == ReaderMode.lowLevel ? "000C" : "000D");
			logger.info("Set low-level API with command 0x" + cmd.toString());

			DatagramPacket sendPacket = new DatagramPacket(
					hexStringToByteArray(cmd),
					hexStringToByteArray(cmd).length, IPInet, 3041);
			byte[] receiveData = new byte[4];
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			clientSocket.send(sendPacket);
			clientSocket.setSoTimeout(2000);
			clientSocket.receive(receivePacket);
			logger.info("Return: "
					+ byteArrayToHexString(receivePacket.getData(),
							receivePacket.getData().length));

			if (byteArrayToHexString(receivePacket.getData(),
					receivePacket.getData().length).startsWith("810100"))
				return true;
			else
				return false;
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	private ReaderMode checkMode() {
		try {

			InetAddress IPInet = InetAddress.getByName(this.IPAddress);

			// Set low-level API mode
			String cmd = "80"
					+ byteArrayToHexString(IPInet.getAddress(),
							IPInet.getAddress().length) + "000E";
			logger.info("Check mode with command 0x" + cmd.toString());

			DatagramPacket sendPacket = new DatagramPacket(
					hexStringToByteArray(cmd),
					hexStringToByteArray(cmd).length, IPInet, 3041);
			byte[] receiveData = new byte[5];
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			clientSocket.send(sendPacket);
			clientSocket.setSoTimeout(2000);
			clientSocket.receive(receivePacket);
			logger.info(String.format("Return: "
					+ byteArrayToHexString(receivePacket.getData(),
							receivePacket.getData().length)));

			if (byteArrayToHexString(receivePacket.getData(),
					receivePacket.getData().length).startsWith("8101010E00"))
				return ReaderMode.highLevel;
			else if (byteArrayToHexString(receivePacket.getData(),
					receivePacket.getData().length).startsWith("8101010E01"))
				return ReaderMode.lowLevel;
			else
				return null;
		} catch (UnknownHostException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}


	
	private int VerifySelectCountry (String sel_Country, int OEMCountryCode)
	{
		List<String> OEMCountryCode_1 = Arrays.asList("ETSI", "IN", "G800");
		List<String> OEMCountryCode_2 = Arrays.asList("AU", "BR1", "BR2", "FCC", "HK", "SG", "MY", "ZA", "TH", "ID");
		List<String> OEMCountryCode_4 = Arrays.asList("AU", "MY", "HK", "SG", "TW", "ID", "CN", "CN1", "CN2", "CN3", "CN4", "CN5", "CN6", "CN7", "CN8", "CN9", "CN10", "CN11", "CN12");
		List<String> OEMCountryCode_7 = Arrays.asList("AU", "HK", "TH", "SG", "CN", "CN1", "CN2", "CN3", "CN4", "CN5", "CN6", "CN7", "CN8", "CN9", "CN10", "CN11", "CN12");
		List<String> OEMCountryCode_8 = Arrays.asList("JP");
		int ret_val = 0;		
		
		switch(OEMCountryCode)
		{
			case 1:
				if(!(OEMCountryCode_1.contains(sel_Country)))
					ret_val = -18;
				break;
			case 2:
				if(!(OEMCountryCode_2.contains(sel_Country)))
					ret_val = -18;
				break;
			case 4:
				if(!(OEMCountryCode_4.contains(sel_Country)))
					ret_val = -18;
				break;
			case 7:
				if(!(OEMCountryCode_7.contains(sel_Country)))
					ret_val = -18;
				break;
			case 8:
				if(!(OEMCountryCode_8.contains(sel_Country)))
					ret_val = -18;
				break;
			default:
				break;		
		}
					
		return ret_val;
	}

	
	private int swapMSBLSB32bit(int in_32bit)
	{
		int[] t_shift = new int[] {0,8,16,24};
		int[] t_tmpVal = new int[4];
		int out_32bit;
		int j;
		
			out_32bit = 0;
			for(j=0; j<4; j++)
			{
				t_tmpVal[j] = (in_32bit>>t_shift[j]) & 0xff;
				out_32bit |= t_tmpVal[j]<<t_shift[3-j];
			}

		return out_32bit;
	}	


}






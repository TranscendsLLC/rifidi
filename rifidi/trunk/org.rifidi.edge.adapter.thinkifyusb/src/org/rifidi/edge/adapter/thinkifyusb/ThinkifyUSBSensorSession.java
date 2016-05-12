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
package org.rifidi.edge.adapter.thinkifyusb;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.notification.ReadCycle;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.sessions.AbstractSensorSession;

import com.thinkify.rfid.ThinkifyReader;
import com.thinkify.rfid.ThinkifyTag;

/**
 * Reader session for the Thinkify 50 reader
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class ThinkifyUSBSensorSession extends AbstractSensorSession {

	private static final Log logger = LogFactory.getLog(ThinkifyUSBSensorSession.class);

	int maxConAttempts = -1;
	int reconnectionInterval = 2500;

	private String readerID;
	private String port;

	private int interval;
	private int ag, ra, q, p, fl, fh;
	private boolean rcs;
	private String ma;

	ReadThread readthread = null;
	ConnectedCheckerThread connectthread = null;

	private ThinkifyReader reader;

	/** atomic boolean that is true if we are inside the connection attempt loop */
	private AtomicBoolean connectingLoop = new AtomicBoolean(false);

	public ThinkifyUSBSensorSession(AbstractSensor<?> sensor, String ID,
			NotifierService notifierService, String readerID, String port,
			Integer reconnectionInterval, Integer maxConnectionAttempts,
			Set<AbstractCommandConfiguration<?>> commandConfigurations,
			int readrate, int ra, String ma, int ag, int q, int p, int fl, int fh, boolean rcs) {
		super(sensor, ID, commandConfigurations);
		this.readerID = readerID;
		this.port = port;
		this.maxConAttempts = maxConnectionAttempts;
		this.reconnectionInterval = reconnectionInterval;
		this.interval = readrate;
		this.ag = ag;
		this.ra = ra;
		this.ma = ma;
		this.q = q;
		this.p = p;
		this.fl = fl;
		this.rcs = rcs;
		this.fh = fh;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorSession#_connect()
	 */
	@Override
	protected void _connect() throws IOException {
		// TODO Auto-generated method stub


		logger.info("Setting the port: " + this.port);
		System.setProperty("gnu.io.rxtx.SerialPorts", this.port);
		
		boolean connected = false;
		this.setStatus(SessionStatus.CONNECTING);
		this.connectingLoop.set(true);
		try {
			for (int connCount = 0; connCount < maxConAttempts && !connected
					|| maxConAttempts == -1; connCount++) {

				try {
					reader = new ThinkifyReader(this.port);
					reader.debugLevel = 0;
					reader.open();
					logger.info("Thinkify firmware version: " + reader.getVersion()); // some commands may have methods provided for you, like getVersion()
					connected = true;
					logger.info("Connected to reader " + this.readerID
							+ " at port " + this.port);
				} catch (Exception e) {
					logger.info("Exception while trying to connect to reader "
							+ this.readerID + " at port " + this.port + ": "
							+ e.getMessage());
				}

				try {
					synchronized (connectingLoop) {
						connectingLoop.wait(reconnectionInterval);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
				// if someone else wants us to stop, break from the for loop
				if (!connectingLoop.get()) {
					break;
				}

			}
		} finally {
			// make sure connecting loop is false!
			connectingLoop.set(false);
		}
		// if not connected, exit
		if (!connected) {
			setStatus(SessionStatus.CLOSED);
			throw new IOException("Cannot connect");
		}
		onConnect();
	}

	/**
	 * 
	 */
	public void startReading() {
		try {
			logger.info(this.reader.send_receive("ra" + ra));
			logger.info(this.reader.send_receive("ag"+ag));
			logger.info(this.reader.send_receive("q"+q));
			logger.info(this.reader.send_receive("p"+p));
			logger.info(this.reader.send_receive("fl"+fl));
			logger.info(this.reader.send_receive("fh"+fh));
			if (this.ma != "") {
				this.reader.send_receive("ma" + ma);
			}
			if(this.rcs) {
				//This will take a long time if this is set
				logger.info(this.reader.send_receive("rcs"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		readthread = new ReadThread(reader, new ThinkifyUSBTagHandler(this,
				this.readerID), this, interval);
		Thread thread = new Thread(readthread);
		thread.start();
//		connectthread = new ConnectedCheckerThread(reader);
//		Thread cThread = new Thread(connectthread);
//		cThread.start();
	}

	/**
	 * 
	 */
	public void stopReading() {
		readthread.stop = true;
		connectthread.stop = true;
	}

	/**
	 * 
	 */
	private void onConnect() {
		setStatus(SessionStatus.PROCESSING);

		startReading();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorSession#disconnect()
	 */
	@Override
	public void disconnect() {
		logger.info("Disconnecting from the reader: " + this.readerID);
		stopReading();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		reader.close();
		logger.info("Finished disconnecting from the reader");
		setStatus(SessionStatus.CLOSED);
	}

	public String getReaderID() {
		return readerID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ThinkifyUSBSession: " + port + " (" + getStatus() + ")";
	}

	public class ReadThread implements Runnable {

		public volatile boolean stop = false;

		private ThinkifyUSBTagHandler handler;

		private ThinkifyReader reader;

		private ThinkifyUSBSensorSession session;

		private int interval;

		public ReadThread(ThinkifyReader reader, ThinkifyUSBTagHandler handler,
				ThinkifyUSBSensorSession session, int interval) {
			this.handler = handler;
			this.reader = reader;
			this.session = session;
			this.interval = interval;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#start()
		 */
		@Override
		public void run() {
			try {
				this.reader.setAutoMode(true);
				
				stop = false;
				while (!stop) {
					try {
						try {
						Set<TagReadEvent> taglist = new HashSet<TagReadEvent>();
						for (ThinkifyTag aTag : reader.taglist) {
							taglist.add(handler.tagArrived(aTag.getEpc(),
									aTag.getLastSeenTime(), aTag.getRSSI(),
									aTag.getReadCount()));
						}
						ReadCycle cycle = new ReadCycle(taglist, readerID,
								System.currentTimeMillis());
						this.session.getSensor().send(cycle);
						reader.taglist.clear();
						Thread.sleep(interval);
						} catch(ConcurrentModificationException e) {
							logger.error("ConcurrentModificationException encountered");
						}
					} catch (InterruptedException e) {
					}
				}

				this.reader.setAutoMode(false);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
		}

		public void stopReading() {
			this.stop = true;
		}
	}
	
	public class ConnectedCheckerThread implements Runnable {

		public volatile boolean stop = false;
		
		private ThinkifyReader reader;

		public ConnectedCheckerThread(ThinkifyReader reader) {
			this.reader = reader;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#start()
		 */
		@Override
		public void run() {
			stop = false;
			while (!stop) {
				try {
					this.reader.getVersion();
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("Reader port is closed");
					disconnect();
					logger.info("Reader is disconnected " + readerID);
					stop = true;
					try {
						Thread.sleep(2000);
						connect();
					} catch (InterruptedException e1) {
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
			}
		}

		public void stopReading() {
			this.stop = true;
		}
	}
}

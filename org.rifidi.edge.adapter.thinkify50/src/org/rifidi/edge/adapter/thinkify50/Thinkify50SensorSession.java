/**
 * 
 */
package org.rifidi.edge.adapter.thinkify50;

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
public class Thinkify50SensorSession extends AbstractSensorSession {

	private static final Log logger = LogFactory
			.getLog(Thinkify50SensorSession.class);

	int maxConAttempts = -1;
	int reconnectionInterval = 2500;

	private String readerID;
	private String port;

	private int interval;
	private int ra;
	private String ma;

	ReadThread readthread = null;

	private ThinkifyReader reader;

	/** atomic boolean that is true if we are inside the connection attempt loop */
	private AtomicBoolean connectingLoop = new AtomicBoolean(false);

	public Thinkify50SensorSession(AbstractSensor<?> sensor, String ID,
			NotifierService notifierService, String readerID, String port,
			Integer reconnectionInterval, Integer maxConnectionAttempts,
			Set<AbstractCommandConfiguration<?>> commandConfigurations,
			int readrate, int ra, String ma) {
		super(sensor, ID, commandConfigurations);
		this.readerID = readerID;
		this.port = port;
		this.maxConAttempts = maxConnectionAttempts;
		this.reconnectionInterval = reconnectionInterval;
		this.interval = readrate;
		this.ra = ra;
		this.ma = ma;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorSession#_connect()
	 */
	@Override
	protected void _connect() throws IOException {
		// TODO Auto-generated method stub
		boolean connected = false;
		this.setStatus(SessionStatus.CONNECTING);
		this.connectingLoop.set(true);
		try {
			for (int connCount = 0; connCount < maxConAttempts && !connected
					|| maxConAttempts == -1; connCount++) {

				try {
					reader = new ThinkifyReader(port);
					reader.debugLevel = 0;
					reader.open();
					connected = true;
					logger.info("Connected to reader " + this.readerID
							+ " at port " + this.port);
				} catch (Exception e) {
					logger.info("Exception while trying to connect to reader "
							+ this.readerID + " at port " + this.port + ": "
							+ e.getLocalizedMessage());
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
			System.out.println(this.reader.send_receive("ra" + ra));
			if (this.ma != "") {
				this.reader.send_receive("ma" + ma);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		readthread = new ReadThread(reader, new Thinkify50TagHandler(this,
				this.readerID), this, interval);
		Thread thread = new Thread(readthread);
		thread.start();
	}

	/**
	 * 
	 */
	public void stopReading() {
		readthread.stop = true;
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
		stopReading();
		reader.close();
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
		return "Thinkify50Session: " + port + " (" + getStatus() + ")";
	}

	public class ReadThread extends Thread {

		public volatile boolean stop = false;

		private Thinkify50TagHandler handler;

		private ThinkifyReader reader;

		private Thinkify50SensorSession session;

		private int interval;

		public ReadThread(ThinkifyReader reader, Thinkify50TagHandler handler,
				Thinkify50SensorSession session, int interval) {
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
}

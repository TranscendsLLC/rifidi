/**
 * 
 */
package org.rifidi.edge.adapter.thingmagic6;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.notification.ReadCycle;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.sessions.AbstractSensorSession;

import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.SimpleReadPlan;
import com.thingmagic.TagProtocol;
import com.thingmagic.Reader.Region;

/**
 * 
 * 
 * @author Matthew Dean (mfd7124@rifidi.org)
 */
public class Thingmagic6SensorSession extends AbstractSensorSession {

	public Reader reader;

	int maxConAttempts = -1;
	int reconnectionInterval = 2500;

	private String readerID;
	private String port;
	private int[] antennas;

	private Thingmagic6TagHandler handler = null;
	private Thingmagic6ExceptionHandler exceptionhandler = null;

	/** atomic boolean that is true if we are inside the connection attempt loop */
	private AtomicBoolean connectingLoop = new AtomicBoolean(false);

	/**
	 * 
	 * 
	 * @param sensor
	 * @param ID
	 * @param commandConfigurations
	 */
	public Thingmagic6SensorSession(AbstractSensor<?> sensor, String ID,
			NotifierService notifierService, String readerID, String port,
			int reconnectionInterval, int maxConAttempts, int[] antennas,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, ID, commandConfigurations);
		this.readerID = readerID;
		this.port = port;
		this.antennas = antennas;
	}

	/**
	 * 
	 */
	public void startReading() {
		// reader.addReadListener(new Thingmagic6TagHandler(this));
		// System.out.println("Startreading called!  ");
		reader.startReading();
	}

	public void stopReading() {
		try {
			reader.stopReading();
		} catch (InterruptedException e) {
			// FIXME: Log this
		}
	}

	/**
	 * 
	 */
	public void sendReadCycle(ReadCycle cycle) {
		sensor.send(cycle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorSession#_connect()
	 */
	@Override
	protected void _connect() throws IOException {
		// Connected flag
		boolean connected = false;
		// TODO Auto-generated method stub
		this.setStatus(SessionStatus.CONNECTING);
		System.out.println("Connecting");
		connectingLoop.set(true);
		try {
			for (int connCount = 0; connCount < maxConAttempts
					|| maxConAttempts == -1; connCount++) {
				try {
					System.out.println("Trying to connect");
					reader = Reader.create(port);
					System.out.println("Got past the reader create");
					reader.connect();
					System.out.println("Past the connect");
					connected = true;
					break;
				} catch (ReaderException e) {
					e.printStackTrace();
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
	private void onConnect() {
		setStatus(SessionStatus.PROCESSING);
		this.handler = new Thingmagic6TagHandler(this);
		this.exceptionhandler = new Thingmagic6ExceptionHandler(this);
		reader.addReadListener(handler);
		reader.addReadExceptionListener(exceptionhandler);
		SimpleReadPlan srp = new SimpleReadPlan();
		if (this.antennas != null) {
			srp.antennas = this.antennas;
		}
		srp.protocol = TagProtocol.GEN2;

		try {
			reader.paramSet("/reader/read/plan", srp);
			reader.paramSet("/reader/region/id", Region.NA);
		} catch (ReaderException e) {
			e.printStackTrace();
		}
		try {
			System.out.println("Read plan: "
					+ reader.paramGet("/reader/read/plan"));
		} catch (ReaderException e) {
			e.printStackTrace();
		}

		this.startReading();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorSession#disconnect()
	 */
	@Override
	public void disconnect() {
		reader.destroy();
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
		return "Thingmagic6Session: " + port + " (" + getStatus() + ")";
	}

}

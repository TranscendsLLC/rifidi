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

/**
 * 
 * 
 * @author Matthew Dean (mfd7124@rifidi.org)
 */
public class Thingmagic6SensorSession extends AbstractSensorSession {

	public Reader reader;

	int maxConAttempts = -1;
	int reconnectionInterval = -1;
	
	public String readerID;

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
			int reconnectionInterval, int maxConAttempts,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, ID, commandConfigurations);
		this.readerID = readerID;
	}
	
	/**
	 * 
	 */
	public void startReading() {
		reader.startReading();
		reader.addReadListener(new Thingmagic6TagHandler(this));
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
		connectingLoop.set(true);
		try {
			for (int connCount = 0; connCount < maxConAttempts
					|| maxConAttempts == -1; connCount++) {
				try {
					reader = Reader.create("tmr:///dev/ttyACM0");
					reader.connect();
					connected = true;
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

}

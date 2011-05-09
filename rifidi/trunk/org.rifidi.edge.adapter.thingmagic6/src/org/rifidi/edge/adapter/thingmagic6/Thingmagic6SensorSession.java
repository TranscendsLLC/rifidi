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
	int reconnectionInterval = 500;

	public String readerID;
	public String port;

	public Thingmagic6TagHandler handler = null;

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
		this.port = port;
	}

	/**
	 * 
	 */
	public void startReading() {
		//reader.addReadListener(new Thingmagic6TagHandler(this));
		reader.startReading();
	}
	
	public void stopReading() {
		try {
			reader.stopReading();
		} catch (InterruptedException e) {
			//FIXME: Log this
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
					reader = Reader.create("tmr:///dev/ttyACM5");
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
			System.out.println("Can't connect");
			throw new IOException("Cannot connect");
		}

		onConnect();
	}

	/**
	 * 
	 */
	private void onConnect() {
		System.out.println("Processing!");
		setStatus(SessionStatus.PROCESSING);
		this.handler = new Thingmagic6TagHandler(this);
		reader.addReadListener(handler);
		SimpleReadPlan srp = new SimpleReadPlan();
		srp.antennas = new int[] { 1, 2, 3, 4 };
		srp.protocol = TagProtocol.GEN2;

		try {
			reader.paramSet("/reader/read/plan", srp);
			reader.paramSet("/reader/region/id", Region.NA);
		} catch (ReaderException e) {
			e.printStackTrace();
		}
		try {
			printArray((int[]) reader
					.paramGet("/reader/antenna/connectedPortList"));
			// printArray((int[]) reader.paramGet("/reader/antenna/portList"));
			System.out.println("Check ports: "
					+ reader.paramGet("/reader/antenna/checkPort"));
			System.out.println("Read plan: "
					+ reader.paramGet("/reader/read/plan"));
			System.out.println("Region: "
					+ reader.paramGet("/reader/region/id"));
			// printRegionArray((Region[]) reader
			// .paramGet("/reader/region/supportedRegions"));
		} catch (ReaderException e) {
			e.printStackTrace();
		}

		//this.startReading();
	}

	private void printArray(int[] array) {
		System.out.println("Array: ");
		for (Integer i : array) {
			System.out.println(i);
		}
		System.out.println("fin");
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

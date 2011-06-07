/**
 * 
 */
package org.rifidi.edge.adapter.thingmagic6;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(Thingmagic6SensorSession.class);

	private static final String FIRMWARE_PATH = "/usr/local/sbin/rifidi/update/firmware.sim";

	public Reader reader;

	int maxConAttempts = -1;
	int reconnectionInterval = 2500;

	private String readerID;
	private String port;
	private int[] antennas;
	private int upgradeFirmware;

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
			int upgradeFirmware,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, ID, commandConfigurations);
		this.readerID = readerID;
		this.port = port;
		this.antennas = antennas;
		this.upgradeFirmware = upgradeFirmware;
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
		connectingLoop.set(true);
		try {
			for (int connCount = 0; connCount < maxConAttempts
					|| maxConAttempts == -1; connCount++) {
				try {
					reader = Reader.create(port);
					reader.connect();
					connected = true;
					logger.info("Successfully connected to Thingmagic 6e at "
							+ this.port);
					break;
				} catch (ReaderException e) {
					if (logger.isDebugEnabled()) {
						logger.debug("Failed to connect to Thingmagic6 at "
								+ this.port + ", reason is: " + e.getMessage());
					}
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
			logger.error("Exception while setting reader parameters: "
					+ e.getMessage());
		}

		if (this.upgradeFirmware == 1) {
			try {
				logger.info("Upgrading firmware of Thingmagic6e at " + port);
				reader
						.firmwareLoad(new FileInputStream(new File(
								FIRMWARE_PATH)));
				logger.info("Firmware has upgraded successfully!");
			} catch (FileNotFoundException e1) {
				logger.error("File at " + FIRMWARE_PATH + " not found");
			} catch (IOException e1) {
				logger.error("There was a problem upgrading "
						+ "the firmware - aborted: " + e1.getMessage());
			} catch (ReaderException e) {
				logger.error("There was an exception: " + e.getMessage());
			}
		}

		try {
			logger.info("Read plan: "
					+ reader.paramGet("/reader/read/plan"));
			logger.info("Software Version :" + reader.paramGet("/reader/version/software"));
			logger.info("Hardware Version :" + reader.paramGet("/reader/version/hardware"));
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
		setStatus(SessionStatus.CLOSED);
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

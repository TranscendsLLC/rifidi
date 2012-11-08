/**
 * 
 */
package org.rifidi.edge.adapter.thinkify50;

import gnu.io.CommPortIdentifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionDTO;
import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.configuration.JMXMBean;
import org.rifidi.edge.configuration.Property;
import org.rifidi.edge.configuration.PropertyType;
import org.rifidi.edge.exceptions.CannotCreateSessionException;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.CannotDestroySensorException;
import org.rifidi.edge.sensors.SensorSession;

import com.thinkify.rfid.SerialManager;

/**
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
@JMXMBean
public class Thinkify50Sensor extends AbstractSensor<Thinkify50SensorSession> {

	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(Thinkify50Sensor.class);

	/** A hashmap containing all the properties for this reader */
	@SuppressWarnings("unused")
	private final ConcurrentHashMap<String, String> readerProperties;
	/** The port the reader is connected to */
	private volatile String port = Thinkify50Constants.PORT;
	/** The read rate for the reader */
	private volatile int readrate = 1000;
	/** Flag to check if this reader is destroyed. */
	private AtomicBoolean destroyed = new AtomicBoolean(false);
	/** The reader attenuation */
	private volatile Integer ra = 6;
	/** The reader mask */
	private String ma = "";
	/** Time between two connection attempts. */
	private volatile Integer reconnectionInterval = 2500;
	/** Number of connection attempts before a connection goes into fail state. */
	private volatile Integer maxNumConnectionAttempts = 10;
	/** The only session an LLRP reader allows. */
	private AtomicReference<Thinkify50SensorSession> session = new AtomicReference<Thinkify50SensorSession>();
	/** Provided by spring. */
	private final Set<AbstractCommandConfiguration<?>> commands;
	/** The ID of the session */
	private AtomicInteger sessionIDcounter = new AtomicInteger(0);

	public static final MBeanInfo mbeaninfo;
	private String displayName;

	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(Thinkify50Sensor.class);
	}

	public Thinkify50Sensor(Set<AbstractCommandConfiguration<?>> commands) {
		this.commands = commands;
		this.readerProperties = new ConcurrentHashMap<String, String>();

		System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

		HashSet<CommPortIdentifier> set = SerialManager
				.getAvailableSerialPorts();

		for (CommPortIdentifier com : set) {
			System.out.println("Port: " + com.getName());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.AbstractSensor#createSensorSession()
	 */
	@Override
	public String createSensorSession() throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionIDcounter.incrementAndGet();
			if (session.compareAndSet(null, new Thinkify50SensorSession(this,
					sessionID.toString(), notifierService, super.getID(), port,
					reconnectionInterval, maxNumConnectionAttempts, commands,
					readrate, ra, ma))) {

				// TODO: remove this once we get AspectJ in here!
				notifierService.addSessionEvent(this.getID(),
						Integer.toString(sessionID));
				return sessionID.toString();
			}
		}
		throw new CannotCreateSessionException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.AbstractSensor#createSensorSession(org.rifidi
	 * .edge.api.SessionDTO)
	 */
	@Override
	public String createSensorSession(SessionDTO sessionDTO)
			throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = Integer.parseInt(sessionDTO.getID());
			if (session.compareAndSet(null, new Thinkify50SensorSession(this,
					sessionID.toString(), notifierService, super.getID(), port,
					reconnectionInterval, maxNumConnectionAttempts, commands,
					readrate, ra, ma))) {
				session.get().restoreCommands(sessionDTO);
				// TODO: remove this once we get AspectJ in here!
				notifierService.addSessionEvent(this.getID(),
						Integer.toString(sessionID));
				return sessionID.toString();
			}

		}
		throw new CannotCreateSessionException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.AbstractSensor#getSensorSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> ret = new HashMap<String, SensorSession>();
		Thinkify50SensorSession thinksession = session.get();
		if (thinksession != null) {
			ret.put(thinksession.getID(), thinksession);
		}
		return ret;
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	@Property(displayName = "Port", description = "Port of the Reader", writable = true, type = PropertyType.PT_STRING, category = "connection"
			+ "", orderValue = 1, defaultValue = Thinkify50Constants.PORT)
	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	@Property(displayName = "ReadRate", description = "The rate that the reader will read at in milliseconds", writable = true, type = PropertyType.PT_INTEGER, category = "reading"
			+ "", orderValue = 2, defaultValue = Thinkify50Constants.READRATE)
	public int getReadRate() {
		return readrate;
	}

	public void setReadRate(Integer readrate) {
		this.readrate = readrate;
	}

	@Property(displayName = "ra", description = "Sets the reader attenuation", writable = true, type = PropertyType.PT_INTEGER, category = "reading"
			+ "", orderValue = 2, defaultValue = Thinkify50Constants.RA, minValue = "0", maxValue = "19")
	public int getra() {
		return ra;
	}

	public void setra(Integer ra) {
		this.ra = ra;
	}

	@Property(displayName = "ma", description = "Sets the mask for the reader", writable = true, type = PropertyType.PT_STRING, category = "reading"
			+ "", orderValue = 2, defaultValue = Thinkify50Constants.MA)
	public String getma() {
		return ma;
	}

	public void setma(String ma) {
		this.ma = ma;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.AbstractSensor#destroySensorSession(java.lang
	 * .String)
	 */
	@Override
	public void destroySensorSession(String id)
			throws CannotDestroySensorException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.AbstractSensor#getDisplayName()
	 */
	@Property(displayName = "Display Name", description = "Logical Name of Reader"
			+ "", writable = true, type = PropertyType.PT_STRING, category = "connection", defaultValue = "Thinkify50", orderValue = 0)
	@Override
	protected String getDisplayName() {
		// TODO Auto-generated method stub
		return displayName;
	}

	/**
	 * Gets the reconnect interval.
	 * 
	 * @return the reconnectionInterval
	 */
	@Property(displayName = "Reconnection Interval", description = "Upon connection failure, the "
			+ "time to wait between two connection attempts (ms)", writable = true, type = PropertyType.PT_INTEGER, category = "connection"
			+ "", defaultValue = Thinkify50Constants.RECONNECTION_INTERVAL, orderValue = 4, minValue = "0")
	public Integer getReconnectionInterval() {
		return reconnectionInterval;
	}

	/**
	 * Sets the reconnect interval.
	 * 
	 * @param reconnectionInterval
	 *            the reconnectionInterval to set
	 */
	public void setReconnectionInterval(Integer reconnectionInterval) {
		this.reconnectionInterval = reconnectionInterval;
	}

	/**
	 * Gets the number of connection attempts to try before giving up.
	 * 
	 * @return the maxNumConnectionAttempts
	 */
	@Property(displayName = "Maximum Connection Attempts" + "", description = "Upon connection failure, the number of times to attempt to "
			+ "recconnect before giving up. If set to '-1', then try forever", writable = true, type = PropertyType.PT_INTEGER, category = "connection"
			+ "", defaultValue = Thinkify50Constants.MAX_CONNECTION_ATTEMPTS, orderValue = 5, minValue = "-1")
	public Integer getMaxNumConnectionAttempts() {
		return maxNumConnectionAttempts;
	}

	/**
	 * Sets the number of connection attempts to try before giving up.
	 * 
	 * @param maxNumConnectionAttempts
	 *            the maxNumConnectionAttempts to set
	 */
	public void setMaxNumConnectionAttempts(Integer maxNumConnectionAttempts) {
		this.maxNumConnectionAttempts = maxNumConnectionAttempts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.AbstractSensor#unbindCommandConfiguration(org
	 * .rifidi.edge.sensors.AbstractCommandConfiguration, java.util.Map)
	 */
	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}

}

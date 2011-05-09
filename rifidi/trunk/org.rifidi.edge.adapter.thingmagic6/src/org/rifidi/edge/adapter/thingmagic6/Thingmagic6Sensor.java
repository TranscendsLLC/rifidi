/**
 * 
 */
package org.rifidi.edge.adapter.thingmagic6;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.thingmagic6.utility.ThingmagicConstants;
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

/**
 * 
 * 
 * @author Matthew Dean
 */
@JMXMBean
public class Thingmagic6Sensor extends AbstractSensor<Thingmagic6SensorSession> {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(Thingmagic6Sensor.class);
	/** A hashmap containing all the properties for this reader */
	private final ConcurrentHashMap<String, String> readerProperties;
	/** The port the reader is connected to */
	private volatile String port = "tmr:///dev/ttyACM0";
	/** Flag to check if this reader is destroyed. */
	private AtomicBoolean destroyed = new AtomicBoolean(false);
	/** Time between two connection attempts. */
	private volatile Integer reconnectionInterval = 500;
	/** Number of connection attempts before a connection goes into fail state. */
	private volatile Integer maxNumConnectionAttempts = 10;
	/** The only session an LLRP reader allows. */
	private AtomicReference<Thingmagic6SensorSession> session = new AtomicReference<Thingmagic6SensorSession>();
	/** Provided by spring. */
	private final Set<AbstractCommandConfiguration<?>> commands;
	/** The ID of the session */
	private AtomicInteger sessionIDcounter = new AtomicInteger(0);

	public static final MBeanInfo mbeaninfo;
	private String displayName;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(Thingmagic6Sensor.class);
	}

	public Thingmagic6Sensor(Set<AbstractCommandConfiguration<?>> commands) {
		this.commands = commands;
		this.readerProperties = new ConcurrentHashMap<String, String>();
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	@Property(displayName = "Port", description = "Port of the Reader" + "", writable = true, type = PropertyType.PT_STRING, category = "connection"
			+ "", orderValue = 1, defaultValue = ThingmagicConstants.PORT)
	public String getPort() {
		return port;
	}
	
	public void setPort(String port) {
		this.port=port;
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
	 * @see org.rifidi.edge.sensors.AbstractSensor#createSensorSession()
	 */
	@Override
	public String createSensorSession() throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionIDcounter.incrementAndGet();
			if (session.compareAndSet(null, new Thingmagic6SensorSession(this,
					sessionID.toString(), notifierService, super.getID(), port,
					reconnectionInterval, maxNumConnectionAttempts, commands))) {

				// TODO: remove this once we get AspectJ in here!
				notifierService.addSessionEvent(this.getID(), Integer
						.toString(sessionID));
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
			if (session.compareAndSet(null, new Thingmagic6SensorSession(this,
					sessionID.toString(), notifierService, super.getID(), port,
					reconnectionInterval, maxNumConnectionAttempts, commands))) {
				session.get().restoreCommands(sessionDTO);
				// TODO: remove this once we get AspectJ in here!
				notifierService.addSessionEvent(this.getID(), Integer
						.toString(sessionID));
				return sessionID.toString();
			}

		}
		throw new CannotCreateSessionException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.AbstractSensor#destroySensorSession(java.lang
	 * .String)
	 */
	@Override
	public void destroySensorSession(String sessionid)
			throws CannotDestroySensorException {
		Thingmagic6SensorSession thingsession = session.get();
		if (thingsession != null) {
			if (thingsession.getID().equals(sessionid)) {
				thingsession.killAllCommands();
				thingsession.disconnect();
				// TODO: remove this once we get AspectJ in here!
				session.set(null);
				notifierService.removeSessionEvent(this.getID(), sessionid);
			}
		}
		logger.warn("Tried to delete a non existent session: " + sessionid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.AbstractSensor#getDisplayName()
	 */
	@Override
	@Property(displayName = "Display Name", description = "Logical Name of Reader"
			+ "", writable = true, type = PropertyType.PT_STRING, category = "connection", defaultValue = "Thingmagic6", orderValue = 0)
	protected String getDisplayName() {
		return displayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.AbstractSensor#getSensorSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> ret = new HashMap<String, SensorSession>();
		Thingmagic6SensorSession thingsession = session.get();
		if (thingsession != null) {
			ret.put(thingsession.getID(), thingsession);
		}
		return ret;
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
		// TODO Auto-generated method stub

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

	/**
	 * Gets the reconnect interval.
	 * 
	 * @return the reconnectionInterval
	 */
	@Property(displayName = "Reconnection Interval", description = "Upon connection failure, the " +
			"time to wait between two connection attempts (ms)", writable = true, type = PropertyType.PT_INTEGER, category = "connection" +
					"", defaultValue = ThingmagicConstants.RECONNECTION_INTERVAL, orderValue = 4, minValue = "0")
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
	@Property(displayName = "Maximum Connection Attempts" +
			"", description = "Upon connection failure, the number of times to attempt to recconnect before " +
					"giving up. If set to '-1', then try forever", writable = true, type = PropertyType.PT_INTEGER, category = "connection", defaultValue = ThingmagicConstants.MAX_CONNECTION_ATTEMPTS, orderValue = 5, minValue = "-1")
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

}

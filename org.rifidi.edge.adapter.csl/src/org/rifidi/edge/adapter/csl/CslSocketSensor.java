/*
 *  GenericSensor.java
 *
 *  Created:	Aug 5, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.adapter.csl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanInfo;

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
 * A generic plugin that can handle csl reader connection management it is sent
 * to the serversocket in the correct format. The format is as follows:
 * 
 * ID:(tag ID)|Antenna:(antenna)|Timestamp:(millis since epoch)
 * 
 * Any amount of other values can be specified in pairs delimited by colons,
 * with pipes delimiting pairs.
 * 
 * @author Carlson Lam - carlson.lam@convergence.com.hk
 */
@JMXMBean
public class CslSocketSensor extends AbstractSensor<CslSocketSensorSession> {

	/**
	 * 
	 */
	private String ipAddress = "192.168.25.203";
	/** The ID of the session */
	private AtomicInteger sessionID = new AtomicInteger(0);
	/** The name of the reader that will be displayed */
	private String displayName = "CSL";

	/** The antenna power */
	private String power_ant = "300";
	/** The Dwell Time */
	private String dwellTime = "0";
	/** The Start Q parameter */
	private String startQ = "7";
	/** Country / Region for the reader operate */
	private String country = "HK";	

	private String inventoryRound = "65536";
	private String algorithm_Q = "Dyn_Q";
	private String link_Profile = "2";	
	
	/** General output port - GPO */
	private String gpo_0 = "0";
	private String gpo_1 = "0";		
	
	private String notifyAddrPort = "3000";
	
	
	/** Flag to check if this reader is destroyed. */
	private AtomicBoolean destroyed = new AtomicBoolean(false);
	/** The only session an generic reader allows. */
	private AtomicReference<CslSocketSensorSession> session = new AtomicReference<CslSocketSensorSession>();

	/** MBeanInfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(CslSocketSensor.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		// No properties.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#createSensorSession()
	 */
	@Override
	public String createSensorSession() throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if(sessionID < 0)
			{
				this.sessionID.set(1);
				sessionID = this.sessionID.get();
			}
			
			if (session.compareAndSet(null, new CslSocketSensorSession(this,
					Integer.toString(sessionID), notifierService,
					super.getID(), this.notifyAddrPort, this.ipAddress, this.displayName, 
					this.power_ant, this.dwellTime, this.country,
					this.inventoryRound, this.algorithm_Q, this.link_Profile,
					this.startQ, this.gpo_0, this.gpo_1,
					new HashSet<AbstractCommandConfiguration<?>>()))) {
				// TODO: remove this once we get AspectJ in here!
				notifierService.addSessionEvent(this.getID(),
						Integer.toString(sessionID));
				// notifierService.notifyAll();
				return sessionID.toString();
			}
		}
		throw new CannotCreateSessionException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#createSensorSession(
	 * org.rifidi.edge.api.rmi.dto.SessionDTO)
	 */
	@Override
	public String createSensorSession(SessionDTO sessionDTO)
			throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if(sessionID < 0)
			{
				this.sessionID.set(1);
				sessionID = this.sessionID.get();
			}
			
			if (session.compareAndSet(null, new CslSocketSensorSession(this,
					Integer.toString(sessionID), notifierService,
					super.getID(), this.notifyAddrPort, this.ipAddress, this.displayName, 
					this.power_ant, this.dwellTime, this.country,
					this.inventoryRound, this.algorithm_Q, this.link_Profile,
					this.startQ, this.gpo_0, this.gpo_1,
					new HashSet<AbstractCommandConfiguration<?>>()))) {
				// TODO: remove this once we get AspectJ in here!
				notifierService.addSessionEvent(this.getID(),
						Integer.toString(sessionID));
				// notifierService.notifyAll();
				return sessionID.toString();
			}
		}
		throw new CannotCreateSessionException();
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.RifidiService#destroy()
	 */
	@Override
	protected void destroy() {
		if (destroyed.compareAndSet(false, true)) {
			super.destroy();
			CslSocketSensorSession genericsession = session.get();
			
			
			if (genericsession != null) {
				try {
					destroySensorSession(genericsession.getID());
				} catch (CannotDestroySensorException e) {
					// logger.warn(e.getMessage());
				}
			}
		}
	}

	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#destroySensorSession
	 * (java.lang.String)
	 */
	@Override
	public void destroySensorSession(String sessionid)
			throws CannotDestroySensorException {
			CslSocketSensorSession genericsession = session.get();
			if (genericsession != null)
			{
				if (genericsession.getID().equals(sessionid)) 
				{
					try {
						genericsession.killAllCommands();
						genericsession.disconnect();
					} catch (Exception e) {
						// FIXME: We don't really care at the moment, but handle
						// this properly.
					}
					// TODO: remove this once we get AspectJ in here!			
					session.set(null);
					notifierService.removeSessionEvent(this.getID(), sessionid);
				}
			}
			// logger.warn("Tried to delete a non existent session: " + sessionid);
	}

	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#getDisplayName()
	 */
	@Override
	@Property(displayName = "Display Name", description = "Display Name of the Reader", writable = true, type = PropertyType.PT_STRING, category = "connection", defaultValue = "CslCS203", orderValue = 0)
	// protected String getDisplayName() {
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#getSensorSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> ret = new HashMap<String, SensorSession>();
		CslSocketSensorSession genericsession = session.get();
		if (genericsession != null) {
			ret.put(genericsession.getID(), genericsession);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensor#unbindCommandConfiguration
	 * (org.rifidi.edge.sensors.commands.AbstractCommandConfiguration,
	 * java.util.Map)
	 */
	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.services.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return mbeaninfo;
	}



	/**
	 * Returns the IP address of the reader.
	 * 
	 * @return the ipAddress
	 */
	@Property(displayName = "IP Address", description = "IP Address of the Reader", 
			writable = true, type = PropertyType.PT_STRING, category = "connection", 
			defaultValue = "192.168.25.203", orderValue = 1)
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @return The name of the Tag Server Port.
	 */
	@Property(displayName = "Notify Address Port", description = "Notify Address Port. " +
			"Please ensure that the Port value for each reader is unique",
			writable = true, type = PropertyType.PT_STRING, category = "connection",
			defaultValue = "3000", orderValue = 2)
	public String getNotifyAddrPort() {
		return this.notifyAddrPort;
	}
	
	
	
	/**
	 * @return The country in string.
	 */
	@Property(displayName = "Country", description = "The country where the Reader operate. \n" +
			"FCC / ETSI / IN / G800 / AU / BR1 / BR2 / HK / TH / SG / MY / ZA / ID / CN / CN1 / CN2 " +
			"/ CN3 / CN4 / CN5 / CN6 / CN7 / CN8 / CN9 / CN10 / CN11 / CN12 / TW / JP", 
			writable = true, type = PropertyType.PT_STRING, category = "connection", 
			defaultValue = "HK", orderValue = 3)
	public String getCountry() {
		return this.country;
	}

	
	/**
	 * @return The antenna power.
	 */
	@Property(displayName = "Reader Antenna Power", description = "The Antenna Power of Reader. The value starts from 0 to 300 ", 
			writable = true, type = PropertyType.PT_STRING, category = "connection", 
			defaultValue = "300", orderValue = 4)
	public String getPowerAnt() {
		return this.power_ant;
	}	
	
	/**
	 * @return The Link Profile in string.
	 */
	@Property(displayName = "Link Profile", description = "the Link Profile the Reader operate.  The value starts from 0 to 4", 
			writable = true, type = PropertyType.PT_STRING, category = "connection", 
			defaultValue = "2", orderValue = 5)
	public String getLinkProfile() {
		return this.link_Profile;
	}	


	/**
	 * @return The Dwell Time.
	 */
/*
	@Property(displayName = "Dwell Time", description = "The time for the multi-port reader to read tag data from one port before switching to another Port", 
			writable = true, type = PropertyType.PT_STRING, category = "connection", 
			defaultValue = "2000", orderValue = 6)
	public String getDwellTime() {
		return this.dwellTime;
	}
*/
	
	
	/**
	 * @return Q Algorithm parameter.
	 */
	@Property(displayName = "Q Algorithm", description = "Set Q algorithm, \"Fix_Q\" for Fix Q, \"Dyn_Q\" for Dynamic Q",
			writable = true, type = PropertyType.PT_STRING, category = "connection"
			+ "", defaultValue = "Dyn_Q", orderValue = 7)
	public String getQAlg() {
		return this.algorithm_Q;
	}

	
	/**
	 * @return The start Q parameter.
	 */
	@Property(displayName = "Start Q", description = "The start value for Q algorithm. The value starts from 0 to 15",
			writable = true, type = PropertyType.PT_STRING, category = "connection"
			+ "", defaultValue = "7", orderValue = 8)
	public String getStartQ() {
		return this.startQ;
	}

	
	
	
	
	/**
	 * Set the IP address of the reader.
	 * 
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	
	/**
	 * Set the Notify Address Port for the reader.
	 * 
	 * @param port
	 */
	public void setNotifyAddrPort(String notifyAddrPort) {
		this.notifyAddrPort = notifyAddrPort;
	}
	
	/**
	 * Set Country.
	 * 
	 * @param country
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * Set Link Profile.
	 * 
	 * @param link_Profile
	 */
	public void setLinkProfile(String link_Profile) {
		this.link_Profile = link_Profile;
	}	
	
	/**
	 * Set the Antenna Power.
	 * 
	 * @param power_ant
	 */
	public void setPowerAnt(String power_ant) {
		this.power_ant = power_ant;
	}
	
	/**
	 * Set the Dwell Time.
	 * 
	 * @param dwellTime
	 */
/*	
	public void setDwellTime(String dwellTime) {
		this.dwellTime = dwellTime;
	}	
*/
	
	/**
	 * Set the Q Algorithm parameter value.
	 * 
	 * @param startQ
	 */
	public void setQAlg(String QAlg) {
		this.algorithm_Q = QAlg;
	}		
	
	
	/**
	 * Set the Start Q parameter value.
	 * 
	 * @param startQ
	 */
	public void setStartQ(String startQ) {
		this.startQ = startQ;
	}	



	
}

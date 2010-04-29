/*
 *  AmbientBarcodeReader.java
 *
 *  Created:	Apr 22, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.ambient.barcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.exceptions.CannotCreateSessionException;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.exceptions.CannotDestroySensorException;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.jms.core.JmsTemplate;

/**
 * Sensor class for the Ambient Barcode Reader.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AmbientBarcodeReader extends
		AbstractSensor<AmbientBarcodeReaderSession> {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AmbientBarcodeReader.class);
	/** Spring JMS template */
	private volatile JmsTemplate template;
	/** The ID of the session */
	private AtomicInteger sessionID = new AtomicInteger(0);
	/** A wrapper containing the service to send JMS notifications */
	private volatile NotifierService notifyServiceWrapper;
	/** The name of the reader that will be displayed */
	private String displayName = "Ambient Barcode";
	/** Flag to check if this reader is destroyed. */
	private AtomicBoolean destroyed = new AtomicBoolean(false);
	/** The only session an acura reader allows. */
	private AtomicReference<AmbientBarcodeReaderSession> session = new AtomicReference<AmbientBarcodeReaderSession>();

	/** MBeanInfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(AmbientBarcodeReader.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		// TODO: No properties, not needed.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#createSensorSession()
	 */
	@Override
	public String createSensorSession() throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new AmbientBarcodeReaderSession(
					this, Integer.toString(sessionID),
					AmbientBarcodeConstants.PORT, this.template,
					this.notifyServiceWrapper, super.getID(),
					new HashSet<AbstractCommandConfiguration<?>>()))) {
				// TODO: remove this once we get AspectJ in here!
				notifyServiceWrapper.addSessionEvent(this.getID(), Integer
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
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#createSensorSession(
	 * org.rifidi.edge.api.rmi.dto.SessionDTO)
	 */
	@Override
	public String createSensorSession(SessionDTO sessionDTO)
			throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			// TODO: Fix the IP and the port here.
			if (session.compareAndSet(null, new AmbientBarcodeReaderSession(
					this, Integer.toString(sessionID), AmbientBarcodeConstants.PORT,
					this.template, this.notifyServiceWrapper, super.getID(),
					new HashSet<AbstractCommandConfiguration<?>>()))) {
				// TODO: remove this once we get AspectJ in here!
				notifyServiceWrapper.addSessionEvent(this.getID(), Integer
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
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#destroySensorSession
	 * (java.lang.String)
	 */
	@Override
	public void destroySensorSession(String id)
			throws CannotDestroySensorException {
		AmbientBarcodeReaderSession ambientsession = session.get();
		if (ambientsession != null && ambientsession.getID().equals(id)) {
			session.set(null);
			ambientsession.killAllCommands();
			ambientsession.disconnect();
			// TODO: remove this once we get AspectJ in here!
			notifyServiceWrapper.removeSessionEvent(this.getID(), id);
		} else {
			String error = "Tried to delete a non existend session: " + id;
			logger.warn(error);
			throw new CannotDestroySensorException(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getDisplayName()
	 */
	@Override
	protected String getDisplayName() {
		return displayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getSensorSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> ret = new HashMap<String, SensorSession>();
		AmbientBarcodeReaderSession ambientsession = session.get();
		if (ambientsession != null) {
			ret.put(ambientsession.getID(), ambientsession);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#unbindCommandConfiguration
	 * (org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration,
	 * java.util.Map)
	 */
	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// No commands, ignored.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.services.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return mbeaninfo;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/***
	 * 
	 * @param wrapper
	 *            The JMS Notifier to set
	 */
	public void setNotifiyService(NotifierService wrapper) {
		this.notifyServiceWrapper = wrapper;
	}

}

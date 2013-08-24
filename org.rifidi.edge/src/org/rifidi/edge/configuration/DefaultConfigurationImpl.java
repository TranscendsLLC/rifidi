/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.configuration;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.rifidi.edge.api.SessionDTO;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.exceptions.CannotCreateSessionException;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.SensorSession;

/**
 * A default implementation of a configuration by wrapping a service object. It
 * also registers itself in the OSGi service registry under the Configuration
 * Interface.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class DefaultConfigurationImpl implements Configuration, ServiceListener {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(DefaultConfigurationImpl.class);

	/** The object that gets serviced. */
	private AtomicReference<RifidiService> target;
	/** The id this service is registered by in the registry. */
	private final String serviceID;
	/** Names of properties mapped to their annotations */
	protected Map<String, Property> nameToProperty;
	/** Names of operations mapped to their annotations */
	protected Map<String, Operation> nameToOperation;
	/** ID of the factory owning the configuration. */
	protected final String factoryID;
	/** Attributes set while we didn't have an actual instance. */
	private volatile AttributeList attributes;
	/**
	 * Map that has the attribute names as key and their position in the
	 * attribute list as the value.
	 */
	private volatile Map<String, Integer> nameToPos;
	/** Listeners to changes of attributes on this object */
	private final Set<AttributesChangedListener> listeners;
	/** The bundle context for registering services. */
	private volatile BundleContext context;
	/** Notifier service for jms messages. */
	private final NotifierService notifierService;
	/** JMXservice reference. */
	private volatile JMXService jmxService;
	/** DTOs that describe the sessions if this config describes a reader. */
	private final Map<SessionDTO, String> sessionDTOs;

	/**
	 * Constructor.
	 * 
	 * @param context
	 * @param serviceID
	 * @param factoryID
	 * @param attributes
	 * @param sessionDTOs
	 */
	public DefaultConfigurationImpl(final String serviceID,
			final String factoryID, final AttributeList attributes,
			final NotifierService notifierService, final JMXService jmxService,
			Set<SessionDTO> sessionDTOs) {
		this.notifierService = notifierService;
		this.nameToProperty = new HashMap<String, Property>();
		this.nameToOperation = new HashMap<String, Operation>();
		this.factoryID = factoryID;
		this.serviceID = serviceID;
		this.attributes = (AttributeList) attributes.clone();
		this.listeners = new CopyOnWriteArraySet<AttributesChangedListener>();
		this.target = new AtomicReference<RifidiService>(null);
		this.jmxService = jmxService;
		this.sessionDTOs = new ConcurrentHashMap<SessionDTO, String>();
		if (sessionDTOs != null) {
			for (SessionDTO dto : sessionDTOs) {
				this.sessionDTOs.put(dto, "-1");
			}
		}
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(BundleContext context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.Configuration#getServiceID()
	 */
	@Override
	public String getServiceID() {
		return serviceID;
	}

	/**
	 * Set the service that this configuration wraps
	 * 
	 * @param target
	 *            the target to set
	 */
	public void setTarget(final RifidiService target) {
		if (this.target.compareAndSet(null, target)) {
			if (target != null) {
				// get the whole list of attributes from the target as we might
				// only
				// have a partial list from the config file
				target.setAttributes(attributes);
				// keep a local clone
				attributes = (AttributeList) target.getAttributes().clone();
				nameToPos = new HashMap<String, Integer>();
				List<Attribute> attrs = attributes.asList();
				if (target instanceof AbstractSensor<?>) {
					for (SessionDTO sessionDTO : sessionDTOs.keySet()) {
						try {
							((AbstractSensor<?>) target)
									.createSensorSession(sessionDTO);
							// test to see if the session should be
							// restarted
							if (shouldStartSession(sessionDTO)) {
								// if it should, restart it
								restartSession((AbstractSensor<?>) target,
										sessionDTO);
							}
						} catch (CannotCreateSessionException e) {
							logger.error("Cannot Create Session ", e);
						} catch (Exception e) {
							logger.error("Cannot restart session ", e);
						}

					}
				}
				for (int count = 0; count < attributes.size(); count++) {
					nameToPos.put(attrs.get(count).getName(), count);
				}
				jmxService.publish(this);
				return;
			}
			jmxService.unpublish(this);
			return;
		}
		// if the value was already set we got a problem
		logger.warn("Tried to set the target but was already set.");
	}

	/**
	 * A helper method to determine if the supplied sessionDTO should restart;
	 * 
	 * @param dto
	 *            The DTO of the session
	 * @return
	 */
	private boolean shouldStartSession(SessionDTO dto) {
		// test to see if the autostart system property is true
		if (System.getProperties().getProperty("org.rifidi.edge.autostart")
				.equalsIgnoreCase("true")) {
			SessionStatus stat = dto.getStatus();
			// check to see if the session was saved in a connecting state
			if (stat == SessionStatus.CONNECTING
					|| stat == SessionStatus.LOGGINGIN
					|| stat == SessionStatus.PROCESSING) {
				return true;
			}
		}
		return false;
	}

	/**
	 * A helper method to start a session.
	 * 
	 * TODO: Would be better to replace this with a sessioin starter service
	 * that would allow a strategy to be provided on how many session can be
	 * started at once, since sessions have alot of threads and may be expensive
	 * to start. The service could also be used by the console and the RMI
	 * server.
	 * 
	 * @param sensor
	 *            The sensor that contains the session
	 * @param sessionDTO
	 *            The DTO of the session
	 */
	private void restartSession(final AbstractSensor<?> sensor,
			final SessionDTO sessionDTO) {
		final SensorSession session = sensor.getSensorSessions().get(
				sessionDTO.getID());
		if (session == null) {
			logger.warn("Session not available: " + sensor.getID() + ":"
					+ sessionDTO.getID());
		}
		logger.info("Restarting session: " + session);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					session.connect();
					sensor.applyPropertyChanges();
				} catch (IOException e) {
					logger.warn("Cannot start session: " + sensor.getID() + ":"
							+ session.getID());
					session.disconnect();
				}
			}
		});
		t.start();
	}

	/**
	 * @return the target
	 */
	public RifidiService getTarget() {
		return target.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.Configuration#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return factoryID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(final String attribute)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		assert (attribute != null);
		for (Attribute attr : attributes.asList()) {
			if (attribute.equals(attr.getName())) {
				return attribute;
			}
		}
		throw new AttributeNotFoundException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.Configuration#getAttributeNames()
	 */
	@Override
	public String[] getAttributeNames() {
		Set<String> names = new HashSet<String>();
		for (Attribute attr : attributes.asList()) {
			names.add(attr.getName());
		}
		return names.toArray(new String[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
	 */
	@Override
	public AttributeList getAttributes(String[] attributes) {
		assert (attributes != null);
		AttributeList ret = new AttributeList();
		Set<String> attribNames = new HashSet<String>();
		for (int count = 0; count < attributes.length; count++) {
			attribNames.add(attributes[count]);
		}
		for (Attribute attr : this.attributes.asList()) {
			if (attribNames.contains(attr.getName())) {
				ret.add(attr);
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		RifidiService service = target.get();
		if (service != null) {
			return service.getMBeanInfo();
		}
		// TODO: needs fixing to make JMX work again
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#invoke(java.lang.String,
	 * java.lang.Object[], java.lang.String[])
	 */
	@Override
	public Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		if (nameToOperation.containsKey(actionName)) {
			try {
				Method method = target.getClass().getMethod(actionName);
				method.invoke(target);
			} catch (SecurityException e) {
				logger.error(e);
			} catch (NoSuchMethodException e) {
				logger.error(e);
			} catch (IllegalArgumentException e) {
				logger.error(e);
			} catch (IllegalAccessException e) {
				logger.error(e);
			} catch (InvocationTargetException e) {
				logger.error(e);
			}
		} else {
			logger.warn("Try to call non existend operation " + actionName
					+ " on " + target.getClass());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
	 */
	@Override
	public void setAttribute(final Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {
		assert (attribute != null);
		RifidiService service = target.get();
		if (service != null) {
			service.setAttribute(attribute);
		}

		attributes.set(nameToPos.get(attribute.getName()), attribute);
		notifierService.attributesChanged(getServiceID(),
				(AttributeList) attributes.clone());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.management.DynamicMBean#setAttributes(javax.management.AttributeList
	 * )
	 */
	@Override
	public AttributeList setAttributes(final AttributeList attributes) {
		assert (attributes != null);
		RifidiService service = target.get();
		if (service != null) {
			service.setAttributes(attributes);
		}

		// keep track of changed attributes since there might be an error
		AttributeList changedAttributes = new AttributeList();

		for (Attribute attribute : attributes.asList()) {

			String attrName = attribute.getName();
			Integer pos = nameToPos.get(attrName);
			if (pos == null) {
				logger.error("Error when trying to set " + attribute.getName());
			} else {
				this.attributes.set(pos, attribute);
				changedAttributes.add(this.attributes.get(pos));
			}

		}

		notifierService.attributesChanged(getServiceID(),
				(AttributeList) changedAttributes);
		return (AttributeList) changedAttributes.clone();
	}

	protected void destroy() {
		RifidiService service = target.get();
		if (service != null) {
			service.destroy();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.Configuration#getAttributes()
	 */
	@Override
	public Map<String, Object> getAttributes() {
		Map<String, Object> ret = new HashMap<String, Object>();
		for (Attribute attr : this.attributes.asList()) {
			ret.put(attr.getName(), attr.getValue());
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.Configuration#addAttributesChangedListener
	 * (org .rifidi.configuration.listeners.AttributesChangedListener)
	 */
	@Override
	public void addAttributesChangedListener(AttributesChangedListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.configuration.Configuration#
	 * removeAttributesChangedListener
	 * (org.rifidi.edge.configuration.listeners.AttributesChangedListener)
	 */
	@Override
	public void removeAttributesChangedListener(
			AttributesChangedListener listener) {
		listeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.
	 * ServiceEvent)
	 */
	@Override
	public synchronized void serviceChanged(ServiceEvent arg0) {
		if (arg0.getServiceReference().getProperty("serviceid") != null) {
			if (arg0.getServiceReference().getProperty("serviceid").equals(
					getServiceID())) {
				if (arg0.getType() == ServiceEvent.REGISTERED) {
					setTarget((RifidiService) context.getService(arg0
							.getServiceReference()));
				} else if (arg0.getType() == ServiceEvent.UNREGISTERING) {
					setTarget(null);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Configuration: " + getServiceID() + " Factory: "
				+ getFactoryID();
	}

}

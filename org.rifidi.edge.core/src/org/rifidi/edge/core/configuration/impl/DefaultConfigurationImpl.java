package org.rifidi.edge.core.configuration.impl;

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
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.configuration.Configuration;
import org.rifidi.edge.core.configuration.RifidiService;
import org.rifidi.edge.core.configuration.annotations.Operation;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.listeners.AttributesChangedListener;
import org.rifidi.edge.core.configuration.services.JMXService;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.services.notification.NotifierService;

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
	private final Map<String, AbstractCommandConfiguration<?>> nameToCommandConfig;

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
		this.nameToCommandConfig = new ConcurrentHashMap<String, AbstractCommandConfiguration<?>>();
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
	 * @see org.rifidi.edge.core.configuration.Configuration#getServiceID()
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
					((AbstractSensor<?>) target).recreateSessions(sessionDTOs
							.keySet());
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
	 * @return the target
	 */
	public RifidiService getTarget() {
		return target.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.Configuration#getFactoryID()
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
	 * @see org.rifidi.edge.core.configuration.Configuration#getAttributeNames()
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
		for (int count = 0; count < attributes.length - 1; count++) {
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
		
		//keep track of changed attributes since there might be an error
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

		notifierService.attributesChanged(getServiceID(),(AttributeList) changedAttributes);
		return (AttributeList) changedAttributes.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.Configuration#destroy()
	 */
	@Override
	public void destroy() {
		RifidiService service = target.get();
		if (service != null) {
			service.destroy();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.Configuration#getAttributes()
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
	 * org.rifidi.edge.core.configuration.Configuration#addAttributesChangedListener
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
	 * (org.rifidi.edge.core.configuration.listeners.AttributesChangedListener)
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

}
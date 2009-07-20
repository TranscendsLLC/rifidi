package org.rifidi.configuration.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.rifidi.configuration.Configuration;
import org.rifidi.configuration.ConfigurationType;
import org.rifidi.configuration.RifidiService;
import org.rifidi.configuration.annotations.Operation;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.configuration.listeners.AttributesChangedListener;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * A default implementation of a configuration by wrapping a service object. It
 * also registers itself in the OSGi service registry under the Configuration
 * Interface.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
@Configurable
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
	/** The type of configuraiton. This is a hack for now */
	private volatile ConfigurationType type;
	/** The bundle context for registering services. */
	private volatile BundleContext context;
	/** Notifier service for jms messages. */
	private final NotifierService notifierService;

	/**
	 * Constructor.
	 * 
	 * @param context
	 * @param serviceID
	 * @param factoryID
	 * @param attributes
	 */
	public DefaultConfigurationImpl(final String serviceID,
			final String factoryID, final AttributeList attributes,
			final NotifierService notifierService) {
		this.notifierService = notifierService;
		this.nameToProperty = new HashMap<String, Property>();
		this.nameToOperation = new HashMap<String, Operation>();
		this.factoryID = factoryID;
		this.serviceID = serviceID;
		this.attributes = (AttributeList) attributes.clone();
		this.listeners = new CopyOnWriteArraySet<AttributesChangedListener>();
		this.target = new AtomicReference<RifidiService>(null);
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
	 * @see org.rifidi.configuration.Configuration#getServiceID()
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
				for (int count = 0; count < attributes.size(); count++) {
					nameToPos.put(attrs.get(count).getName(), count);
				}
			}
			return;
		}
		// if the value was already set we got a problem
		logger.warn("Tried to set the target but was already set.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.Configuration#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return factoryID;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final ConfigurationType type) {
		assert (type != null);
		this.type = type;
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
	 * @see org.rifidi.configuration.Configuration#getAttributeNames()
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
		//TODO: needs fixing to make JMX work again
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
		notifierService.attributesChanged(getServiceID(), (AttributeList)attributes.clone());
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

		for (Attribute attribute : attributes.asList()) {
			this.attributes.set(nameToPos.get(attribute.getName()), attribute);
		}
		
		notifierService.attributesChanged(getServiceID(), (AttributeList)attributes.clone());
		return (AttributeList) attributes.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.Configuration#destroy()
	 */
	@Override
	public void destroy() {
		RifidiService service=target.get();
		if(service!=null){
			service.destroy();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.Configuration#getAttributes()
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
	 * org.rifidi.configuration.Configuration#addAttributesChangedListener(org
	 * .rifidi.configuration.listeners.AttributesChangedListener)
	 */
	@Override
	public void addAttributesChangedListener(AttributesChangedListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.Configuration#removeAttributesChangedListener
	 * (org.rifidi.configuration.listeners.AttributesChangedListener)
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
	public void serviceChanged(ServiceEvent arg0) {
		if (arg0.getServiceReference().getProperty("serviceid") != null
				&& arg0.getServiceReference().getProperty("serviceid").equals(
						getServiceID())) {
			setTarget((RifidiService) context.getService(arg0
					.getServiceReference()));
		}
	}

}
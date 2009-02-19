/**
 * 
 */
package org.rifidi.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.ServiceRegistration;
import org.rifidi.configuration.annotations.JMXMBean;
import org.rifidi.configuration.annotations.Operation;
import org.rifidi.configuration.annotations.Property;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class DefaultConfigurationImpl implements Configuration, Cloneable {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(DefaultConfigurationImpl.class);

	/** The object that gets serviced. */
	private Object target;
	/** The id this service is registered by in the registry. */
	private String serviceID;
	/** Service registration for the instance. */
	private ServiceRegistration registration;
	/** Class this configuration is used for. */
	private Class<?> clazz;
	/** Names of properties mapped to their annotations */
	protected Map<String, Property> nameToProperty;
	/** Names of operations mapped to their annotations */
	protected Map<String, Operation> nameToOperation;
	/** ID of the factory owning the configuration. */
	protected String factoryID;
	/** Attributes set while we diddn't have an actual instance. */
	private Set<Attribute> attributes;
	/** ID of this configuration. */
	private String id;

	/**
	 * Protected constructor used by clone.
	 */
	protected DefaultConfigurationImpl() {
		attributes = new HashSet<Attribute>();
	}

	/**
	 * Constructor.
	 * 
	 * @param target
	 */
	public DefaultConfigurationImpl(Class<?> clazz, String factoryID) {
		this.clazz = clazz;
		this.nameToProperty = new HashMap<String, Property>();
		this.nameToOperation = new HashMap<String, Operation>();
		this.factoryID = factoryID;
		if (clazz.isAnnotationPresent(JMXMBean.class)) {
			// check method annotations
			for (Method method : clazz.getMethods()) {
				// scan for operations annotation
				if (method.isAnnotationPresent(Operation.class)) {
					nameToOperation.put(method.getName(), (Operation) method
							.getAnnotation(Operation.class));
				}
				// scan for property annotation
				if (method.isAnnotationPresent(Property.class)) {
					nameToProperty.put(method.getName().substring(3),
							(Property) method.getAnnotation(Property.class));
				}
			}
		}
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
	 * @param serviceID
	 *            the serviceID to set
	 */
	public void setServiceID(String serviceID) {
		// TODO: check if the id ha already been set?
		if (target instanceof RifidiService) {
			((RifidiService) target).setID(serviceID);
		}
		this.serviceID = serviceID;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(Object target) {
		if (!target.getClass().equals(clazz)) {
			throw new RuntimeException("Got " + target.getClass()
					+ " expected " + clazz);
		}
		this.target = target;
		// TODO: check if the id ha already been set?
		if (target instanceof RifidiService) {
			((RifidiService) target).setID(serviceID);
		}
		for (Attribute attribute : attributes) {
			try {
				setAttribute(attribute);
			} catch (AttributeNotFoundException e) {
				logger.error("That should not happen: " + e);
			} catch (InvalidAttributeValueException e) {
				logger.error("That should not happen: " + e);
			} catch (MBeanException e) {
				logger.error("That should not happen: " + e);
			} catch (ReflectionException e) {
				logger.error("That should not happen: " + e);
			}
		}
	}

	/**
	 * private String
	 * 
	 * @param registration
	 *            the registration to set
	 */
	public void setRegistration(ServiceRegistration registration) {
		this.registration = registration;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		if (nameToProperty.containsKey(attribute)) {
			try {
				Method method = target.getClass().getMethod("get" + attribute);
				String res = (String) method.invoke(target);
				return res;
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

		}
		throw new AttributeNotFoundException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
	 */
	@Override
	public AttributeList getAttributes(String[] attributes) {
		AttributeList ret = new AttributeList();
		for (String obj : attributes) {
			try {
				ret.add(new Attribute(obj, getAttribute(obj)));
			} catch (AttributeNotFoundException e) {
				logger.error(e);
			} catch (MBeanException e) {
				logger.error(e);
			} catch (ReflectionException e) {
				logger.error(e);
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
		MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[nameToProperty
				.size()];
		int counter = 0;
		// /assemble attributes
		for (String name : nameToProperty.keySet()) {
			Property prop = nameToProperty.get(name);
			attrs[counter] = new MBeanAttributeInfo(name, "java.lang.String",
					"Property " + prop.description(), true, // isReadable
					prop.writable(), // isWritable
					false); // isIs
			counter++;
		}
		counter = 0;
		MBeanOperationInfo[] opers = new MBeanOperationInfo[nameToOperation
				.size()];
		// assemble operations
		for (String name : nameToOperation.keySet()) {
			Operation oper = nameToOperation.get(name);
			opers[counter] = new MBeanOperationInfo(name, oper.description(),
					null, // no parameters
					"void", MBeanOperationInfo.ACTION);
			counter++;
		}

		return new MBeanInfo(this.getClass().getName(),
				"Property Manager MBean", attrs, null, // constructors
				opers, null); // notifications
	}

	/*
	 * } catch (MBeanRegistrationException e) { (non-Javadoc)
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
			logger.warn("Trie to call non existend operation " + actionName
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
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {
		if (nameToProperty.containsKey(attribute.getName())) {
			if (target == null) {
				attributes.add(attribute);
				return;
			} else {
				try {
					Method method = target.getClass().getMethod(
							"set" + attribute.getName(), String.class);
					method.invoke(target, attribute.getValue());
					return;
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
			}
		}
		throw new AttributeNotFoundException();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.management.DynamicMBean#setAttributes(javax.management.AttributeList
	 * )
	 */
	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		AttributeList ret = new AttributeList();
		for (Object obj : attributes) {
			if (target == null) {
				attributes.add((Attribute) obj);
			} else {
				try {
					setAttribute((Attribute) obj);
					ret.add(new Attribute(((Attribute) obj).getName(),
							getAttribute(((Attribute) obj).getName())));
				} catch (AttributeNotFoundException e) {
					logger.error(e);
				} catch (InvalidAttributeValueException e) {
					logger.error(e);
				} catch (MBeanException e) {
					logger.error(e);
				} catch (ReflectionException e) {
					logger.error(e);
				}
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.Configuration#destroy()
	 */
	@Override
	public void destroy() {
		if (registration != null) {
			registration.unregister();
			return;
		}
		logger
				.error("Tried to unregister service that was not yet registered!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.Configuration#getAttributes()
	 */
	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> ret = new HashMap<String, String>();
		try {
			for (String name : nameToProperty.keySet()) {
				ret.put(name, (String) getAttribute(name));
			}
			return ret;
		} catch (AttributeNotFoundException e) {
			logger.error("Problem getting property: " + e);
		} catch (MBeanException e) {
			logger.error("Problem getting property: " + e);
		} catch (ReflectionException e) {
			logger.error("Problem getting property: " + e);
		}
		return Collections.emptyMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		DefaultConfigurationImpl config = new DefaultConfigurationImpl();
		config.nameToOperation = new HashMap<String, Operation>(nameToOperation);
		config.nameToProperty = new HashMap<String, Property>(nameToProperty);
		config.factoryID = factoryID;
		config.clazz = clazz;
		return config;
	}
}

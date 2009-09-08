/*
 * 
 * RifidiService.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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
import org.osgi.framework.ServiceRegistration;
import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Operation;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;

/**
 * Interface for rifidi services.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class RifidiService {

	/** The object that allows us to unregister the OSGi service */
	private ServiceRegistration serviceRegistration;
	/** The ID for this service */
	private volatile String ID;
	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(RifidiService.class);
	/** Names of properties mapped to their annotations */
	protected final Map<String, Property> nameToProperty;
	/** Names of properties mapped to their getter */
	protected final Map<String, Method> nameToMethod;
	/** Names of operations mapped to their annotations */
	protected final Map<String, Operation> nameToOperation;

	/**
	 * Constructor.
	 */
	public RifidiService() {
		this.nameToProperty = new HashMap<String, Property>();
		this.nameToOperation = new HashMap<String, Operation>();
		this.nameToMethod = new HashMap<String, Method>();
		Class<?> clazz = this.getClass();
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
					nameToMethod.put(method.getName().substring(3), method);
				}
			}
		}
	}

	/**
	 * Get an attribute.
	 * 
	 * @param attributeName
	 * @return
	 */
	public Attribute getAttribute(String attributeName) {
		try {
			Object value = nameToMethod.get(attributeName).invoke(this);
			return new Attribute(attributeName, value);
		} catch (IllegalArgumentException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return null;
	}

	/**
	 * Get all attributes and their values.
	 * 
	 * @return
	 */
	public AttributeList getAttributes() {
		AttributeList ret = new AttributeList();
		try {
			for (String name : nameToMethod.keySet()) {
				Object value = nameToMethod.get(name).invoke(this);
				ret.add(new Attribute(name, value));
			}
			return ret;
		} catch (IllegalArgumentException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return ret;
	}

	/**
	 * Get a list of attributes.
	 * 
	 * @param attributeNames
	 * @return
	 */
	public AttributeList getAttributes(Collection<String> attributeNames) {
		AttributeList ret = new AttributeList();
		try {
			for (String name : nameToMethod.keySet()) {
				if (attributeNames.contains(name)) {
					Object value = nameToMethod.get(name).invoke(this);
					ret.add(new Attribute(name, value));
				}
			}
			return ret;
		} catch (IllegalArgumentException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return ret;
	}

	/**
	 * Private helper method so I don't have to duplicate JMS Notification calls
	 * 
	 * @param attribute
	 * @throws AttributeNotFoundException
	 * @throws InvalidAttributeValueException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	private void _setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {
		// for now we go through the individual setters
		if (nameToProperty.containsKey(attribute.getName())) {
			try {
				Property prop = nameToProperty.get(attribute.getName());
				Method method = this.getClass().getMethod(
						"set" + attribute.getName(),
						PropertyType.getClassFromType(prop.type()));

				// if attribute is a string, but is supposed to be another
				// type, convert it
				if ((attribute.getValue() instanceof String)
						&& prop.type() != PropertyType.PT_STRING) {
					attribute = new Attribute(attribute.getName(),
							PropertyType.convert((String) attribute.getValue(),
									prop.type()));
				}
				method.invoke(this, attribute.getValue());
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
		logger.warn("Unknown attribute: " + attribute);
		// throw new AttributeNotFoundException();
	}

	/**
	 * Set an attribute.
	 * 
	 * @param attribute
	 */
	public void setAttribute(Attribute attribute) {
		try {
			_setAttribute(attribute);
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

	/**
	 * Set several attributes.
	 * 
	 * @param attributes
	 */
	public void setAttributes(AttributeList attributes) {
		for (Attribute attribute : attributes.asList()) {
			try {
				_setAttribute(attribute);
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

	/**
	 * Id of the service. This ID has be unique for the whole application.
	 * 
	 * @return
	 */
	public String getID() {
		return ID;
	}

	/**
	 * Set the ID of the service. This can only be done once. Further calls have
	 * to be ignored.
	 * 
	 * @param id
	 */
	public void setID(String id) {
		if (ID == null) {
			this.ID = id;
		} else {
			logger.warn("Already set the ID for this Service " + ID);
		}
	}

	/**
	 * This method should be called to register the service to OSGi
	 * 
	 * @param context
	 *            The BundleContext to use to register the service
	 * @param interfaces
	 *            The Interfaces to register the service under
	 * @param params
	 *            OSGi service params
	 */
	protected void register(final BundleContext context,
			final Collection<String> interfaces,
			final Map<String, String> params) {
		interfaces.add(RifidiService.class.getName());
		String[] serviceInterfaces = new String[interfaces.size()];
		serviceInterfaces = interfaces.toArray(serviceInterfaces);
		Hashtable<String, String> parms = new Hashtable<String, String>();
		parms.put("serviceid", getID());
		parms.putAll(params);
		this.serviceRegistration = context.registerService(serviceInterfaces,
				this, parms);
	}

	/**
	 * Unregister this service from the OSGi registry. Should normally be called
	 * when the RifidiService is destroyed
	 */
	protected void unregister() {
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		} else {
			logger.warn("Service has not been registered " + ID);
		}
	}

	/**
	 * Get the MBean info.
	 * 
	 * @return
	 */
	public abstract MBeanInfo getMBeanInfo();

	/**
	 * Destroy the RifidiService. Should normally at least unregister this
	 * service
	 */
	public abstract void destroy();
}

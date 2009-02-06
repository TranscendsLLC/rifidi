/**
 * 
 */
package org.rifidi.configuration.mbeans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.annotations.JMXMBean;
import org.rifidi.configuration.annotations.Name;
import org.rifidi.configuration.annotations.Operation;
import org.rifidi.configuration.annotations.Property;

/**
 * Container class for JMX.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class RifidiDynamicMBean implements DynamicMBean {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(RifidiDynamicMBean.class);
	/** Names of properties mapped to their annotations */
	private Map<String, Property> nameToProperty;
	/** Names of operations mapped to their annotations */
	private Map<String, Operation> nameToOperation;
	/** The object that gets serviced. */
	private Object serviced;
	/** Name of the object. */
	private Method name;

	/**
	 * Constructor.
	 * 
	 * @param nameToProperty
	 * @param nameToOperation
	 * @param serviced
	 */
	public RifidiDynamicMBean(Object service) {
		this.nameToProperty = new HashMap<String, Property>();
		this.nameToOperation = new HashMap<String, Operation>();
		this.serviced = service;

		if (service.getClass().isAnnotationPresent(JMXMBean.class)) {
			// check method annotations
			for (Method method : service.getClass().getMethods()) {
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
				// scan for name annotation
				if (method.isAnnotationPresent(Name.class)) {
					name = method;
				}
			}
		}
	}

	public String getName() {
		if (name != null) {
			try {
				return (String) name.invoke(serviced);
			} catch (IllegalArgumentException e) {
				logger.error("Can't get the name: " + e);
			} catch (IllegalAccessException e) {
				logger.error("Can't get the name: " + e);
			} catch (InvocationTargetException e) {
				logger.error("Can't get the name: " + e);
			}
		}
		return "Missing name!";
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
				Method method = serviced.getClass()
						.getMethod("get" + attribute);
				String res = (String) method.invoke(serviced);
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
			} catch (MBeanRegistrationException e) {
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
				Method method = serviced.getClass().getMethod(actionName);
				method.invoke(serviced);
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
					+ " on " + serviced.getClass());
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
			try {
				Method method = serviced.getClass().getMethod(
						"set" + attribute.getName(), String.class);
				method.invoke(serviced, attribute.getValue());
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
		return ret;
	}

}

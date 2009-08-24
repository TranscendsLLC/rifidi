/**
 * 
 */
package org.rifidi.edge.core.configuration.mbeanstrategies;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.management.Descriptor;
import javax.management.JMX;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.SimpleType;

import org.rifidi.edge.core.configuration.MBeanInfoStrategy;
import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Operation;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class AnnotationMBeanInfoStrategy implements MBeanInfoStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.MBeanInfoStrategy#getMBeanInfo(java
	 * .lang.Class)
	 */
	@Override
	public OpenMBeanInfoSupport getMBeanInfo(Class<?> clazz) {
		/** Names of properties mapped to their annotations */
		Map<String, Property> nameToProperty = new HashMap<String, Property>();
		/** Names of properties mapped to their getter */
		Map<String, Method> nameToMethod = new HashMap<String, Method>();
		/** Names of operations mapped to their annotations */
		Map<String, Operation> nameToOperation = new HashMap<String, Operation>();

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
		OpenMBeanAttributeInfo[] attrs = new OpenMBeanAttributeInfo[nameToProperty
				.size()];
		int counter = 0;
		// assemble attributes
		for (String name : nameToProperty.keySet()) {

			// get the property annotation
			Property prop = nameToProperty.get(name);

			// build the descriptor
			Descriptor descriptor = new DescriptorSupport();
			descriptor.setField("immutableInfo", "true");
			descriptor.setField("displayName", prop.displayName());
			if (!prop.maxValue().equals("")) {
				descriptor.setField(JMX.MAX_VALUE_FIELD, PropertyType.convert(
						prop.maxValue(), prop.type()));
			}
			if (!prop.minValue().equals("")) {
				descriptor.setField(JMX.MIN_VALUE_FIELD, PropertyType.convert(
						prop.minValue(), prop.type()));
			}
			if (!prop.category().equals("")) {
				descriptor
						.setField("org.rifidi.edge.category", prop.category());
			}
			if (!prop.defaultValue().equals("")) {
				descriptor.setField(JMX.DEFAULT_VALUE_FIELD, PropertyType
						.convert(prop.defaultValue(), prop.type()));
			}
			descriptor
					.setField("org.rifidi.edge.ordervalue", prop.orderValue());

			attrs[counter] = new OpenMBeanAttributeInfoSupport(name, prop
					.description(), PropertyType.getOpenType(prop.type()),
					true, prop.writable(), false, descriptor);
			counter++;
		}
		counter = 0;
		OpenMBeanOperationInfo[] opers = new OpenMBeanOperationInfo[nameToOperation
				.size()];
		// assemble operations
		for (String name : nameToOperation.keySet()) {
			Operation oper = nameToOperation.get(name);
			opers[counter] = new OpenMBeanOperationInfoSupport(name, oper
					.description(), null, SimpleType.VOID,
					MBeanOperationInfo.ACTION);
			counter++;
		}

		return new OpenMBeanInfoSupport(this.getClass().getName(),
				"Property Manager MBean", attrs, null, opers, null);
	}

}

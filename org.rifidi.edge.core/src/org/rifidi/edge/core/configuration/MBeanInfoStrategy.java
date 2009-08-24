/**
 * 
 */
package org.rifidi.edge.core.configuration;

import javax.management.openmbean.OpenMBeanInfoSupport;

/**
 * Strategy pattern used to create an MBeanInfo for a given class.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface MBeanInfoStrategy {
	/**
	 * Process the given class to create an MBeanInfo.
	 * 
	 * @param clazz
	 * @return
	 */
	OpenMBeanInfoSupport getMBeanInfo(Class<?> clazz);
}

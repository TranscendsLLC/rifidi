/**
 * 
 */
package org.rifidi.edge.core.app.api.resources.jms;

import org.rifidi.edge.core.app.api.resources.ResourceDescription;

/**
 * A description of a JMS Resource
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class JMSResourceDescription extends ResourceDescription implements
		JMSProperties {

	/**
	 * 
	 * @return The name of the destination
	 */
	String getDestinationName() {
		return getProperty(DESTINATION);
	}

	/**
	 * Returns either 'queue' or 'topic'
	 * 
	 * @return The destination type
	 */
	String getDesinationType() {
		return getProperty(DESTINATION_TYPE);
	}

}

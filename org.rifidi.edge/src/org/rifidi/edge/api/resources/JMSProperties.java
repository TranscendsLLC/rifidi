/**
 * 
 */
package org.rifidi.edge.api.resources;

/**
 * Properties used to configure a JMS Resource
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface JMSProperties {

	/** The name of the destination */
	public static final String DESTINATION = "Destination";
	/** The type of the destination (queue or topic) */
	public static final String DESTINATION_TYPE = "DestinationType";

}

package org.rifidi.edge.client.model.sal.preferences;

/**
 * Preference names and default values
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EdgeServerPreferences {
	public static final String EDGE_SERVER_IP = "org.rifidi.edge.client.server.ip";
	public static final String EDGE_SERVER_IP_DEFAULT = "127.0.0.1";
	public static final String EDGE_SERVER_PORT_RMI = "org.rifidi.edge.client.server.port.rmi";
	public static final String EDGE_SERVER_PORT_RMI_DEFAULT = "1098";
	public static final String EDGE_SERVER_PORT_JMS = "org.rifidi.edge.client.server.port.jms";
	public static final String EDGE_SERVER_PORT_JMS_DEFAULT = "1099";
	public static final String EDGE_SERVER_JMS_QUEUE = "org.rifidi.edge.client.server.jms.notifications";
	public static final String EDGE_SERVER_JMS_QUEUE_DEFAULT = "org.rifidi.edge.external.notifications";
	public static final String EDGE_SERVER_JMS_QUEUE_TAGS = "org.rifidi.edge.client.server.jms.tags";
	public static final String EDGE_SERVER_JMS_QUEUE_TAGS_DEFAULT = "org.rifidi.edge.external.tags";
	public static final String EDGE_SERVER_RMI_USERNAME = "org.rifidi.edge.client.rmi.username";
	public static final String EDGE_SERVER_RMI_USERNAME_DEFAULT = "admin";
	public static final String EDGE_SERVER_RMI_PASSWORD = "org.rifidi.edge.client.rmi.password";
	public static final String EDGE_SERVER_RMI_PASSWORD_DEFAULT = "admin";
}

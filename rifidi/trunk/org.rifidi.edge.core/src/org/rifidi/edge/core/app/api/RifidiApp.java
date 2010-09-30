package org.rifidi.edge.core.app.api;

import java.util.Properties;

import org.rifidi.edge.core.services.esper.EsperManagementService;

/**
 * This is the interface for a RifidiApp. A RifidiApp provides some base set of
 * services to applications running on the Rifidi edge server. This interface
 * should not be implemented directlry. Instead, clients should extend the
 * AbstractRifidiApp
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface RifidiApp extends Comparable<RifidiApp>,
		RifidiAppCommonProperties {

	/**
	 * Each application has a name that is unique within it's group
	 * 
	 * @return The name of the application
	 */
	String getName();

	/**
	 * A group is a set of applications within some logical domain. For example,
	 * the Acme company maye have one set of applications that deal with dock
	 * door receiving, and another set of applications that deal with inventory
	 * management.
	 * 
	 * @return The group the application belongs to.
	 */
	String getGroup();

	/**
	 * Each application has a state that is either running or stopped.
	 * 
	 * @return The state of the application
	 */
	AppState getState();

	/**
	 * Used to set the esper service for this application
	 * 
	 * @param esperService
	 *            the esperService to set
	 */
	void setEsperService(EsperManagementService esperService);

	/**
	 * Used to set the Properties for this application Set the properties for
	 * this application
	 * 
	 * @param properties
	 */
	void setAppProperties(Properties properties);

	/**
	 * If this method returns true, the application will not be started when it
	 * is loaded.
	 * 
	 * @return
	 */
	boolean lazyStart();

	/**
	 * The initialize method can be used to do any work that needs to be done
	 * right before the start method is called. It can be used to set properties
	 * read in from the properties file.
	 */
	void initialize();

	/**
	 * This method is called to start the application.
	 */
	void start();

	/**
	 * This method is called to stop the application.
	 */
	void stop();

}

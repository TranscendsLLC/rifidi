package org.rifidi.edge.core.app.api;

import java.util.Properties;

import org.rifidi.edge.core.services.esper.EsperManagementService;

public interface RifidiApp extends Comparable<RifidiApp>,
		RifidiAppCommonProperties {

	/**
	 * 
	 * @return The name of the application
	 */
	String getName();

	/**
	 * 
	 * @return The name of the application
	 */
	String getGroup();

	/**
	 * @return The state of the application
	 */
	AppState getState();

	/**
	 * @param esperService
	 *            the esperService to set
	 */
	void setEsperService(EsperManagementService esperService);

	/**
	 * Set the properties for this application
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
	 * right before the start method is called
	 */
	void initialize();

	/**
	 * This method is called when the application is started.
	 */
	void start();

	/**
	 * This method is called when the application is stopped. By default it
	 * destroys all active statements.
	 */
	void stop();

}

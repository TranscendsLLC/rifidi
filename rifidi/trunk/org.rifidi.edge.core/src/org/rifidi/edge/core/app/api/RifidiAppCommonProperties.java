package org.rifidi.edge.core.app.api;

/**
 * This interface specifies property names that are common across all Rifidi
 * Apps. These properties can be specified in the properties file for each
 * application
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface RifidiAppCommonProperties {
	/**
	 * The property for whether or not the application shoul automatically start
	 * when it is loaded.
	 */
	public static final String LAZY_START = "LazyStart";
}

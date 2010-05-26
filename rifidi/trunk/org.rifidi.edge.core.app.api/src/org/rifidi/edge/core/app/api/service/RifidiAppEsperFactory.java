/**
 * 
 */
package org.rifidi.edge.core.app.api.service;

import java.util.List;

/**
 * A RifidiAppEsperFactory builds esper statements for AppServices to use.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface RifidiAppEsperFactory {

	/**
	 * Use this method to create the necessary statements. Take care that all
	 * windows have unique names, since windows are shared globally across all
	 * rifidi applications.
	 * 
	 * @return A list of esper statements as strings
	 */
	List<String> createStatements();

	/**
	 * This method should return a select statement that can be hooked up to a
	 * StatementAwareUpdateListener
	 * 
	 * @return
	 */
	String createQuery();

}

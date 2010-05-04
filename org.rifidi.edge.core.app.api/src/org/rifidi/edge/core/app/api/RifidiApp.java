/**
 * 
 */
package org.rifidi.edge.core.app.api;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.rifidi.edge.core.services.esper.EsperManagementService;

import com.espertech.esper.client.EPStatement;

/**
 * This is a base class for all Rifidi Applications to extend.
 * 
 * Implementors must inject the EsperManagementService.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class RifidiApp {

	/** All the esper statements that have been defined so far */
	protected final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();
	/** Esper service */
	protected volatile EsperManagementService esperService;

	/**
	 * Called by spring
	 * 
	 * @param esperService
	 *            the esperService to set
	 */
	public void setEsperService(EsperManagementService esperService) {
		this.esperService = esperService;
	}

	/**
	 * Clients should use the start method to create esper statements.
	 * Statements should be saved in the 'statements' set.
	 */
	public abstract void start();

	/**
	 * The stop method iterates through the set of Esper Statements and destroys
	 * them.
	 */
	public void stop() {
		for (EPStatement statement : statements) {
			statement.destroy();
		}
	}

}

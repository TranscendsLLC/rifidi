/**
 * 
 */
package org.rifidi.edge.core.app.api;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.esper.EsperManagementService;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This is a base class for all Rifidi Applications to extend.
 * 
 * EsperManagementService.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class RifidiApp {

	/** All the esper statements that have been defined so far */
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();
	/** Esper service */
	private volatile EsperManagementService esperService;
	/** The name of the application */
	private final String name;
	/** The state of the application */
	private AppState state;
	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(RifidiApp.class);

	/**
	 * Constructor for a RifidiApp
	 * 
	 * @param name
	 *            The name of the application
	 */
	public RifidiApp(String name) {
		this.name = name;
		this.state = AppState.STOPPED;
	}

	/**
	 * 
	 * @return The name of the application
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The state of the application
	 */
	public AppState getState() {
		return state;
	}

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
	 * Subclasses can use this method to get a hold of the EPAdminsistrator for
	 * advanced esper configuration.
	 * 
	 * @return
	 */
	protected EPAdministrator getEPAdministrator() {
		return this.esperService.getProvider().getEPAdministrator();
	}
	
	/**
	 * Subclasses can use this method to get a hold of the EPAdminsistrator for
	 * advanced esper configuration.
	 * 
	 * @return
	 */
	protected EPRuntime getEPRuntime() {
		return this.esperService.getProvider().getEPRuntime();
	}

	/**
	 * This method is called when the application is started. Subclasses should
	 * not override this method!
	 */
	public void start() {
		if (this.esperService == null) {
			throw new IllegalStateException("Application cannot be started "
					+ "until EsperManagementService has been injected");
		}
		try {
			_start();
		} catch (Exception e) {
			logger.warn("Cannot start " + this + ". " + e.getMessage());
			return;
		}
		this.state = AppState.STARTED;
	}

	/**
	 * Subclasses should use this method to add statements.
	 */
	protected void _start() {

	}

	/**
	 * This method is called when the application is stopped. By default it
	 * destroys all active statements. Subclasses should not normally override
	 * this method!
	 */
	public void stop() {
		synchronized (this) {

			for (EPStatement statement : statements) {
				try {
					statement.destroy();
				} catch (Exception e) {
					logger.debug("Exception when destroying the statement: "
							+ statement.getText());
				}
			}
			try {
				_stop();
			} catch (Exception e) {

			}
			this.state = AppState.STOPPED;
		}
	}

	/**
	 * Subclasses should override this method do any work they need to do when
	 * the application is stopping.
	 */
	protected void _stop() {

	}

	/**
	 * Add a statement to esper.
	 * 
	 * @param esperStatement
	 *            The statement to add
	 * @return The name of the staement.
	 */
	protected String addStatement(String esperStatement) {
		EPStatement statement = this.esperService.getProvider()
				.getEPAdministrator().createEPL(esperStatement);
		return statement.getName();

	}

	/**
	 * Add a statement to esper along with a listener to the statement.
	 * 
	 * @param esperStatement
	 *            The statement to add
	 * @param listener
	 *            The listener to the statement
	 * @return The name of the statement
	 */
	protected String addStatement(String esperStatement,
			StatementAwareUpdateListener listener) {
		EPStatement statement = this.esperService.getProvider()
				.getEPAdministrator().createEPL(esperStatement);
		statement.addListener(listener);
		statements.add(statement);
		return statement.getName();
	}

	/**
	 * Run an on-demand query.
	 * 
	 * @param esperQuery
	 *            The query to run
	 * @return The result of the query
	 */
	protected EPOnDemandQueryResult executeQuery(String esperQuery) {
		return esperService.getProvider().getEPRuntime().executeQuery(
				esperQuery);
	}

	/**
	 * Destroy a statement with a given name
	 * 
	 * @param statementName
	 *            The name of the statement to destroy.
	 */
	protected void destroyStatement(String statementName) {
		EPStatement statement = this.esperService.getProvider()
				.getEPAdministrator().getStatement(statementName);
		if (statement != null) {
			statement.destroy();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Rifidi App: " + name + "(" + state + ")";
	}
}

/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.notification.AppStartedEvent;
import org.rifidi.edge.notification.AppStoppedEvent;
import org.rifidi.edge.services.EsperManagementService;
import org.rifidi.edge.util.RifidiEdgeHelper;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This is a base class for all Rifidi Applications to extend.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractRifidiApp implements RifidiApp {

	/** All the esper statements that have been defined so far */
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();
	/** Any additional events this app adds to the runtime. */
	private final Set<String> additionalEvents = new CopyOnWriteArraySet<String>();
	/** Esper service */
	private EsperManagementService esperService;
	/** The group this application is a part of */
	private final String group;
	/** The name of the application */
	private final String name;
	/** The state of the application */
	private AppState state;
	/** A unique String ID for this app */
	private final String hashString;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(AbstractRifidiApp.class);
	/** The properties for this application */
	private Properties properties;
	/** The bundle context for the application (not the API) */
	private BundleContext bundleContext;
	/** A reference to the command provider */
	private ServiceRegistration commandProviderService;
	/** This is a map of ReadZones read in from the readzones directory */
	public HashMap<String, ReadZone> readZones;

	/**
	 * Constructor for a AbstractRifidiApp
	 * 
	 * @param group
	 *            the group this application is a part of
	 * @param name
	 *            The name of the application
	 */
	public AbstractRifidiApp(String group, String name) {
		this.name = name;
		this.group = group;
		this.hashString = group + ":" + name;
		this.state = AppState.STOPPED;
		this.properties = new Properties();
		this.readZones = new HashMap<String, ReadZone>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.api.RifidiApp#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.api.RifidiApp#getGroup()
	 */
	@Override
	public String getGroup() {
		return group;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.RifidiApp#getState()
	 */
	@Override
	public AppState getState() {
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.RifidiApp#setEsperService(org.rifidi.edge.services.EsperManagementService)
	 */
	public void setEsperService(EsperManagementService esperService) {
		if (this.state == AppState.STARTED) {
			throw new IllegalStateException(
					"Cannot set esper while app is started");
		}
		this.esperService = esperService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.app.api.RifidiApp#setAppProperties(org.rifidi.edge
	 * .api.service.appmanager.RifidiAppProperties)
	 */
	public void setAppProperties(Properties properties) {
		if (this.state == AppState.STARTED) {
			throw new IllegalStateException(
					"Cannot set properties while app is started");
		}
		this.properties = properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.osgi.context.BundleContextAware#setBundleContext(
	 * org.osgi.framework.BundleContext)
	 */
//	@Override
//	public void setBundleContext(BundleContext arg0) {
//		this.bundleContext = arg0;
//	}

	/**
	 * Subclasses should override this method if they want to automatically
	 * register a command provider for their app
	 * 
	 * @return
	 */
	protected CommandProvider getCommandProvider() {
		return null;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.RifidiApp#initialize()
	 */
	@Override
	public void initialize() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.RifidiApp#lazyStart()
	 */
	@Override
	public boolean lazyStart() {
		String lazyStart = getProperty(LAZY_START, "false");
		return Boolean.parseBoolean(lazyStart);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.RifidiApp#start()
	 */
	@Override
	public final void start() {
		if (this.esperService == null) {
			throw new IllegalStateException("Application cannot be started "
					+ "until EsperManagementService has been injected");
		}
		if (this.properties == null) {
			throw new IllegalStateException("Application cannot be started "
					+ "until properties have been injected");
		}
		try {
			logger.info("Starting App: " + getName());
			CommandProvider commandProvider = getCommandProvider();
			if (commandProvider != null) {
				this.commandProviderService = bundleContext.registerService(
						CommandProvider.class.getCanonicalName(),
						getCommandProvider(), null);
			}
			readZones = RifidiEdgeHelper.getReadZones(getGroup());
			
			_start();
			AppStartedEvent event = new AppStartedEvent();
			event.setGroup(this.getGroup());
			event.setName(this.getName());
			this.getEPRuntime().sendEvent(event);
		} catch (Exception e) {
			logger.warn("Cannot start " + this + ". ", e);
			return;
		}
		this.state = AppState.STARTED;
	}

	/**
	 * Subclasses should use this method to add statements.
	 */
	protected void _start() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.RifidiApp#stop()
	 */
	@Override
	public final void stop() {
		synchronized (this) {

			for (EPStatement statement : statements) {
				try {
					destroyStatement(statement.getName());
				} catch (Exception e) {
					logger.debug("Exception when destroying the statement: "
							+ statement.getText());
				}
			}
			for (String customEvent : this.additionalEvents) {
				try {
					getEPAdministrator().getConfiguration().removeEventType(
							customEvent, true);
				} catch (Exception e) {
					logger.debug("Exception when removing this eventtype: "
							+ customEvent);
				}
			}
			try {
				if (commandProviderService != null) {
					commandProviderService.unregister();
				}
				_stop();
				AppStoppedEvent event = new AppStoppedEvent();
				event.setGroup(this.getGroup());
				event.setName(this.getName());
				this.getEPRuntime().sendEvent(event);
			} catch (Exception e) {

			}
			this.state = AppState.STOPPED;
		}
	}

	/**
	 * Subclasses should override this method do any work they need to do when
	 * the application is stopping. Make sure to unregister any listeners here.
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
	protected final String addStatement(String esperStatement) {
		try {
			//Small sleep statement to give the esper statement a chance to propogate
			Thread.sleep(100);
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		EPStatement statement = getEPAdministrator().createEPL(esperStatement);
		statements.add(statement);
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
	protected final String addStatement(String esperStatement,
			StatementAwareUpdateListener listener) {
		try {
			//Small sleep statement to give the esper statement a chance to propogate
			Thread.sleep(100);
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		EPStatement statement = getEPAdministrator().createEPL(esperStatement);		
		statement.addListener(listener);
		statements.add(statement);
		return statement.getName();
	}

	/**
	 * Adds a new event type to the esper configuration. The name of the event
	 * type is clazz.getSimpleName(). Events will be automatically removed when
	 * the application is stopped.
	 * 
	 * @param clazz
	 *            The class of the event to add
	 */
	protected final void addEventType(Class<?> clazz) {
		String eventName = clazz.getSimpleName();
		addEventType(eventName, clazz);
	}

	/**
	 * Adds a new event type to the esper configuration. The name of the event
	 * type is clazz.getSimpleName(). Events will be automatically removed when
	 * the application is stopped.
	 * 
	 * @param clazz
	 *            The class of the event to add
	 */
	protected final void addEventType(String eventName, Class<?> clazz) {
		getEPAdministrator().getConfiguration().addEventType(eventName, clazz);
		this.additionalEvents.add(eventName);
	}

	/**
	 * Add a new Map event type to the esper configuration. Events will be
	 * automatically removed when the application is stopped.
	 * 
	 * @param eventName
	 * @param description
	 */
	protected final void addEventType(String eventName,
			Map<String, Object> description) {
		getEPAdministrator().getConfiguration().addEventType(eventName,
				description);
		this.additionalEvents.add(eventName);
	}

	/**
	 * This method sends the given event to the Esper Runtime.
	 * 
	 * @param event
	 */
	protected final void sendEvent(Object event) {
		getEPRuntime().sendEvent(event);
	}

	/**
	 * Run an on-demand query.
	 * 
	 * @param esperQuery
	 *            The query to run
	 * @return The result of the query
	 */
	protected final EPOnDemandQueryResult executeQuery(String esperQuery) {
		return getEPRuntime().executeQuery(esperQuery);
	}

	/**
	 * Destroy a statement with a given name
	 * 
	 * @param statementName
	 *            The name of the statement to destroy.
	 */
	protected final void destroyStatement(String statementName) {
		EPStatement statement = getEPAdministrator()
				.getStatement(statementName);
		if (statement != null) {
			logger.debug("Destroy Esper Statement: " + statement.getText());
			statement.destroy();
		}
	}

	/**
	 * Gets value of the property with the supplied name. If the value is null,
	 * then the default value will be returned. If both the value and the
	 * defaultValue is null, a null pointer exeption will be thrown.
	 * 
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	protected String getProperty(String propertyName, String defaultValue) {
		String prop = properties.getProperty(propertyName, defaultValue);
		if (prop != null) {
			return prop;
		}
		throw new NullPointerException("Property with name: " + propertyName
				+ " not found in list of properties for application "
				+ getName());

	}

	

	/**
	 * RifidiApps can store read zone configurations in their readzones
	 * directory. These are property files which describe a read zone. The app
	 * can then use the readzone in app services or in their own esper
	 * statements.
	 * 
	 * Please see {@link ReadZone} for a list of valid properties for the
	 * properties file
	 * 
	 * @return
	 */
	protected HashMap<String, ReadZone> getReadZones() {
		return new HashMap<String, ReadZone>(readZones);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Rifidi App: " + group + ":" + name + " (" + state + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof AbstractRifidiApp))
			return false;
		AbstractRifidiApp that = (AbstractRifidiApp) obj;
		return this.hashString.equals(that.hashString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashString.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RifidiApp o) {
		AbstractRifidiApp that = (AbstractRifidiApp) o;
		return this.hashString.compareTo(that.hashString);
	}

}

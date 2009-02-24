/**
 * 
 */
package org.rifidi.edge.core.readersession;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.exceptions.NonExistentCommandFactoryException;
import org.rifidi.edge.core.exceptions.NonExistentReaderConfigurationException;
import org.rifidi.edge.core.internal.ReaderSession;
import org.rifidi.edge.core.internal.impl.ReaderSessionImpl;
import org.rifidi.edge.core.readers.AbstractReaderConfiguration;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderSessionDAOImpl implements ReaderSessionDAO {
	/** Reference to the bundle context. */
	private BundleContext context;
	/** Template for jms messages. */
	private JmsTemplate template;
	/** Destination for jms messages. */
	private Destination destination;
	/** Counter for session ids. */
	private Integer counter = 0;
	/** Set of readers that is currently available. */
	private Map<String, AbstractReaderConfiguration<?>> readerConfigurationsById;
	/** Set of currently available commands. */
	private Map<String, AbstractCommandConfiguration<?>> commandFactoriesById;
	/** Executor for reader sessions. */
	private ThreadPoolExecutor executor;
	/** Currently available reader sessions by reader configs. */
	private Map<String, List<ReaderSession>> readerSessionByReaderConfig;
	/** Currently available reader sessions by command factories. */
	private Map<String, List<ReaderSession>> readerSessionByCommandFactory;
	/** Currently created reader sessions. */
	private Map<String, ReaderSession> readerSessionByName;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(ReaderSessionDAOImpl.class);

	/**
	 * Constructor.
	 */
	public ReaderSessionDAOImpl() {
		readerConfigurationsById = new HashMap<String, AbstractReaderConfiguration<?>>();
		commandFactoriesById = new HashMap<String, AbstractCommandConfiguration<?>>();
		readerSessionByCommandFactory = new HashMap<String, List<ReaderSession>>();
		readerSessionByReaderConfig = new HashMap<String, List<ReaderSession>>();
		readerSessionByName = new HashMap<String, ReaderSession>();
		executor = new ThreadPoolExecutor(5, 15, 200, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.readersession.ReaderSessionManagement#
	 * createAndStartReaderSession(java.lang.String, java.lang.String)
	 */
	@Override
	public String createAndStartReaderSession(String readerConfigurationID,
			String commandFactoryID) throws NonExistentCommandFactoryException,
			NonExistentReaderConfigurationException {
		synchronized (this) {
			AbstractReaderConfiguration<?> reader = readerConfigurationsById
					.get(readerConfigurationID);
			AbstractCommandConfiguration<?> command = commandFactoriesById
					.get(commandFactoryID);
			if (reader == null) {
				throw new NonExistentReaderConfigurationException(
						"Tried to use a reader that doesn't exist: "
								+ readerConfigurationID);
			}
			if (command == null) {
				throw new NonExistentCommandFactoryException(
						"Tried to use a command that doesn't exist: "
								+ commandFactoryID);
			}
			ReaderSessionImpl session = new ReaderSessionImpl();
			session.setReaderFactory(reader);
			session.setCommmandFactory(command);
			session.setDestination(destination);
			session.setTemplate(template);
			Dictionary<String, String> params = new Hashtable<String, String>();
			params.put("id", counter.toString());
			readerSessionByName.put(Integer.toString(counter), session);
			session.setID(Integer.toString(counter));
			counter++;
			session.setRegistration(context.registerService(ReaderSession.class
					.getName(), session, params));

			if (readerSessionByCommandFactory.get(commandFactoryID) == null) {
				readerSessionByCommandFactory.put(commandFactoryID,
						new ArrayList<ReaderSession>());
			}
			if (readerSessionByReaderConfig.get(readerConfigurationID) == null) {
				readerSessionByReaderConfig.put(readerConfigurationID,
						new ArrayList<ReaderSession>());
			}
			readerSessionByCommandFactory.get(commandFactoryID).add(session);
			readerSessionByReaderConfig.get(readerConfigurationID).add(session);
			// TODO: store future
			Future<?> future = executor.submit(session);
			return session.getID();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.ReaderSessionDAO#stopReaderSession
	 * (java.lang.String)
	 */
	@Override
	public void stopReaderSession(String sessionID) {
		synchronized (this) {
			ReaderSession session = readerSessionByName.remove(sessionID);
			if (session != null) {
				session.stop();

				// remove from OSGi
				session.destroy();

				// remove from readerSessionbyCommanFactory
				for (List<ReaderSession> sessionList : readerSessionByCommandFactory
						.values()) {
					if (sessionList.contains(session)) {
						sessionList.remove(session);
					}
				}

				// remove from readerSessionByReaderConfig
				for (List<ReaderSession> sessionList : readerSessionByReaderConfig
						.values()) {
					if (sessionList.contains(session)) {
						sessionList.remove(session);
					}
				}

			} else {
				logger.warn("No session found with ID " + sessionID);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.ReaderSessionDAO#getReaderSessions ()
	 */
	@Override
	public Set<String> getReaderSessions() {
		return new HashSet<String>(readerSessionByName.keySet());
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(BundleContext context) {
		this.context = context;
	}

	/**
	 * Bind a new factory to this service.
	 * 
	 * @param readerFactory
	 * @param parameters
	 */
	public void bind(AbstractReaderConfiguration<?> readerConfiguration,
			Dictionary<String, String> parameters) {
		synchronized (this) {
			readerConfigurationsById.put(readerConfiguration.getID(),
					readerConfiguration);
		}
	}

	/**
	 * Unbind a disapearing service from this service.
	 * 
	 * @param readerFactory
	 * @param parameters
	 */
	public void unbind(AbstractReaderConfiguration<?> readerConfiguration,
			Dictionary<String, String> parameters) {
		synchronized (this) {
			readerConfigurationsById.remove(readerConfiguration.getID());
			if (readerSessionByReaderConfig.get(readerConfiguration) != null) {
				for (ReaderSession session : readerSessionByReaderConfig
						.get(readerConfiguration)) {
					session.destroy();
					readerSessionByName.remove(session.getID());
					for (List<ReaderSession> sessionList : readerSessionByCommandFactory
							.values()) {
						if (sessionList.contains(session)) {
							sessionList.remove(session);
						}
					}
				}
				readerSessionByReaderConfig.get(readerConfiguration).clear();
			}
		}
	}

	/**
	 * Used by spring to give the initial list of configs.
	 * 
	 * @param configurations
	 */
	public void setReaderConfigurations(
			Set<AbstractReaderConfiguration<?>> configurations) {
		synchronized (this) {
			for (AbstractReaderConfiguration<?> config : configurations) {
				readerConfigurationsById.put(config.getID(), config);
			}
		}
	}

	/**
	 * Bind a new command factory to this service.
	 * 
	 * @param readerFactory
	 * @param parameters
	 */
	public void bindCommand(AbstractCommandConfiguration<?> commandFactory,
			Dictionary<String, String> parameters) {
		synchronized (this) {
			commandFactoriesById.put(commandFactory.getID(), commandFactory);
		}
	}

	/**
	 * Unbind a disapearing command service from this service.
	 * 
	 * @param readerFactory
	 * @param parameters
	 */
	public void unbindCommand(AbstractCommandConfiguration<?> commandFactory,
			Dictionary<String, String> parameters) {
		synchronized (this) {
			commandFactoriesById.remove(commandFactory.getID());
			if (readerSessionByCommandFactory.get(commandFactory) != null) {
				for (ReaderSession session : readerSessionByCommandFactory
						.get(commandFactory)) {
					session.destroy();
					readerSessionByName.remove(session.getID());
					for (List<ReaderSession> sessionList : readerSessionByReaderConfig
							.values()) {
						if (sessionList.contains(session)) {
							sessionList.remove(session);
						}
					}
				}
				readerSessionByCommandFactory.get(commandFactory).clear();
			}
		}
	}

	/**
	 * Used by spring to give the initial list of command factories.
	 * 
	 * @param factories
	 */
	public void setCommandFactories(
			Set<AbstractCommandConfiguration<?>> factories) {
		synchronized (this) {
			for (AbstractCommandConfiguration<?> factory : factories) {
				commandFactoriesById.put(factory.getID(), factory);
			}
		}
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
	}
}

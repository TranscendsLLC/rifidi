/**
 * 
 */
package org.rifidi.edge.newcore.readersession;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.rifidi.edge.newcore.commands.CommandFactory;
import org.rifidi.edge.newcore.internal.ReaderSession;
import org.rifidi.edge.newcore.internal.impl.ReaderSessionImpl;
import org.rifidi.edge.newcore.readers.ReaderConfiguration;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ReaderSessionManagementImpl implements ReaderSessionManagement {

	/** Reference to the bundle context. */
	private BundleContext context;
	/** Counter for session ids. */
	private Integer counter = 0;
	/** Set of readers that is currently available. */
	private Set<ReaderConfiguration<?>> configurations;
	/** Set of currently available commands. */
	private Set<CommandFactory<?>> commandFactories;
	/** Executor for reader sessions. */
	private ThreadPoolExecutor executor;

	/**
	 * Constructor.
	 */
	public ReaderSessionManagementImpl() {
		configurations = new HashSet<ReaderConfiguration<?>>();
		commandFactories = new HashSet<CommandFactory<?>>();
		executor = new ThreadPoolExecutor(5, 15, 200, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.readersession.ReaderSessionManagement#
	 * createAndStartReaderSession
	 * (org.rifidi.edge.newcore.readers.ReaderConfiguration,
	 * org.rifidi.edge.newcore.commands.CommandFactory)
	 */
	@Override
	public void createAndStartReaderSession(ReaderConfiguration<?> reader,
			CommandFactory<?> command) {
		ReaderSessionImpl session = new ReaderSessionImpl();
		session.setReaderFactory(reader);
		session.setCommmandFactory(command);
		Dictionary<String, String> params = new Hashtable<String, String>();
		params.put("id", counter.toString());
		counter++;
		session.setRegistration(context.registerService(ReaderSession.class
				.getName(), session, params));
		// TODO: store future
		Future<?> future = executor.submit(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.readersession.ReaderSessionManagement#
	 * getAvailableCommandFactories()
	 */
	@Override
	public Set<CommandFactory<?>> getAvailableCommandFactories() {
		return new HashSet<CommandFactory<?>>(commandFactories);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.readersession.ReaderSessionManagement#
	 * getAvailableReaderFactories()
	 */
	@Override
	public Set<ReaderConfiguration<?>> getAvailableReaderConfigurations() {
		return new HashSet<ReaderConfiguration<?>>(configurations);
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(BundleContext context) {
		this.context = context;
	}

	/**
	 * Used by spring to give the initial list of configs.
	 * 
	 * @param configurations
	 */
	public void setReaderConfigurations(
			Set<ReaderConfiguration<?>> configurations) {
		this.configurations.addAll(configurations);
	}

	/**
	 * Bind a new command factory to this service.
	 * 
	 * @param readerFactory
	 * @param parameters
	 */
	public void bindCommand(CommandFactory<?> commandFactory,
			Dictionary<String, String> parameters) {
		commandFactories.add(commandFactory);
	}

	/**
	 * Unbind a disapearing command service from this service.
	 * 
	 * @param readerFactory
	 * @param parameters
	 */
	public void unbindCommand(CommandFactory<?> commandFactory,
			Dictionary<String, String> parameters) {
		commandFactories.remove(commandFactory);
	}

	/**
	 * Used by spring to give the initial list of command factories.
	 * 
	 * @param factories
	 */
	public void setCommandFactories(Set<CommandFactory<?>> factories) {
		commandFactories.addAll(factories);
	}

	/**
	 * Bind a new factory to this service.
	 * 
	 * @param readerFactory
	 * @param parameters
	 */
	public void bind(ReaderConfiguration<?> readerConfiguration,
			Dictionary<String, String> parameters) {
		configurations.add(readerConfiguration);
	}

	/**
	 * Unbind a disapearing service from this service.
	 * 
	 * @param readerFactory
	 * @param parameters
	 */
	public void unbind(ReaderConfiguration<?> readerConfiguration,
			Dictionary<String, String> parameters) {
		configurations.remove(readerConfiguration);
	}
}

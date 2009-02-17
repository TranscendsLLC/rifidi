/**
 * 
 */
package org.rifidi.edge.newcore.readersession;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.rifidi.edge.newcore.ReaderConfiguration;
import org.rifidi.edge.newcore.internal.ReaderSession;
import org.rifidi.edge.newcore.internal.impl.ReaderSessionImpl;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ReaderSessionManagementImpl implements ReaderSessionManagement {

	/** Reference to the bundle context. */
	private BundleContext context;
	/** Counter for session ids. */
	private Integer counter = 0;
	/***/
	private Set<ReaderConfiguration> configurations;

	/**
	 * Constructor.
	 */
	public ReaderSessionManagementImpl() {
		configurations = new HashSet<ReaderConfiguration>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.readersession.ReaderSessionFactory#
	 * createReaderSession()
	 */
	@Override
	public synchronized ReaderSession createReaderSession() {
		ReaderSessionImpl session = new ReaderSessionImpl();
		Dictionary<String, String> params = new Hashtable<String, String>();
		params.put("id", counter.toString());
		counter++;
		session.setRegistration(context.registerService(ReaderSession.class
				.getName(), session, params));
		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.readersession.ReaderSessionManagement#
	 * getAvailableReaderFactories()
	 */
	@Override
	public Set<ReaderConfiguration> getAvailableReaderConfigurations() {
		return new HashSet<ReaderConfiguration>(configurations);
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
	public void setReaderConfigurations(Set<ReaderConfiguration> configurations) {
		this.configurations.addAll(configurations);
	}

	/**
	 * Bind a new factory to this service.
	 * 
	 * @param readerFactory
	 * @param parameters
	 */
	public void bind(ReaderConfiguration readerConfiguration,
			Dictionary<String, String> parameters) {
		configurations.add(readerConfiguration);
	}

	/**
	 * Unbind a disapearing service from this service.
	 * 
	 * @param readerFactory
	 * @param parameters
	 */
	public void unbind(ReaderConfiguration readerConfiguration,
			Dictionary<String, String> parameters) {
		configurations.remove(readerConfiguration);
	}
}

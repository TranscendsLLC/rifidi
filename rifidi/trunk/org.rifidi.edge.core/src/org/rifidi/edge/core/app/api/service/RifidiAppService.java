/**
 * 
 */
package org.rifidi.edge.core.app.api.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.rifidi.edge.core.app.api.AbstractRifidiApp;

import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This is a base class for Rifidi Application Services. These services capture
 * common esper patterns and allow subscribers to paramaterize them and
 * subscribe to events produced by these patterns. The services are then made
 * available in the OSGi registry.
 * 
 * When a subscriber subscribes, a new set of esper statements is created using
 * the EsperFactory. When a subscriber unsubscribes, the esper statemetns are
 * destroyed.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class RifidiAppService<T extends RifidiAppSubscriber> extends
		AbstractRifidiApp {

	/** The number of subscribers created so far */
	private final AtomicInteger counter;
	/** A map to keep up with subscribers and their esper statement names */
	private final Map<T, List<String>> subscriberMap;

	/**
	 * Constructor for a AbstractRifidiApp
	 * 
	 * @param group
	 *            the group this application is a part of
	 * @param name
	 *            The name of the application
	 */
	public RifidiAppService(String group, String name) {
		super(group,name);
		counter = new AtomicInteger(0);
		subscriberMap = new HashMap<T, List<String>>();
	}

	/**
	 * Subscribe to this Rifidi Application Service.
	 * 
	 * First all statements returned by the createStatements method in the
	 * esperFactory are added. Then the query along with the statement listener
	 * are added.
	 * 
	 * @param subscriber
	 *            The new subscriber. Each subscriber should be unique according
	 *            to its equals method.
	 * @param esperFactory
	 *            The factory used to create the
	 */
	protected void subscribe(T subscriber, RifidiAppEsperFactory esperFactory) {
		synchronized (subscriberMap) {
			// if already subscribed, return
			if (subscriberMap.containsKey(subscriber)) {
				return;
			}
			List<String> statementNames = new LinkedList<String>();
			// create the esper statemetns from strings
			for (String statement : esperFactory.createStatements()) {
				statementNames.add(addStatement(statement));
			}
			statementNames.add(addStatement(esperFactory.createQuery(),
					createListener(subscriber)));
			subscriberMap.put(subscriber, statementNames);
		}

	}

	/**
	 * Unsubscribe from this service
	 * 
	 * @param subscriber
	 *            The subscriber
	 */
	public void unsubscribe(T subscriber) {
		synchronized (subscriberMap) {
			if(!this.subscriberMap.containsKey(subscriber)){
				return;
			}
			List<String> statements = this.subscriberMap.remove(subscriber);
			if (statements != null) {
				for (String name : statements) {
					destroyStatement(name);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_stop()
	 */
	@Override
	protected void _stop() {
		super._stop();

		// need to override the stop method so that we make sure to delete all
		// esper statements
		Set<T> subscribers = new HashSet<T>(this.subscriberMap.keySet());
		for (T subscriber : subscribers) {
			unsubscribe(subscriber);
		}
	}

	/**
	 * Increments and returns the counter.
	 */
	protected Integer getCounter() {
		return counter.incrementAndGet();
	}

	/**
	 * This method returns a StatementAwareUpdateListener that listens to the
	 * query specified in the esperFactory's createQuery method. It should
	 * notify subscribers of events.
	 * 
	 * @param subscriber
	 *            The subscriber to notify
	 */
	protected abstract StatementAwareUpdateListener createListener(T subscriber);

}

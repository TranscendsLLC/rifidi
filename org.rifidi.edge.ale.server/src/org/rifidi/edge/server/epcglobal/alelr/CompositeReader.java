package org.rifidi.edge.server.epcglobal.alelr;

import java.text.DateFormat;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.rifidi.edge.epcglobal.alelr.LRProperty;
import org.rifidi.edge.epcglobal.alelr.LRSpec;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.server.epcglobal.alelr.services.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;

/**
 * @author Daniel Gomez
 */

public final class CompositeReader extends Reader {

	public UUID CompositeReaderId;

	private AbstractQueue<Set<TagReadEvent>> readerCycleTagReadEventSetQueue = new ConcurrentLinkedQueue<Set<TagReadEvent>>();

	private List<Observer<TagReadEvent>> observers = new ArrayList<Observer<TagReadEvent>>();

	private String readerName;

	private Dictionary<String, String> logicalReaderProperties = new Hashtable<String, String>();

	private LRSpec lrSpec;

//	private Collection<LRProperty> properties = new ArrayList<LRProperty>();

	private boolean isStarted = false;

	private SubscriptionList disposables = new SubscriptionList();

	private boolean isConnected = false;

	/** logical readers within the composite reader. */
	private Map<String, Pair<Reader, Subscription>> readers = new Hashtable<String, Pair<Reader, Subscription>>();

	@Autowired
	private ReaderService readerService;

	private long readerCycleTime = 1000l;
	
	
	
	public CompositeReader()	{
	}
			

	@Override
	public void onCompleted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(Throwable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Subscription getSubscription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSubscription(Subscription subscription) {
		// TODO Auto-generated method stub

	}

	public Subscription subscribeObserver(Observer<TagReadEvent> observer) {

		if (!this.observers.contains(observer)) {
			observers.add(observer);
		}

		return new Unsubscriber(this.observers, observer);
	}

	@Override
	public void unsubscribeObserver() {
		if (getSubscription() != null)
			getSubscription().unsubscribe();
		for (Pair<Reader, Subscription> reader : readers.values()) {
			reader.getRight().unsubscribe();
		}
		readers.clear();
	}

	@Override
	public void fireEvent(TagReadEvent e) {

		if(isStarted)
			for (Observer<TagReadEvent> observer : this.observers) {
				observer.onNext(e);
			}
	}

	@Override
	public void initialize(String name, LRSpec lrSpec) throws ValidationExceptionResponse {
		if (name == null) {
			// throw new NotImplementedException("no reader name specified");
		}
		if (lrSpec == null) {
			// throw new NotImplementedException("no spec specified");
		}

		this.readerName = name;
		this.lrSpec = lrSpec;

		if (this.lrSpec.getProperties().getProperty() == null) {
			throw new UnsupportedOperationException("no properties specified");
		}

		// store the properties
		properties.addAll(this.lrSpec.getProperties().getProperty());

		// create the sub parts by calling the factory method
		List<String> readers = lrSpec.getReaders().getReader();

		if (readers != null)
			for (String readerName : readers) {
				if(readerService != null){
					
					// just retrieve the reader from the LogicalReaderManager
					Reader reader = readerService.getLogicalReader(readerName);

					// Subscribe the reader if not null
					if (reader == null){
						throw new ValidationExceptionResponse("Not found a logical reader with name: '" + readerName + "'. Make sure you have defined this logical reader by using ALELR define method");
					}
					
					addReader(reader);
				}
			}

	}

	/**
	 * add a logicalReader to the composite.
	 * 
	 * @param reader
	 *            a logicalReader (baseReader or CompositeReader)
	 */
	public void addReader(Reader reader) {
		Subscription disposable = readerService.subscribe(reader.getName(), this);
		readers.put(reader.getName(), new ImmutablePair<Reader, Subscription>(reader, disposable));
	}

	/**
	 * removes a given reader from the composite.
	 * 
	 * @param reader
	 *            a logicalReader (baseReader or CompositeReader).
	 */
	public void RemoveReader(Reader reader) {
		if (readers.containsKey(reader.getName())) {
			readers.get(reader.getName()).getRight().unsubscribe();
			readers.remove(reader.getName());
		}
	}

	/**
	 * checks if the composite reader contains the given reader.
	 * 
	 * @param readerName
	 *            the name of the reader to check.
	 * @return true if contained, false otherwise.
	 */
	public boolean ContainsReader(String readerName) {
		return readers.containsKey(readerName);
	}

	@Override
	public LRSpec getLRSpec() {
		// TODO Auto-generated method stub
		return lrSpec;
	}

	@Override
	public void setLRSpec(LRSpec lrSpec) {
		// TODO Auto-generated method stub
		this.lrSpec = lrSpec;
	}

	@Override
	public Collection<LRProperty> getProperties() {
		// TODO Auto-generated method stub
		return properties;
	}

	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return isStarted;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return readerName;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		readerName = name;
	}

	@Override
	public synchronized void update(LRSpec lrSpec) {
		// TODO Auto-generated method stub

		synchronized (readers) {

			// test whether we need to update the reader or just the properties
			// this can be done by comparing the readers with the readers in the
			// new LRSpec
			// TODO Uncomment this
			List<String> readerNameList = lrSpec.getReaders().getReader();

			// set the new spec
			setLRSpec(lrSpec);

			if (!CollectionUtils.isEqualCollection(readerNameList, readers.keySet())) {

				// as there are changes in the readers, we
				// need to stop this compositeReader, update the
				// components and then restart again
				// LOG.debug("updating readers in CompositeReader " +
				// readerName);

				// stop the reader
				stop();

				// remove all readers first
				for (Pair<Reader, Subscription> reader : readers.values()) {
					reader.getRight().unsubscribe();
				}
				readers.clear();

				// fill in the new readers
				for (String readerName : readerNameList) {
					Reader logicalReader = readerService.getLogicalReader(readerName);
					addReader(logicalReader);
				}

				start();
			}

			// update the LRProperties
			// LOG.debug("updating LRProperties in CompositeReader " +
			// readerName);

			notifyAll();

		}
	}

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		isStarted = true;		
	}

	@Override
	public synchronized void stop() {
		// TODO Auto-generated method stub
		isStarted = false;
		disposables.unsubscribe();
		disposables.clear();
	}

	@Override
	public boolean hasSubscription() {
		if (getSubscription() != null)
			return true;
		return false;
	}

	/*
	 * <summary> Number of observers </summary>
	 */
	public int Count() {
		return readers.size();
	}

	public boolean isConnected() {
		return isConnected;
	}

}

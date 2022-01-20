/**
 * 
 */
package org.rifidi.edge.server.epcglobal.alelr.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.rifidi.edge.epcglobal.ale.ECReports;
import org.rifidi.edge.epcglobal.ale.ECSpec;
import org.rifidi.edge.epcglobal.ale.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;
import org.rifidi.edge.server.ale.infrastructure.HttpNotifier;
import org.rifidi.edge.server.ale.infrastructure.MqttNotifier;
import org.rifidi.edge.server.ale.infrastructure.Notifier;
import org.rifidi.edge.server.epcglobal.ale.Cycle;
import org.rifidi.edge.server.epcglobal.ale.EventCycle;
import org.rifidi.edge.server.epcglobal.alelr.Reader;
import org.rifidi.edge.utils.ECReportsHelper;
import org.rifidi.edge.utils.FileUtils;
import org.rifidi.edge.utils.PersistenceConfig;
import org.rifidi.edge.utils.RifidiHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rx.Observer;
import rx.Subscription;

/**
 * @author Daniel Gomez
 *
 */
@Service
public class EventCycleService implements CycleService {

	/**
	 * set of logical readers and notifiers which deliver tags and reports for
	 * each event cycle.
	 */
	private final ConcurrentMap<String, Pair<EventCycle, Pair<ConcurrentMap<String, Pair<Subscription, Reader>>, ConcurrentMap<String, Pair<Subscription, Notifier>>>>> eventCycleReadersAndNotifiers = new ConcurrentHashMap<String, Pair<EventCycle, Pair<ConcurrentMap<String, Pair<Subscription, Reader>>, ConcurrentMap<String, Pair<Subscription, Notifier>>>>>();

	private final ReaderService readerService;
	private final ECReportsHelper reportsHelper;
	private final NotifierService notifierService;
	
	@Autowired
	private RifidiHelper rifidiHelper;
	
	@Autowired
	PersistenceConfig config;
	
	@Autowired
	private FileUtils fileUtils;
	
	/** logger */
	private static final Logger LOG = Logger.getLogger(EventCycleService.class);

	@Autowired
	public EventCycleService(ReaderService readerService, ECReportsHelper reportsHelper,
			NotifierService notifierService) {
		this.readerService = readerService;
		this.reportsHelper = reportsHelper;
		this.notifierService = notifierService;
	}

	@Override
	public boolean containsKey(String specName) {
		// TODO Auto-generated method stub
		return eventCycleReadersAndNotifiers.containsKey(specName);

	}

	@Override
	public Cycle define(String specName, ECSpec spec) throws ImplementationExceptionResponse, ECSpecValidationExceptionResponse {
		// TODO Auto-generated method stub
		EventCycle eventCycle = new EventCycle(specName, spec, readerService, reportsHelper, rifidiHelper, config, fileUtils, this);
		ConcurrentMap<String, Pair<Subscription, Reader>> subscriptionAndReaderMap = new ConcurrentHashMap<String, Pair<Subscription, Reader>>();
		ConcurrentMap<String, Pair<Subscription, Notifier>> subscriptionAndNotifierMap = new ConcurrentHashMap<String, Pair<Subscription, Notifier>>();
		Pair<ConcurrentMap<String, Pair<Subscription, Reader>>, ConcurrentMap<String, Pair<Subscription, Notifier>>> readerAndSubscriptionNotifierAndSubscription = new MutablePair<ConcurrentMap<String, Pair<Subscription, Reader>>, ConcurrentMap<String, Pair<Subscription, Notifier>>>(
				subscriptionAndReaderMap, subscriptionAndNotifierMap);
		Pair<EventCycle, Pair<ConcurrentMap<String, Pair<Subscription, Reader>>, ConcurrentMap<String, Pair<Subscription, Notifier>>>> evenCycleAndSubscriptionAndReaderAndSubscriptionAndNotifier = new MutablePair<EventCycle, Pair<ConcurrentMap<String, Pair<Subscription, Reader>>, ConcurrentMap<String, Pair<Subscription, Notifier>>>>(
				eventCycle, readerAndSubscriptionNotifierAndSubscription);

		for (String logicalReaderName : spec.getLogicalReaders().getLogicalReader()) {
			Reader reader = readerService.getLogicalReader(logicalReaderName);
			if ( reader == null ){
				throw new ECSpecValidationExceptionResponse("Not found a logical reader with name: '" + logicalReaderName + "'. Make sure you have defined this logical reader by using ALELR define method");
			}
			Subscription subscription = reader.subscribeObserver(eventCycle);
			Pair<Subscription, Reader> subscriptionAndReaderPair = new MutablePair<Subscription, Reader>(subscription,
					reader);
			subscriptionAndReaderMap.putIfAbsent(reader.getName(), subscriptionAndReaderPair);
		}

		eventCycleReadersAndNotifiers.putIfAbsent(specName,
				evenCycleAndSubscriptionAndReaderAndSubscriptionAndNotifier);

		return eventCycle;
	}

	@Override
	public Iterable<ECSpec> getECSpecs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ECSpec getECSpec(String specName) {
		// TODO Auto-generated method stub
		Pair<EventCycle, Pair<ConcurrentMap<String, Pair<Subscription, Reader>>, ConcurrentMap<String, Pair<Subscription, Notifier>>>> evenCycleAndSubscriptionAndReaderAndSubscriptionAndNotifier = eventCycleReadersAndNotifiers
				.get(specName);
		EventCycle cycle = evenCycleAndSubscriptionAndReaderAndSubscriptionAndNotifier.getKey();
		ECSpec ecSpec = cycle.getECSpec();
		return ecSpec;
	}

	@Override
	public List<String> getECSpecNames() {
		// TODO Auto-generated method stub
		return new ArrayList<String>(eventCycleReadersAndNotifiers.keySet());
	}

	@Override
	public Cycle getCycle(String specName) {
		// TODO Auto-generated method stub
		return eventCycleReadersAndNotifiers.get(specName).getKey();
	}

	@Override
	public void loadEventCycles() {
		// TODO Auto-generated method stub

	}

	@Override
	public void subscribe(String specName, String notificationURI) throws InvalidURIExceptionResponse {
		// TODO Auto-generated method stub
		// Notifier notifier = notifierService.createNotifierFromPlugin("MQTT",
		// specName, notificationURI);

		// Determine whether to use MQTT (TCP) or SOAP (HTTP) notifier
		Notifier notifier;
		if (notificationURI.startsWith("tcp://")) {
			notifier = new MqttNotifier();
		} else if (notificationURI.startsWith("http://")) {
			notifier = new HttpNotifier();
		} else {
			LOG.error("Unsupported subscription schema: " + notificationURI);
			throw new InvalidURIExceptionResponse("Unsupported subscription schema");
		}

		notifier.setNotifierId(specName + notificationURI);
		notifier.init(notificationURI, rifidiHelper);
		Subscription subscription = eventCycleReadersAndNotifiers.get(specName).getKey().subscribeObserver(notifier);
		Pair<Subscription, Notifier> subscriptionAndNotifier = new ImmutablePair<Subscription, Notifier>(subscription,
				notifier);
		eventCycleReadersAndNotifiers.get(specName).getRight().getRight().putIfAbsent(notificationURI,
				subscriptionAndNotifier);
	}

	@Override
	public void undefine(String specName) {
		// TODO Auto-generated method stub

		Pair<EventCycle, Pair<ConcurrentMap<String, Pair<Subscription, Reader>>, ConcurrentMap<String, Pair<Subscription, Notifier>>>> eventCycle = eventCycleReadersAndNotifiers
				.remove(specName);
		
		eventCycle.getKey().stop();
		
		System.out.println("EventCycle.undefine.eventCycle: " + eventCycle.getKey().getName());
		
		Pair<ConcurrentMap<String, Pair<Subscription, Reader>>, ConcurrentMap<String, Pair<Subscription, Notifier>>> rightCycle = eventCycle
				.getRight();
		for (Pair<Subscription, Reader> subscriptionReader : rightCycle.getLeft().values()) {
			subscriptionReader.getLeft().unsubscribe();
			// FIXME ALE
			// subscriptionReader.getRight().unsubscribeObserver();
		}
		

	}

	@Override
	public void unsubscribe(String specName, String notificationURI) {
		// TODO Auto-generated method stub

		Pair<Subscription, Notifier> subscriptionNotifier = eventCycleReadersAndNotifiers.get(specName).getRight()
				.getRight().remove(notificationURI);
		subscriptionNotifier.getLeft().unsubscribe();
//		subscriptionNotifier.getRight().onCompleted();

	}

	@Override
	public Subscription subscribe(String specName, Observer<ECReports> observer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getSubscribers(String specName) {
		return eventCycleReadersAndNotifiers.get(specName).getRight().getRight().keySet();
	}

	@Override
	public Cycle get(String specName) {
		// TODO Auto-generated method stub
		return eventCycleReadersAndNotifiers.get(specName).getKey();
	}
	
	public Collection<Pair<Subscription, Notifier>> getNotifiers(String specName){
		return eventCycleReadersAndNotifiers.get(specName).getRight().getRight().values();
	}

}

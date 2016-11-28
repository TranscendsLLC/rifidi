/**
 * 
 */
package org.rifidi.edge.server.epcglobal.ale;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.rifidi.edge.epcglobal.ale.ECReport;
import org.rifidi.edge.epcglobal.ale.ECReportGroup;
import org.rifidi.edge.epcglobal.ale.ECReportSpec;
import org.rifidi.edge.epcglobal.ale.ECReports;
import org.rifidi.edge.epcglobal.ale.ECReports.Reports;
import org.rifidi.edge.epcglobal.ale.ECSpec;
import org.rifidi.edge.epcglobal.ale.ECSpecValidationException;
import org.rifidi.edge.epcglobal.ale.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.ECTime;
import org.rifidi.edge.epcglobal.ale.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.AddReaders;
import org.rifidi.edge.epcglobal.alelr.ImplementationException;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.server.ale.infrastructure.Notifier;
import org.rifidi.edge.server.epcglobal.alelr.Reader;
import org.rifidi.edge.server.epcglobal.alelr.services.EventCycleService;
import org.rifidi.edge.server.epcglobal.alelr.services.ReaderService;
import org.rifidi.edge.utils.DeserializerUtil;
import org.rifidi.edge.utils.ECReportsHelper;
import org.rifidi.edge.utils.ECTerminationCondition;
import org.rifidi.edge.utils.ECTimeUnit;
import org.rifidi.edge.utils.FileUtils;
import org.rifidi.edge.utils.PersistenceConfig;
import org.rifidi.edge.utils.RifidiHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.oxo42.stateless4j.StateMachineConfig;
import com.rits.cloning.Cloner;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;

/**
 * default implementation of the event cycle.
 * 
 * @author regli
 * @author swieland
 * @author benoit.plomion@orange.com
 * @author nkef@ait.edu.gr
 */

public final class EventCycle extends Observable<ECReport> implements Cycle, Runnable {
	private static rx.Observable.OnSubscribe<ECReport> f;

	// Injection by constructor
	private final ReaderService readerService;
	private final ECReportsHelper reportsHelper;
	private final RifidiHelper rifidiHelper;
	private final PersistenceConfig config;
	private final FileUtils fileUtils;
	private final EventCycleService eventCycleService;

//	private ConcurrentMap<String, Pair<Subscription, Notifier>> notifiers = new ConcurrentHashMap<String, Pair<Subscription, Notifier>>();

	private List<Observer<ECReports>> observers = new ArrayList<Observer<ECReports>>();

	private ConcurrentMap<String, Report> reports = new ConcurrentHashMap<String, Report>();

	private SubscriptionList disposables = new SubscriptionList();

	private ECReports ecReports;

	/** logger. */
	private static final Logger LOG = Logger.getLogger(EventCycle.class);

	/** random numbers generator. */
	private static final Random rand = new Random(System.currentTimeMillis());

	/** ale id. */
	private static final String ALEID = "RIFIDI-ALE" + rand.nextInt();

	/** number of this event cycle. */
	private static int number = 0;

	/** name of this event cycle. */
	private final String name;

	/** report generator which contains this event cycle. */
	// private final ReportsGenerator generator;

	/** thread. */
	// private final Thread thread;
	private final Thread thread;

	/** event cycle specification for this event cycle. */
	private final ECSpec spec;

	/** set of logical readers which deliver tags for this event cycle. */
	private final ConcurrentMap<String, Pair<Subscription, Reader>> logicalReaders = new ConcurrentHashMap<String, Pair<Subscription, Reader>>();

	/** set of reports for this event cycle. */
	// private final Set<Report> reports = new HashSet<Report>();

	/** a hash map with all the reports generated in the last round. */
	private final ConcurrentMap<String, ECReport> lastReports = new ConcurrentHashMap<String, ECReport>();

	/** contains all the ec report specs hashed by their report name. */
	private final ConcurrentMap<String, ECReportSpec> reportSpecByName = new ConcurrentHashMap<String, ECReportSpec>();

	/** set of tags for this event cycle. */
	private Set<Tag> tags = Collections.synchronizedSet(new HashSet<Tag>());

	/** this set stores the tags from the previous EventCycle run. */
	private Set<Tag> lastEventCycleTags = null;

	/**
	 * this set stores the tags between two event cycle in the case of
	 * rejectTagsBetweenCycle is false
	 */
	private Set<Tag> betweenEventsCycleTags = Collections.synchronizedSet(new HashSet<Tag>());

	/**
	 * flags to know if the event cycle haven t to reject tags in the case than
	 * duration and repeatPeriod is same
	 */
	private boolean rejectTagsBetweenCycle = true;

	/** indicates if this event cycle is terminated or not . */
	private boolean isTerminated = false;

	/**
	 * lock for thread synchronization between reports generator and this.
	 * swieland 2012-09-29: do not use primitive type as int or Integer as
	 * autoboxing can result in new thread object for the lock ->
	 * non-threadsafe...
	 */
	private final EventCycleLock lock = new EventCycleLock();

	/** flag whether the event cycle has passed through or not. */
	private boolean roundOver = false;

	/** the duration of collecting tags for this event cycle in milliseconds. */
	private long durationValue;

	/** the total time this event cycle runs in milliseconds. */
	private long totalTime;

	/** the termination condition of this event cycle. */
	private String terminationCondition = null;

	/** flags the eventCycle whether it shall run several times or not. */
	private boolean running = false;

	/** flags whether the EventCycle is currently not accepting tags. */
	private boolean acceptTags = false;

	/** tells how many times this EventCycle has been scheduled. */
	private int rounds = 0;

	/** ec report for the poller */
	private ECReports pollReport = null;

	/** indicates if somebody is polling this input generator at the moment. */
	private boolean polling = false;

	private State state;
	
	// TODO: check if we can use this instead of the dummy class.
	private final class EventCycleLock {

	}

	/**
	 * Constructor sets parameter and starts thread.
	 * 
	 * @param generator
	 *            to which this event cycle belongs to
	 * @throws ImplementationExceptionResponse
	 * @throws ImplementationException
	 *             if an implementation exception occurs
	 */
	@Autowired
	public EventCycle(String specName, ECSpec spec, ReaderService readerService, 
			ECReportsHelper reportsHelper, RifidiHelper rifidiHelper, PersistenceConfig config,
			FileUtils fileUtils, EventCycleService eventCycleService)
			throws ImplementationExceptionResponse {
		super(f);

		// Set reader service
		this.readerService = readerService;
		this.reportsHelper = reportsHelper;
		this.rifidiHelper = rifidiHelper;
		this.config = config;
		this.fileUtils = fileUtils;
		this.eventCycleService = eventCycleService;

		// set name
		this.name = specName + "_" + number++;

		// set spec
		this.spec = spec;

		// init BoundarySpec values
		durationValue = getDurationValue();

		// get report specs and create a report for each spec
		for (ECReportSpec reportSpec : spec.getReportSpecs().getReportSpec()) {

			// add report spec and report to reports
			Report report = new Report(reportSpec, this);
			reports.putIfAbsent(reportSpec.getReportName(), report);

			// hash into the report spec structure
			reportSpecByName.putIfAbsent(reportSpec.getReportName(), reportSpec);
		}

		long repeatPeriod = getRepeatPeriodValue();
		if (durationValue == repeatPeriod) {
			setRejectTagsBetweenCycle(false);
		}

		LOG.debug(String.format("durationValue: %s\n", durationValue));

		setAcceptTags(false);

		// LOG.debug("adding logicalReaders to EventCycle");
		// get LogicalReaderStubs
		if (spec.getLogicalReaders() != null) {
			List<String> logicalReaderNames = spec.getLogicalReaders().getLogicalReader();
			for (String logicalReaderName : logicalReaderNames) {
				// LOG.debug("retrieving logicalReader " + logicalReaderName);
				Reader logicalReader = readerService.getLogicalReader(logicalReaderName);

				if (logicalReader != null) {
					// LOG.debug("adding logicalReader " +
					// logicalReader.getName() + " to EventCycle " + name);
					addReader(logicalReader);

				}
			}
		} else {
			LOG.error("ECSpec contains no readers");
		}

		rounds = 0;

		// create and start Thread
		thread = new Thread(this, "EventCycle" + name);
		thread.setDaemon(true);
		thread.start();
		this.launch();

		if (durationValue > 0) {
			LOG.debug("Duration = " + durationValue);

		}

		disposables
				.add(Observable.interval(getRepeatPeriodValue(), TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {

					@Override
					public void call(Long arg0) {
						// TODO Auto-generated method stub

						try {
							buildAndNotify();
						} catch (ECSpecValidationExceptionResponse | ImplementationExceptionResponse e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}));

		LOG.debug("New EventCycle '" + name + "' created.");
	}

	private void buildAndNotify() throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse {

		synchronized (this) {
			this.notifyAll();
		}
	}

	/**
	 * add a logicalReader to the eventCycle.
	 * 
	 * @param reader
	 *            a logicalReader (baseReader or CompositeReader)
	 */
	private void addReader(Reader reader) {
		Subscription disposable = readerService.subscribe(reader.getName(), this);
		logicalReaders.putIfAbsent(reader.getName(), new ImmutablePair<Subscription, Reader>(disposable, reader));
	}

	public void removeReaders() {
		for (Pair<Subscription, Reader> reader : logicalReaders.values()) {
			reader.getKey().unsubscribe();
		}

		logicalReaders.clear();
	}

	/**
	 * This method returns the ec reports.
	 * 
	 * @return ec reports
	 * @throws ECSpecValidationException
	 *             if the tags of the report are not valid
	 * @throws ImplementationException
	 *             if an implementation exception occurs.
	 */
	private ECReports getECReports() throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse {

		// create ECReports
		ecReports = new ECReports();

		// set spec name
		ecReports.setSpecName(getName());

		// set date
		try {
			ecReports.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		} catch (DatatypeConfigurationException e) {
			LOG.error("Could not create date: " + e.getMessage());
		}

		// set ale id
		ecReports.setALEID(ALEID);

		// set total time in milliseconds
		ecReports.setTotalMilliseconds(totalTime);

		// set termination condition
		ecReports.setTerminationCondition(terminationCondition);

		// set spec
//		System.out.println("EventCycle.getECReports(), spec: " + spec);
//		System.out.println("EventCycle.getECReports(), spec.isIncludeSpecInReports(): " + spec.isIncludeSpecInReports());
		if (spec.isIncludeSpecInReports()) {
			ecReports.setECSpec(spec);
		}

		// set reports
		ecReports.setReports(new Reports());
		ecReports.getReports().getReport().addAll(getReportList());

		return ecReports;
	}

	// @Override
	public void addTag(Tag tag) {

		if (!isAcceptingTags()) {
			return;
		}

		// add event only if EventCycle is still running
		if (isEventCycleActive()) {
			tags.add(tag);
		}
	}

	/**
	 * This method adds a tag between 2 event cycle.
	 * 
	 * @param tag
	 *            to add
	 * @throws ImplementationException
	 *             if an implementation exception occurs
	 * @throws ECSpecValidationException
	 *             if the tag is not valid
	 */
	private void addTagBetweenEventsCycle(Tag tag) {

		if (isRejectTagsBetweenCycle()) {
			return;
		}

		// add event only if EventCycle is still running
		if (isEventCycleActive()) {
			betweenEventsCycleTags.add(tag);
		}
	}

	/**
	 * determine if this event cycle is active (running) or not.
	 * 
	 * @return true if the event cycle is active, false if not.
	 */
	private boolean isEventCycleActive() {
		return thread.isAlive();
	}

	// @Override
	public void update(Observable o, Object arg) {

		LOG.debug("EventCycle "+ getName() + ": Update notification received. ");
		Set<Tag> tags = new LinkedHashSet<>();
		// process the new tag.
		if (arg instanceof Tag) {
			LOG.debug("processing one tag");
			// process one tag
			tags.add((Tag) arg);
		} else if (arg instanceof List) {
			LOG.debug("processing a list of tags");
			for (Object entry : (List<?>) arg) {
				if (entry instanceof Tag) {
					tags.add((Tag) entry);					
				}
			}
		}
		
		if (tags.size() > 0) {
			handleTags(tags);
		} else {
			LOG.debug("EventCycle "+ getName() + ": Update notification received - but not with any tags - ignoring. ");			
		}

		
		
	}

	private void handleTags(Set<Tag> tags) {
		if (!isAcceptingTags()) {
			handleTagsWhileNotAccepting(tags);
		} else {
			handleTagsWhileAccepting(tags);
		}
	}

	/**
	 * deal with new tags.
	 * 
	 * @param tags
	 */
	private void handleTagsWhileAccepting(Set<Tag> tags) {
		// process all the tags we did not process between two eventcycles (or
		// while we did not accept any tags).
		if (!isRejectTagsBetweenCycle()) {
			for (Tag tag : betweenEventsCycleTags) {
				addTag(tag);
			}

			betweenEventsCycleTags.clear();
		}
		// LOG.debug("EventCycle "+ getName() + ": Received list of tags :");
		for (Tag tag : tags) {
			addTag(tag);
		}
	}

	/**
	 * deal with tags while the event cycle is not accepting tags. (eg. between
	 * two event cycles).
	 * 
	 * @param arg
	 *            the update we received.
	 */
	private void handleTagsWhileNotAccepting(Set<Tag> tags) {
		if (!isRejectTagsBetweenCycle()) {
			for (Tag tag : tags) {
				// LOG.debug("received tag between eventcycles: " +
				// tag.getTagIDAsPureURI());
				addTagBetweenEventsCycle(tag);
			}
		}
	}

	// @Override
	public void stop() {
		// unsubscribe this event cycle from logical readers

		for (String logicalReaderName : logicalReaders.keySet()) {
			if (!logicalReaders.get(logicalReaderName).getKey().isUnsubscribed())
				logicalReaders.get(logicalReaderName).getKey().unsubscribe();
		}

		running = false;
		thread.interrupt();
		// LOG.debug("EventCycle '" + name + "' stopped.");

		isTerminated = true;
				
		disposables.unsubscribe();
		disposables.clear();

		synchronized (this) {
			this.notifyAll();
		}
	}

	// @Override
	public String getName() {
		return name;
	}

	// @Override
	public boolean isTerminated() {
		return isTerminated;
	}

	/**
	 * This method is the main loop of the event cycle in which the tags will be
	 * collected. At the end the reports will be generated and the subscribers
	 * will be notified.
	 */
	@Override
	public void run() {

		lastEventCycleTags = new HashSet<Tag>();

		// wait for the start
		// running will be set when the EventCycle
		// has a subscriber
		if (!running) {
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					// LOG.info("eventcycle got interrupted");
					return;
				}
			}
		}

		while (running) {
			rounds++;
			synchronized (lock) {
				roundOver = false;
			}
			// LOG.info("EventCycle "+ getName() + ": Starting (Round " + rounds
			// + ").");

			// set start time
			long startTime = System.currentTimeMillis();

			// accept tags
			setAcceptTags(true);

			// ------------------------------ run for the specified time
			try {

				if (durationValue > 0) {

					// if durationValue is specified and larger than zero,
					// wait for notify or durationValue elapsed.
					synchronized (this) {
						long dt = (System.currentTimeMillis() - startTime);
						this.wait(Math.max(1, durationValue - dt));
						terminationCondition = ECTerminationCondition.DURATION;
					}
				} else {

					// if durationValue is not specified or smaller than zero,
					// wait for notify.
					synchronized (this) {
						this.wait();
					}
				}

			} catch (InterruptedException e) {

				// if Thread is stopped with method stop(),
				// then return without notify subscribers.
				// LOG.info("eventcycle got interrupted");
				return;
			}

			// don't accept tags anymore
			setAcceptTags(false);
			// ------------------------ generate the reports

			// get reports
			try {
				// compute total time
				totalTime = System.currentTimeMillis() - startTime;

				// LOG.info("EventCycle "+ getName() +
				// ": Number of Tags read in the current EventCyle.java: "
				// + tags.size());

				ECReports eventCycleReports = getECReports();

				// notifySubscribers
//				System.out.println("EventCycle.run, ecReports.getECSpec(): " + ecReports.getECSpec());

				notifySubscribers(ecReports);

				// store the current tags into the old tags
				// explicitly clear the tags
				if (lastEventCycleTags != null) {
					lastEventCycleTags.clear();
				}
				if (tags != null) {
					lastEventCycleTags.addAll(tags);
				}

				tags = Collections.synchronizedSet(new HashSet<Tag>());

			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof InterruptedException) {
					// LOG.info("eventcycle got interrupted");
					return;
				}
				// LOG.error("EventCycle "+ getName() + ": Could not create
				// ECReports", e);
			}

			// LOG.info("EventCycle "+ getName() + ": EventCycle finished (Round
			// " + rounds + ").");
			try {
				// inform possibly waiting workers about the finish
				synchronized (lock) {
					roundOver = true;
					lock.notifyAll();
				}
				// wait until reschedule.
				synchronized (this) {
					this.wait();
				}
				// LOG.debug("eventcycle continues");
			} catch (InterruptedException e) {
				// LOG.info("eventcycle got interrupted");
				return;
			}
		}

		// stop EventCycle
		stop();

	}

	private void notifySubscribers(ECReports reports) {
//		System.out.println("EventCycle.notifySubscribers, reports.getECSpec(): " + reports.getECSpec());
		
		// according the ALE 1.1 standard:
		// When the processing of reportIfEmpty and reportOnlyOnChange
		// results in all ECReport instances being omitted from an
		// ECReports for an event cycle, then the delivery of results
		// to subscribers SHALL be suppressed altogether. [...] poll
		// and immediate SHALL always be returned [...] even if that
		// ECReports instance contains zero ECReport instances.

		// An ECReports instance SHALL include an ECReport instance
		// corresponding to each
		// ECReportSpec in the governing ECSpec, in the same order specified in
		// the ECSpec,
		// except that an ECReport instance SHALL be omitted under the following
		// circumstances:
		// - If an ECReportSpec has reportIfEmpty set to false, then the
		// corresponding
		// ECReport instance SHALL be omitted from the ECReports for this event
		// cycle if
		// the final, filtered set of Tags is empty (i.e., if the final Tag list
		// would be empty, or if
		// the final count would be zero).
		// - If an ECReportSpec has reportOnlyOnChange set to true, then the
		// corresponding ECReport instance SHALL be omitted from the ECReports
		// for
		// this event cycle if the filtered set of Tags is identical to the
		// filtered prior set of Tags,
		// where equality is tested by considering the primaryKeyFields as
		// specified in the
		// ECSpec (see Section 8.2), and where the phrase 'the prior set of
		// Tags' is as defined
		// in Section 8.2.6. This comparison takes place before the filtered set
		// has been modified
		// based on reportSet or output parameters. The comparison also
		// disregards
		// whether the previous ECReports was actually sent due to the effect of
		// this
		// parameter, or the reportIfEmpty parameter.
		// When the processing of reportIfEmpty and reportOnlyOnChange results
		// in all
		// ECReport instances being omitted from an ECReports for an event
		// cycle, then the
		// delivery of results to subscribers SHALL be suppressed altogether.
		// That is, a result
		// consisting of an ECReports having zero contained ECReport instances
		// SHALL NOT
		// be sent to a subscriber. (Because an ECSpec must contain at least one
		// ECReportSpec, this can only arise as a result of reportIfEmpty or
		// reportOnlyOnChange processing.) This rule only applies to subscribers
		// (event cycle
		// requestors that were registered by use of the subscribe method); an
		// ECReports
		// instance SHALL always be returned to the caller of immediate or poll
		// at the end of
		// an event cycle, even if that ECReports instance contains zero
		// ECReport instances.

		Cloner cloner = new Cloner();
		// deep clone the original input in order to keep it as the
		// next event cycles last cycle reports.
		ECReports originalInput = cloner.deepClone(reports);

		// we deep clone (clone not sufficient) for the pollers
		// in order to deliver them the correct set of reports.
		if (isPolling()) {
			// deep clone for the pollers (poll and immediate)
			pollReport = cloner.deepClone(reports);
		}

		// we remove the reports that are equal to the ones in the
		// last event cycle. then we send the subscribers.
		List<ECReport> equalReps = new LinkedList<ECReport>();
		List<ECReport> reportsToNotify = new LinkedList<ECReport>();
		try {
			for (ECReport r : reports.getReports().getReport()) {
				final ECReportSpec reportSpec = getReportSpecByName(r.getReportName());

				boolean tagsInReport = hasTags(r);
				// case no tags in report but report if empty
				if (!tagsInReport && reportSpec.isReportIfEmpty()) {
					// LOG.debug("requesting empty for report: " +
					// r.getReportName());
					reportsToNotify.add(r);
				} else if (tagsInReport) {
					reportsToNotify.add(r);
				}
				// check for equal reports since last notification.
				if (reportSpec.isReportOnlyOnChange()) {
					// report from the previous EventCycle run.
					ECReport oldR = getLastReports().get(r.getReportName());

					// compare the new report with the old one.
					if (reportsHelper.areReportsEqual(reportSpec, r, oldR)) {
						equalReps.add(r);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("caught exception while processing reports: ", e);
		}

		// check if the intersection of all reports to notify (including empty
		// ones) and the equal ones is empty
		// -> if so, do not notify at all.
		reportsToNotify.removeAll(equalReps);

		// remove the equal reports
		Reports re = reports.getReports();
		if (null != re)
			re.getReport().removeAll(equalReps);
		// LOG.debug("reports size: " +
		// reports.getReports().getReport().size());

		// next step is to check, if the total report is empty (even if
		// requestIfEmpty but when all reports are equal, do not deliver)
		if (reportsToNotify.size() > 0) {
			// notify the ECReports
			notifySubscribersWithFilteredReports(reports);
		}
		// store the new reports as old reports
		getLastReports().clear();
		if (null != originalInput.getReports()) {
			for (ECReport r : originalInput.getReports().getReport()) {
				getLastReports().put(r.getReportName(), r);
			}
		}

		// notify pollers
		// pollers always receive reports (even when empty).
		if (isPolling()) {
			polling = false;
			if (observers.isEmpty()) {
				setState(State.UNREQUESTED);
			}
			synchronized (this) {
				this.notifyAll();
			}
		}
	}

	/**
	 * once all the filtering is done eventually notify the subscribers with the
	 * reports.
	 * 
	 * @param reports
	 *            the filtered reports.
	 */
	protected void notifySubscribersWithFilteredReports(ECReports reports) {
		// notify subscribers
		String specName = rifidiHelper.getECSpecName( reports.getSpecName() );
//		System.out.println("EventCycle.specName: " + specName);
		
		List<String> errorMessages = new ArrayList<>();
		try {
			
//			if (!eventCycleService.containsKey(specName)) {
//				throw new NoSuchNameExceptionResponse("No ECSpec with such name defined: " + specName);
//			}
			
			String path = config.getRealPathECSpecDir();
			String fileName = specName + ".xml";
//			System.out.println("EventCycle.fileName: " + fileName);
			if ( fileUtils.fileExist(fileName, path) ){
				ECSpec spec = DeserializerUtil.deserializeECSpec(path + fileName);
				errorMessages = rifidiHelper.validateSessionInProcessingState( spec );
				
				if ( !errorMessages.isEmpty() ){
					String errorsStr = rifidiHelper.getErrorMessagesAsSingleText(errorMessages);

					System.out.println("EventCycle.error: " + errorsStr);
					LOG.error("EventCycle.error: " + errorsStr);
					
					sendErrorMesssageToSubscribers(specName, errorsStr);
					
					return;
					
				}
				
			}
			
			synchronized (this.observers) {

				for (Observer<ECReports> observer : this.observers) {
					observer.onNext(reports);
				}
			}
			
		} catch (Exception e){
//			throw new NoSuchNameExceptionResponse(e.getMessage(), e);
			e.printStackTrace();
//			errorMessages.add(e.getMessage());
//			throw new ValidationExceptionResponse(e.getMessage(), e);
			//Send error message to mqtt
			sendErrorMesssageToSubscribers(specName, e.getMessage());
		}
		
		
					
//		reportError.setErrorList(errorMessages);
//		t.getAny().add(reportError);
		
		
		

	}
	
	private void sendErrorMesssageToSubscribers(String specName, String errorMsg){
		
		Collection<Pair<Subscription, Notifier>> notifierCollection = eventCycleService.getNotifiers(specName);
		for (Pair<Subscription, Notifier> pair : notifierCollection  ){
			Notifier notifier = pair.getValue();
			ImplementationExceptionResponse implementationExceptionResponse = new ImplementationExceptionResponse(errorMsg); 
			notifier.notifyExceptionToSubscriber(implementationExceptionResponse);
		}
		
	}

	@Override
	public void launch() {
		this.running = true;
		// LOG.debug("launching eventCycle" + getName());
		synchronized (this) {
			this.notifyAll();
		}
	}

	/**
	 * This method returns all reports of this event cycle as event cycle
	 * reports.
	 * 
	 * @return array of ec reports
	 * @throws ECSpecValidationException
	 *             if a tag of this report is not valid
	 * @throws ImplementationException
	 *             if an implementation exception occurs.
	 */
	private List<ECReport> getReportList() throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse {
		ArrayList<ECReport> ecReports = new ArrayList<ECReport>();
		for (Report report : this.reports.values()) {
			ECReport r = report.getECReport();
			if (null != r)
				ecReports.add(r);
		}
		return ecReports;
	}

	/**
	 * This method returns the duration value extracted from the event cycle
	 * specification.
	 * 
	 * @return duration value in milliseconds
	 * @throws ImplementationException
	 *             if an implementation exception occurs
	 */
	private long getDurationValue() throws ImplementationExceptionResponse {
		if (spec.getBoundarySpec() != null) {
			ECTime duration = spec.getBoundarySpec().getDuration();
			if (duration != null) {
				if (duration.getUnit().compareToIgnoreCase(ECTimeUnit.MS) == 0) {
					return duration.getValue();
				} else {
					throw new ImplementationExceptionResponse("The only ECTimeUnit allowed is milliseconds (MS).");
				}
			}
		}
		return -1;
	}

	/**
	 * This method returns the repeat period value on the basis of the event
	 * cycle specification.
	 * 
	 * @return repeat period value
	 * @throws ImplementationException
	 *             if the time unit in use is unknown
	 */
	private long getRepeatPeriodValue() throws ImplementationExceptionResponse {
		if (spec.getBoundarySpec() != null) {
			ECTime repeatPeriod = spec.getBoundarySpec().getRepeatPeriod();
			if (repeatPeriod != null) {
				if (repeatPeriod.getUnit().compareToIgnoreCase(ECTimeUnit.MS) != 0) {
					throw new ImplementationExceptionResponse("The only ECTimeUnit allowed is milliseconds (MS).");
				} else {
					return repeatPeriod.getValue();
				}
			}
		}
		return -1;
	}

	// @Override
	public Set<Tag> getLastEventCycleTags() {
		return copyContentToNewDatastructure(lastEventCycleTags);
	}

	// @Override
	public Set<Tag> getTags() {
		return copyContentToNewDatastructure(tags);
	}

	/**
	 * create a copy of the content of the given data structure -> we use
	 * synchronized sets -> make sure not to leak them.<br/>
	 * this method synchronizes the original data structure during to copy
	 * process. <br/>
	 * <strong>Notice that the content is NOT cloned, simply
	 * referenced!</strong>
	 * 
	 * @param contentToCopy
	 *            the data structure to copy.
	 * @return a copy of the data structure with the content of the input.
	 */
	private Set<Tag> copyContentToNewDatastructure(Set<Tag> contentToCopy) {
		Set<Tag> copy = new HashSet<Tag>();
		synchronized (contentToCopy) {
			for (Tag tag : contentToCopy) {
				copy.add(tag);
			}
		}
		return copy;
	}

	private boolean isRejectTagsBetweenCycle() {
		return rejectTagsBetweenCycle;
	}

	private void setRejectTagsBetweenCycle(boolean rejectTagsBetweenCycle) {
		this.rejectTagsBetweenCycle = rejectTagsBetweenCycle;
	}

	/**
	 * tells whether the ec accepts tags.
	 * 
	 * @return boolean telling whether the ec accepts tags
	 */
	private boolean isAcceptingTags() {
		return acceptTags;
	}

	/**
	 * sets the flag acceptTags to the passed boolean value.
	 * 
	 * @param acceptTags
	 *            sets the flag acceptTags to the passed boolean value.
	 */
	private void setAcceptTags(boolean acceptTags) {
		this.acceptTags = acceptTags;
	}

	// @Override
	public int getRounds() {
		return rounds;
	}

	// @Override
	public void join() throws InterruptedException {
		synchronized (lock) {
			while (!isRoundOver()) {
				lock.wait();
			}
		}
	}

	/**
	 * whether the event cycle round is over. <strong>notice that this method is
	 * not exported via interface</strong>.
	 * 
	 * @return true if over, false otherwise
	 */
	public boolean isRoundOver() {
		return roundOver;
	}

	// FIXME: Implementation is currently leaking... need to do something.
	// @Override
	public ECReportSpec getReportSpecByName(String name) {
		return reportSpecByName.get(name);
	}

	/**
	 * get a handle onto the map holding all the report specs.
	 * 
	 * @return the map.
	 */
	protected Map<String, ECReportSpec> getReportSpecByName() {
		return reportSpecByName;
	}

	// FIXME: Implementation is currently leaking... need to do something.
	// @Override
	public Map<String, ECReport> getLastReports() {
		return lastReports;
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
	public void onNext(TagReadEvent entry) {

		Tag tag = new Tag(entry.getTag().getID().toString(), entry.getReaderID(), entry.getAntennaID(), null,
				entry.getTag().getFormattedID(), entry.getTimestamp());
		tags.add(tag);
		handleTags(tags);
	}

	/**
	 * check if a given ECReport contains at least one tag in its data
	 * structures.
	 * 
	 * @param r
	 *            the report to check.
	 * @return true if tags contained, false otherwise.
	 */
	private boolean hasTags(ECReport r) {
		try {
			for (ECReportGroup g : r.getGroup()) {
				if (g.getGroupList().getMember().size() > 0) {
					return true;
				}
			}
		} catch (Exception ex) {
			LOG.debug("could not check for tag occurence - report considered not to containing tags", ex);
		}
		return false;
	}

	public Subscription subscribeObserver(Observer<ECReports> observer) {

		synchronized (this) {

			if (!this.observers.contains(observer)) {
				this.observers.add(observer);
				System.out.println("EventCycle subscribeObserver");
			}

			this.notifyAll();

			return new Unsubscriber(this.observers, observer);
		}
	}
	
	public Unsubscriber unsubscribeObserver(Observer<ECReports> observer) {

		synchronized (this) {

			if (this.observers.contains(observer)) {
				this.observers.remove(observer);
				System.out.println("EventCycle unsubscribeObserver");
			}

			this.notifyAll();

			return new Unsubscriber(this.observers, observer);
		}
	}

	class Unsubscriber implements Subscription {
		private List<Observer<ECReports>> _observers;
		private Observer<ECReports> _observer;

		public Unsubscriber(List<Observer<ECReports>> observers, Observer<ECReports> observer) {
			this._observers = observers;
			this._observer = observer;
		}

		@Override
		public boolean isUnsubscribed() {
			// TODO Auto-generated method stub
			if (this._observer != null && !this._observers.contains(this._observer))
				return true;
			return false;
		}

		@Override
		public void unsubscribe() {
			// TODO Auto-generated method stub
			if (this._observer != null && this._observers.contains(this._observer)) {
				this._observers.remove(_observer);
			}
		}

	}

	@Override
	public ECSpec getECSpec() {
		// TODO Auto-generated method stub
		return ecReports.getECSpec();
	}

	@Override
	public void setECSpec(ECSpec ecSpec) {
		// TODO Auto-generated method stub
		ecReports.setECSpec(ecSpec);
	}

	@Override
	public void poll() {
		// TODO Auto-generated method stub
		// LOG.debug("Spec '" + name + "' polled.");
		pollReport = null;
		polling = true;
		if (isStateUnRequested()) {
			setState(State.REQUESTED);
		}

	}

	public void setState(State state) {
		State oldState = this.state;
		this.state = state;

		LOG.debug("ReportGenerator '" + name + "' change state from '" + oldState + "' to '" + state + "'");

		// FIXME ALE
		// if (isStateRequested() && !isRunning()) {
		// start();
		// } else if (isStateUnRequested() && isRunning()) {
		// stop();
		// }
	}

	// @Override
	public boolean isStateUnRequested() {
		return state == State.UNREQUESTED;
	}

	protected boolean isPolling() {
		return polling;
	}

	@Override
	public ECReports getPollReports() {
		return pollReport;
	}

	@Override
	public void limpiarTags() {
		tags = Collections.synchronizedSet(new HashSet<Tag>());
	}

	private StateMachineConfig<State, Trigger> lifecycle = new StateMachineConfig<>();

	private enum State {
		UNREQUESTED, REQUESTED, ACTIVE

	}

	private enum Trigger {
		Undefine, Subscribe, Unsubscribe, Poll, StartTrigger, StopTrigger, RepeatPeriod

	}

}

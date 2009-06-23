package org.rifidi.edge.epcglobal.aleread.wrappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.ale.esper.SignalListener;
import org.rifidi.edge.ale.esper.StatementController;
import org.rifidi.edge.ale.esper.starters.IntervalTimingStatement;
import org.rifidi.edge.ale.esper.starters.StartEventStatement;
import org.rifidi.edge.ale.esper.stoppers.DurationTimingStatement;
import org.rifidi.edge.ale.esper.stoppers.StableSetTimingStatement;
import org.rifidi.edge.ale.esper.stoppers.StopEventTimingStatement;
import org.rifidi.edge.ale.esper.timer.Timer;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.ALEReadAPI;
import org.rifidi.edge.lr.LogicalReader;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;

/**
 * Class for managing the lifetime of an ECSpec. TODO: check if the esper engine
 * is still valid, mind your OSGi ;)
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class RifidiECSpec implements SignalListener {
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(RifidiECSpec.class);
	/** Fields in here constitute the primary keys for the tags. */
	private Set<String> primarykeys;
	/** The spec this object was created from. */
	private ECSpec spec;
	/** Name of the spec. */
	private String name;
	/** Esper instance to use. */
	private EPServiceProvider esper;
	/** Readers used by this spec. */
	private Set<LogicalReader> readers;
	/** Boundary spec for this spec. */
	private RifidiBoundarySpec rifidiBoundarySpec;
	/** Threadsafe list for subscription uris. */
	private List<String> subscriptionURIs;
	/** All statements that got created. */
	private List<StatementController> startStatementControllers;
	private List<StatementController> stopStatementControllers;
	private List<EPStatement> collectionStatements;

	private ReentrantLock startLock;
	/**
	 * Only false if a start event was specified and we need to wait for it to
	 * happen.
	 */
	private Boolean instantStart = true;
	private Boolean running = false;
	private Boolean restart = false;

	/** The result currently being processed */
	private ResultContainer currentResult;
	/** The result being processed next */
	private ResultContainer nextResult;
	/** Sends out the data. */
	private ReportSender sender;
	/** Sender runs in this thread. */
	private Thread senderThread;
	/** Sender for the start/stop triggers. */
	private Timer timer;

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param spec
	 * @param esper
	 * @param rifidiBoundarySpec
	 * @param readers
	 * @param primarykeys
	 * @param reports
	 * @throws InvalidURIExceptionResponse
	 * @throws ECSpecValidationExceptionResponse
	 */
	public RifidiECSpec(final String name, final ECSpec spec,
			final EPServiceProvider esper,
			final RifidiBoundarySpec rifidiBoundarySpec,
			final Set<LogicalReader> readers, final Set<String> primarykeys,
			final List<RifidiReport> reports) {
		this.startLock = new ReentrantLock();
		this.rifidiBoundarySpec = rifidiBoundarySpec;
		this.spec = spec;
		this.name = name;
		this.esper = esper;
		this.readers = new HashSet<LogicalReader>();
		this.readers.addAll(readers);
		// configure the report sender
		this.sender = new ReportSender(reports, name, spec);

		// collect the primary keys
		this.primarykeys = new HashSet<String>();
		if (this.primarykeys.isEmpty()) {
			this.primarykeys.add("epc");
		}
		this.subscriptionURIs = new CopyOnWriteArrayList<String>();

		startStatementControllers = new ArrayList<StatementController>();
		stopStatementControllers = new ArrayList<StatementController>();
		collectionStatements = new ArrayList<EPStatement>();
		for (LogicalReader reader : readers) {
			collectionStatements.add(esper.getEPAdministrator().createEPL(
					"insert into LogicalReader select * from "
							+ reader.getName()));
		}
		// add a timer if we got triggers
		if (rifidiBoundarySpec.getStartTriggers().size() != 0
				|| rifidiBoundarySpec.getStopTriggers() != null) {
			timer = new Timer(rifidiBoundarySpec.getStartTriggers(),
					rifidiBoundarySpec.getStopTriggers(), esper);
		}
		// TODO: When data available needs to be added ASAP!!!
		if (rifidiBoundarySpec.isWhenDataAvailable()) {
			logger.fatal("'When data available' not yet implemented!");
		}
		if (rifidiBoundarySpec.getDuration() > 0) {
			logger.debug("Initializing duration timing with duration="
					+ rifidiBoundarySpec.getDuration());
			DurationTimingStatement durationTimingStatement = new DurationTimingStatement(
					esper.getEPAdministrator(), rifidiBoundarySpec
							.getDuration(), primarykeys);
			durationTimingStatement.registerSignalListener(this);
			stopStatementControllers.add(durationTimingStatement);
		}
		if (rifidiBoundarySpec.getStableSetInterval() > 0) {
			logger
					.debug("Initializing 'stable set interval' timing with interval="
							+ rifidiBoundarySpec.getStableSetInterval());
			StableSetTimingStatement stableSetTimingStatement = new StableSetTimingStatement(
					esper.getEPAdministrator(), rifidiBoundarySpec
							.getStableSetInterval(), primarykeys);
			stableSetTimingStatement.registerSignalListener(this);
			stopStatementControllers.add(stableSetTimingStatement);
		}
		if (rifidiBoundarySpec.getStopTriggers().size() > 0) {
			// TODO: create triggers
			StopEventTimingStatement stopEventTimingStatement = new StopEventTimingStatement(
					esper.getEPAdministrator(), primarykeys);
			stopEventTimingStatement.registerSignalListener(this);
			stopStatementControllers.add(stopEventTimingStatement);
		}
		if (rifidiBoundarySpec.getStartTriggers().size() > 0) {
			instantStart = false;
			StartEventStatement startEventStatement = new StartEventStatement(
					esper.getEPAdministrator());
			startEventStatement.registerSignalListener(this);
			startEventStatement.start();
			startStatementControllers.add(startEventStatement);
		}
		if (rifidiBoundarySpec.getRepeatInterval() > 0) {
			IntervalTimingStatement intervalTimingStatement = new IntervalTimingStatement(
					esper.getEPAdministrator(), rifidiBoundarySpec
							.getRepeatInterval());
			intervalTimingStatement.registerSignalListener(this);
			startStatementControllers.add(intervalTimingStatement);
		}
	}

	/**
	 * Helper method for generating a String to be used in the logical reader
	 * statement.
	 * 
	 * @param primarykeys
	 * @return
	 */
	protected String assembleLogicalReader(Set<LogicalReader> readers) {
		StringBuilder builder = new StringBuilder();
		for (LogicalReader reader : readers) {
			builder.append(reader.getName());
			builder.append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	/**
	 * Start a new event cycle.
	 */
	private void startNewCycle() {
		logger.debug("starting new cycle");
		running = true;
		restart = false;
		// swap the result containers
		currentResult = nextResult;
		nextResult = null;
		currentResult.startTime = System.currentTimeMillis();
		// start the collectors
		for (StatementController ctrl : stopStatementControllers) {
			if (ctrl.needsRestart()) {
				ctrl.start();
			}
		}
		// start the statements for processing start signals
		for (StatementController ctrl : startStatementControllers) {
			if (ctrl.needsRestart()) {
				ctrl.start();
			}
		}
	}

	/**
	 * Start a new event cycle after a start signal has arrived. Only used when
	 * a start trigger was provided as the event cycle has to wait until that
	 * event occurs. Under all other circumstances the eventcycle start
	 * instantly.
	 */
	private void startNewCycleAfterStartSignal() {
		for (StatementController ctrl : startStatementControllers) {
			if (ctrl.needsRestart()) {
				ctrl.start();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.ale.esper.SignalListener#startSignal(ALEReadAPI.
	 * TriggerCondition, java.lang.Object)
	 */
	@Override
	public void startSignal(ALEReadAPI.TriggerCondition type, Object cause) {
		logger.trace("start signal received");
		startLock.lock();
		logger.trace("start signal: locked");
		try {
			// ignore start signals if the system can already start again
			if (running && restart == false) {
				logger.debug("Running, marking for restart.");
				// we received a start signal so we can stop all start
				// statements
				// that require a restart
				for (StatementController ctrl : startStatementControllers) {
					if (ctrl.needsRestart()) {
						ctrl.stop();
					}
				}
				if (nextResult == null) {
					nextResult = new ResultContainer(type, cause);
				}
				restart = true;
			} else if (!running) {
				logger.debug("Not running, restarting.");
				restart = false;
				if (nextResult == null) {
					nextResult = new ResultContainer(type, cause);
				}
				startNewCycle();
			}
		} finally {
			startLock.unlock();
			logger.trace("start signal: unlocked");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.ale.esper.SignalListener#stopSignal(ALEReadAPI.
	 * TriggerCondition, java.lang.Object, java.util.List)
	 */
	@Override
	public void stopSignal(ALEReadAPI.TriggerCondition type, Object cause,
			List<TagReadEvent> events) {
		logger.trace("stop signal received");
		startLock.lock();
		logger.trace("stop signal: locked");
		try {
			if (running) {
				running = false;
				currentResult.stopTime = System.currentTimeMillis();
				// stop all currently executing statements
				for (StatementController ctrl : stopStatementControllers) {
					if (ctrl.needsRestart()) {
						ctrl.stop();
					}
				}
				currentResult.stopReason = type;
				currentResult.stopCause = cause;
				currentResult.events = events;
				sender.enqueueResultContainer(currentResult);
				currentResult = null;
				if (!ALEReadAPI.TriggerCondition.UNDEFINE.equals(type)
						&& restart) {
					// only restart if we didn't get a destroy event
					startNewCycle();
				}
			}
		} finally {
			startLock.unlock();
			logger.trace("stop signal: unlocked");
		}
	}

	/**
	 * Shoot it, kill it, destroy it. And because java doesn't have friends I
	 * need this method to be declared public. Why!Why!Why!Why!Why! The one
	 * thing that C++ got right :(
	 */
	public void destroy() {
		senderThread.interrupt();
		if (timer != null) {
			timer.destroy();
		}
		// start the collectors
		for (StatementController ctrl : stopStatementControllers) {
			ctrl.stop();
		}
		// start the statements for processing start signals
		for (StatementController ctrl : startStatementControllers) {
			ctrl.stop();
		}
	}

	/**
	 * Subscribe the given URI to this spec.
	 * 
	 * @param uri
	 * @throws DuplicateSubscriptionExceptionResponse
	 */
	public void subscribe(String uri)
			throws DuplicateSubscriptionExceptionResponse {
		if (subscriptionURIs.contains(uri)) {
			throw new DuplicateSubscriptionExceptionResponse("Uri " + uri
					+ " is already subscribed.");
		}
		synchronized (subscriptionURIs) {
			subscriptionURIs.add(uri);
			sender.subscribe(uri);
			if (subscriptionURIs.size() == 1) {
				senderThread = new Thread(sender);
				senderThread.start();
				if (instantStart) {
					nextResult = new ResultContainer(
							ALEReadAPI.TriggerCondition.REQUESTED, null);
					startNewCycle();
				} else {
					startNewCycleAfterStartSignal();
				}
			}
		}
	}

	/**
	 * Unsubscribe the given URI from this spec.
	 * 
	 * @param uri
	 * @throws NoSuchSubscriberExceptionResponse
	 */
	public void unsubscribe(String uri)
			throws NoSuchSubscriberExceptionResponse {
		if (!subscriptionURIs.contains(uri)) {
			throw new NoSuchSubscriberExceptionResponse("Uri " + uri
					+ " is not subscribed to this spec.");
		}
		synchronized (subscriptionURIs) {
			subscriptionURIs.remove(uri);
			sender.unsubscribe(uri);
			if (subscriptionURIs.size() == 0) {
				logger.debug("Stopping ECSpec: " + name);
				senderThread.interrupt();
				// start the collectors
				for (StatementController ctrl : stopStatementControllers) {
					ctrl.stop();
				}
				// start the statements for processing start signals
				for (StatementController ctrl : startStatementControllers) {
					ctrl.stop();
				}
			}
		}
	}

	/**
	 * Get all subscription URIs for this spec.
	 * 
	 * @return
	 */
	public List<String> getSubscriptions() {
		return new ArrayList<String>(subscriptionURIs);
	}

	/**
	 * @return the rifidiBoundarySpec
	 */
	public RifidiBoundarySpec getRifidiBoundarySpec() {
		return rifidiBoundarySpec;
	}

	/**
	 * @return the spec
	 */
	public ECSpec getSpec() {
		return spec;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Class that holds the results of an event cycle.
	 * 
	 * @author Jochen Mader - jochen@pramari.com
	 * 
	 */
	public class ResultContainer {
		public ALEReadAPI.TriggerCondition startReason;
		public Object startCause;
		public ALEReadAPI.TriggerCondition stopReason;
		public Object stopCause;
		public List<TagReadEvent> events;
		public long startTime;
		public long stopTime;

		/**
		 * Constructor.
		 * 
		 * @param startReason
		 * @param startCause
		 */
		public ResultContainer(ALEReadAPI.TriggerCondition startReason,
				Object startCause) {
			super();
			this.startReason = startReason;
			this.startCause = startCause;
		}
	}
}
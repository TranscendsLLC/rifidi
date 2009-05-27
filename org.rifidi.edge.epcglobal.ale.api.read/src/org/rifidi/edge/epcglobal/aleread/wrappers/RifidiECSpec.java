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
import org.rifidi.edge.core.messages.TagReadEvent;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.ECReportmanager;
import org.rifidi.edge.esper.events.DestroyEvent;
import org.rifidi.edge.lr.LogicalReader;
import org.rifidi.edge.lr.LogicalReaderManagementService;

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
	public static final int STARTREASON_INTERVAL = 1;
	public static final int STARTREASON_STARTEVENT = 2;
	public static final int STOPREASON_DURATION = 3;
	public static final int STOPREASON_STABLESET = 4;
	public static final int STOPREASON_STOPEVENT = 5;
	public static final int STOPREASON_DESTROYEVENT = 6;

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
	/** TODO: this needs fixing as the service might go away and reappear!!! */
	private LogicalReaderManagementService lrService;
	/** Readers used by this spec. */
	private Set<LogicalReader> readers;
	/** RifidiReport manager for this spec. */
	private ECReportmanager ecReportmanager = null;
	/** Thread that runs the reportmanager. */
	private Thread reportThread = null;
	/** Boundary spec for this spec. */
	private RifidiBoundarySpec rifidiBoundarySpec;
	/** Threadsafe list for subscription uri. */
	private List<String> subscriptionURIs;

	private List<StatementController> startStatementControllers;
	private List<StatementController> stopStatementControllers;
	private EPStatement collectionStatement;

	private ReentrantLock startLock;
	/**
	 * Indicates if after an event cycle ends we need to wait for a start
	 * signal.
	 */
	private Boolean needsStartCondition = false;
	/**
	 * Only false if a start event was specified and we need to wait for it to
	 * happen.
	 */
	private Boolean instantStart = true;
	private Boolean running = false;
	private Boolean restart = false;

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

		this.primarykeys = new HashSet<String>();
		if (this.primarykeys.isEmpty()) {
			this.primarykeys.add("epc");
		}
		this.subscriptionURIs = new CopyOnWriteArrayList<String>();

		startStatementControllers = new ArrayList<StatementController>();
		stopStatementControllers = new ArrayList<StatementController>();

		collectionStatement = esper.getEPAdministrator().createEPL(
				"insert into LogicalReader select * from "
						+ assembleLogicalReader(readers));
		if (rifidiBoundarySpec.isWhenDataAvailable()) {
			logger.fatal("'When data available' not yet implemented!");
		}
		if (rifidiBoundarySpec.getDuration() > 0) {
			logger.debug("Initializing duration timing with duration="
					+ rifidiBoundarySpec.getDuration());
			DurationTimingStatement durationTimingStatement = new DurationTimingStatement(
					esper.getEPAdministrator(), rifidiBoundarySpec
							.getDuration(), primarykeys);
			stopStatementControllers.add(durationTimingStatement);
		}
		if (rifidiBoundarySpec.getStableSetInterval() > 0) {
			logger
					.debug("Initializing 'stable set interval' timing with interval="
							+ rifidiBoundarySpec.getStableSetInterval());
			StableSetTimingStatement stableSetTimingStatement = new StableSetTimingStatement(
					esper.getEPAdministrator(), rifidiBoundarySpec
							.getStableSetInterval(), primarykeys);
			stopStatementControllers.add(stableSetTimingStatement);
		}
		if (rifidiBoundarySpec.getStopTriggers().size() > 0) {
			// TODO: create triggers
			StopEventTimingStatement stopEventTimingStatement = new StopEventTimingStatement(
					esper.getEPAdministrator(), primarykeys);
			stopStatementControllers.add(stopEventTimingStatement);
		}
		if (rifidiBoundarySpec.getStartTriggers().size() > 0) {
			needsStartCondition = true;
			instantStart = false;
			StartEventStatement startEventStatement = new StartEventStatement(
					esper.getEPAdministrator());
			startStatementControllers.add(startEventStatement);
		}
		if (rifidiBoundarySpec.getRepeatInterval() > 0) {
			needsStartCondition = true;
			IntervalTimingStatement intervalTimingStatement = new IntervalTimingStatement(
					esper.getEPAdministrator(), rifidiBoundarySpec
							.getRepeatInterval());
			startStatementControllers.add(intervalTimingStatement);
		}

		// if (!spec.isIncludeSpecInReports()) {
		// ecReportmanager = new ECReportmanager(name, esper, reports, this,
		// subscriptionURIs);
		// } else {
		// ecReportmanager = new ECReportmanager(name, esper, spec, reports,
		// this, subscriptionURIs);
		// }
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
		running = true;
		restart = false;
		for (StatementController ctrl : stopStatementControllers) {
			if (ctrl.needsRestart()) {
				ctrl.start();
			}
		}
		for (StatementController ctrl : startStatementControllers) {
			if (ctrl.needsRestart()) {
				ctrl.start();
			}
		}
	}

	/**
	 * Start a new event cycle after a start signal has arrived.
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
	 * @see org.rifidi.edge.ale.esper.SignalListener#startSignal(int,
	 * java.lang.Object)
	 */
	@Override
	public void startSignal(int type, Object cause) {
		startLock.lock();
		try {
			// system is waiting for a start signal
			if (type == STARTREASON_INTERVAL) {
				System.out.println("interval " + System.currentTimeMillis()
						/ 1000);
			}
			if (type == STARTREASON_STARTEVENT) {
			}
			// we received a start signal so we can stop all statements that
			// require a restart
			for (StatementController ctrl : startStatementControllers) {
				if (ctrl.needsRestart()) {
					ctrl.stop();
				}
			}
			if (running) {
				restart = true;
			} else {
				restart = false;
				startNewCycle();
			}
			// ignore start signals if the system can already start again
		} finally {
			startLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.esper.SignalListener#stopSignal(int,
	 * java.lang.Object, java.util.List)
	 */
	@Override
	public void stopSignal(int type, Object cause, List<TagReadEvent> events) {
		startLock.lock();
		try {
			// stop all currently executing statements
			for (StatementController ctrl : stopStatementControllers) {
				if (ctrl.needsRestart()) {
					ctrl.stop();
				}
			}
			if (type == STOPREASON_DURATION) {
				System.out.println("duration " + System.currentTimeMillis()
						/ 1000);
				for (TagReadEvent event : (List<TagReadEvent>) events) {
					System.out.println(event);
				}
			}
			if (type == STOPREASON_STABLESET) {
				System.out.println("stableset");
				for (TagReadEvent event : (List<TagReadEvent>) events) {
					System.out.println(event);
				}
			}
			if (type == STOPREASON_STOPEVENT) {
				System.out.println("stopevent " + cause);
				for (TagReadEvent event : (List<TagReadEvent>) events) {
					System.out.println(event);
				}
			}
			if (type == STOPREASON_DESTROYEVENT) {
				System.out.println("destroyevent " + cause);
				for (TagReadEvent event : (List<TagReadEvent>) events) {
					System.out.println(event);
				}
			} else if (restart) {
				// only restart if we didn't get a destroy event
				startNewCycle();
			}
		} finally {
			startLock.unlock();
		}
	}

	/**
	 * Destroy the spec and clean up.
	 */
	public void destroy() {
		esper.getEPRuntime().sendEvent(new DestroyEvent(name));
	}

	/**
	 * Shoot it, kill it, destroy it. And because java doesn't have friends I
	 * need this method to be declared public. Why!Why!Why!Why!Why! The one
	 * thing that C++ got right :(
	 */
	public void discard() {
	}

	/**
	 * Subscribe the given URI to this spec.
	 * 
	 * @param uri
	 * @throws DuplicateSubscriptionExceptionResponse
	 */
	public void subscribe(String uri)
			throws DuplicateSubscriptionExceptionResponse {
		synchronized (subscriptionURIs) {
			if (subscriptionURIs.contains(uri)) {
				throw new DuplicateSubscriptionExceptionResponse("Uri " + uri
						+ " is already subscribed.");
			}
			subscriptionURIs.add(uri);
			if (subscriptionURIs.size() == 1) {
				if (instantStart) {
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
		synchronized (subscriptionURIs) {
			if (!subscriptionURIs.contains(uri)) {
				throw new NoSuchSubscriberExceptionResponse("Uri " + uri
						+ " is not subscribed to this spec.");
			}
			subscriptionURIs.remove(uri);
			if (subscriptionURIs.size() == 0) {
				destroy();
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
}
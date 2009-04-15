package org.rifidi.edge.epcglobal.aleread.wrappers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportSpec;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.ECReportmanager;
import org.rifidi.edge.epcglobal.aleread.StartEvent;
import org.rifidi.edge.epcglobal.aleread.Trigger;
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
public class RifidiECSpec {
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
	/** Threadpool for executing the scheduled triggers. */
	private ScheduledExecutorService triggerpool;
	/** Statements created for this spec. */
	private ArrayList<EPStatement> statements;
	/** Set containing all the scheduled triggers futures. */
	private Set<ScheduledFuture<?>> futures;
	/** TODO: this needs fixing as the service might go away and reappear!!! */
	private LogicalReaderManagementService lrService;
	/** Readers used by this spec. */
	private Set<LogicalReader> readers;

	/** List containing statements that trigger when interval ran out. */
	private List<EPStatement> intervalStatements;
	/** List containing statements that trigger when the tag set is stable. */
	private List<EPStatement> stableSetIntervalStatements;
	/** List containing statements that trigger when a stop trigger arrived. */
	private List<EPStatement> stopTriggerStatements;
	/** List containing statements that trigger when data is available. */
	private List<EPStatement> whenDataAvailableStatements;
	/** RifidiReport manager for this spec. */
	private ECReportmanager ecReportmanager = null;
	/** Thread that runs the reportmanager. */
	private Thread reportThread = null;

	/** Boundary spec for this spec. */
	private RifidiBoundarySpec rifidiBoundarySpec;

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param spec
	 * @param esper
	 * @param triggerpool
	 * @param rifidiBoundarySpec
	 * @param readers
	 * @param primarykeys
	 * @param reports
	 * @throws InvalidURIExceptionResponse
	 * @throws ECSpecValidationExceptionResponse
	 */
	public RifidiECSpec(String name, ECSpec spec, EPServiceProvider esper,
			ScheduledExecutorService triggerpool,
			RifidiBoundarySpec rifidiBoundarySpec, Set<LogicalReader> readers,
			Set<String> primarykeys, List<RifidiReport> reports) {
		this.rifidiBoundarySpec = rifidiBoundarySpec;
		this.triggerpool = triggerpool;
		this.spec = spec;
		this.name = name;
		this.statements = new ArrayList<EPStatement>();
		this.futures = new HashSet<ScheduledFuture<?>>();
		this.esper = esper;
		this.readers = readers;
		this.primarykeys = primarykeys;
		whenDataAvailableStatements = new ArrayList<EPStatement>();
		intervalStatements = new ArrayList<EPStatement>();
		stopTriggerStatements = new ArrayList<EPStatement>();
		stableSetIntervalStatements = new ArrayList<EPStatement>();
		readers = new HashSet<LogicalReader>();
		if (!spec.isIncludeSpecInReports()) {
			ecReportmanager = new ECReportmanager(name, esper, reports);
		} else {
			ecReportmanager = new ECReportmanager(name, esper, spec, reports);
		}

		for (ECReportSpec reportSpec : spec.getReportSpecs().getReportSpec()) {
			ecReportmanager.addECReport(reportSpec);
		}
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
	 * Helper method for filling the given set with the names of physicals
	 * readers.
	 * 
	 * @param readerNames
	 */
	private void getReaderNames(LogicalReader reader, Set<String> readerNames) {
		if (reader.getReaders().size() == 0) {
			readerNames.add(reader.getName());
			return;
		}
		for (LogicalReader subReader : reader.getReaders()) {
			getReaderNames(subReader, readerNames);
		}
	}

	/**
	 * Start the spec.
	 */
	public void start() {
		synchronized (statements) {
			if (statements.size() == 0) {
				HashSet<String> readerNames = new HashSet<String>();
				for (LogicalReader reader : readers) {
					getReaderNames(reader, readerNames);
				}

				// SETUP
				// create the window window
				statements
						.add(esper
								.getEPAdministrator()
								.createEPL(
										"create window "
												+ name
												+ "_collectwin.win:time("
												+ (rifidiBoundarySpec
														.getDuration() > rifidiBoundarySpec
														.getRepeatInterval() ? rifidiBoundarySpec
														.getDuration()
														: rifidiBoundarySpec
																.getRepeatInterval())
												+ " msec) org.rifidi.edge.core.messages.TagReadEvent"));
				// create the where conditions for primary fields
				StringBuilder prims = new StringBuilder();
				boolean first = true;
				for (String key : primarykeys) {
					if (!first) {
						prims.append(" and");
						first = false;
					}
					// direct memory access
					if (key.startsWith("@")) {
						prims.append(" collected.tag.readMemory('");
						prims.append(key);
						prims.append("')=tagevent.tag.readMemory('");
						prims.append(key);
						prims.append("')");
					} else {
						prims.append(" collected.tag.");
						prims.append(key);
						prims.append("?=tagevent.tag.");
						prims.append(key);
						prims.append("?");
					}
				}

				StringBuilder readersBuilder = new StringBuilder("( ");
				for (String readerName : readerNames) {
					readersBuilder.append("'");
					readersBuilder.append(readerName);
					readersBuilder.append("'");
					readersBuilder.append(",");
				}
				// remove trailing commas
				readersBuilder.deleteCharAt(readersBuilder.length() - 1);
				readersBuilder.append(" )");
				// fill the window
				statements
						.add(esper
								.getEPAdministrator()
								.createEPL(
										"insert into "
												+ name
												+ "_collectwin select * from org.rifidi.edge.core.messages.TagReadEvent as tagevent where "
												+ "not exists (select * from "
												+ name
												+ "_collectwin as collected where "
												+ prims.toString()
												+ ") and readerID in "
												+ readersBuilder.toString()));
				// INTERVAL TIMING
				// regular timing using intervals, don't use if data available
				// is set
				if (!rifidiBoundarySpec.isWhenDataAvailable()) {
					intervalStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on pattern[every org.rifidi.edge.epcglobal.aleread.StartEvent -> (timer:interval("
													+ rifidiBoundarySpec
															.getDuration()
													+ " msec) and not org.rifidi.edge.epcglobal.aleread.StopEvent)] select tags from "
													+ name
													+ "_collectwin as tags"));
				} else {
					// WHEN DATA AVAILABLE
					// timing for when data available, should replace regular
					// timing if
					// provided!!!
					whenDataAvailableStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on pattern[every (org.rifidi.edge.epcglobal.aleread.StartEvent -> "
													+ name
													+ "_collectwin where timer:within("
													+ rifidiBoundarySpec
															.getDuration()
													+ " msec))] select count(whine) from "
													+ name
													+ "_collectwin as whine"));

					whenDataAvailableStatements.add(esper.getEPAdministrator()
							.createEPL(
									"on pattern[every (org.rifidi.edge.epcglobal.aleread.StartEvent -> "
											+ name
											+ "_collectwin where timer:within("
											+ rifidiBoundarySpec.getDuration()
											+ " msec))] select tags from "
											+ name + "_collectwin as tags"));

				}

				// triggers if a startevent arrives without followup processed
				// events
				// needs to be used with both the regular and the data available
				// queries
				intervalStatements
						.add(esper
								.getEPAdministrator()
								.createEPL(
										"on pattern[every org.rifidi.edge.epcglobal.aleread.StartEvent -> (timer:interval("
												+ rifidiBoundarySpec
														.getDuration()
												+ " msec) and not "
												+ name
												+ "_collectwin and not org.rifidi.edge.epcglobal.aleread.StopEvent)] select count(whine) from "
												+ name + "_collectwin as whine"));
				// STOP TRIGGERS
				if (rifidiBoundarySpec.getStopTriggers().size() > 0) {
					// timing using a stop trigger
					stopTriggerStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on pattern[every (org.rifidi.edge.epcglobal.aleread.StartEvent -> [0..] "
													+ name
													+ "_collectwin until (timer:interval("
													+ rifidiBoundarySpec
															.getDuration()
													+ " msec) and org.rifidi.edge.epcglobal.aleread.StopEvent))] select count(whine) from "
													+ name
													+ "_collectwin as whine"));

					stopTriggerStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on pattern[every (org.rifidi.edge.epcglobal.aleread.StartEvent -> [0..] "
													+ name
													+ "_collectwin until (timer:interval("
													+ rifidiBoundarySpec
															.getDuration()
													+ " msec) and org.rifidi.edge.epcglobal.aleread.StopEvent))] select tags from "
													+ name
													+ "_collectwin as tags"));

				}

				if (rifidiBoundarySpec.getStableSetInterval() > 0) {
					// TODO: incorporate INTERVAL!!!!
					// timing using stable set
					stableSetIntervalStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on pattern[every (org.rifidi.edge.epcglobal.aleread.StartEvent -> "
													+ name
													+ "_collectwin -> (timer:interval("
													+ rifidiBoundarySpec
															.getStableSetInterval()
													+ " msec) and not "
													+ name
													+ "_collectwin))] select tags  from "
													+ name
													+ "_collectwin as tags"));

					stableSetIntervalStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on pattern[every (org.rifidi.edge.epcglobal.aleread.StartEvent -> (timer:interval("
													+ rifidiBoundarySpec
															.getStableSetInterval()
													+ " msec) and not "
													+ name
													+ "_collectwin))] select count(whine) from "
													+ name
													+ "_collectwin as whine"));

				}

				// clean up before collecting
				statements.add(esper.getEPAdministrator().createEPL(
						"on pattern[every org.rifidi.edge.epcglobal.aleread.StartEvent] delete from "
								+ name + "_collectwin"));

				esper.getEPRuntime().sendEvent(new StartEvent("buhu"));

				// debug output
				StringBuilder builder = new StringBuilder();
				builder.append("Statements:\n");
				for (EPStatement statement : statements) {
					builder.append(statement.getText() + "\n");
				}
				builder.append("Query Statements:\n");
				for (EPStatement statement : collectQueryStatements()) {
					builder.append(statement.getText() + "\n");
				}
				logger.debug("The following statements were created: \n"
						+ builder.toString());

				// Register statements to the manager.
				ecReportmanager.setIntervalStatements(intervalStatements);
				ecReportmanager
						.setStableSetIntervalStatements(stableSetIntervalStatements);
				ecReportmanager.setStopTriggerStatements(stopTriggerStatements);
				ecReportmanager
						.setWhenDataAvailableStatements(whenDataAvailableStatements);
				logger.debug("Spec " + getName() + " started.");
				// start the manager
				reportThread = new Thread(ecReportmanager);
				reportThread.start();
			}
		}

	}

	/**
	 * Collect all statements into one list. I KNOW IT'S REDUNDANT!
	 * 
	 * @return
	 */
	private List<EPStatement> collectQueryStatements() {
		ArrayList<EPStatement> ret = new ArrayList<EPStatement>();
		ret.addAll(intervalStatements);
		ret.addAll(stableSetIntervalStatements);
		ret.addAll(stopTriggerStatements);
		ret.addAll(whenDataAvailableStatements);
		return ret;
	}

	/**
	 * Stop the spec.
	 */
	public void stop() {
		synchronized (statements) {
			logger.debug("Spec " + getName() + " stopped.");
			reportThread.interrupt();
			try {
				reportThread.join();
			} catch (InterruptedException e1) {
				logger.warn("Got interrupted while waiting for report thread: "
						+ e1);
				Thread.currentThread().interrupt();
			}
			// kill all queries
			for (EPStatement statement : collectQueryStatements()) {
				statement.stop();
			}
			// reverse the collection to prevent inconsistencies while deleting
			Collections.reverse(new ArrayList<EPStatement>(statements));
			// destroy statements
			for (EPStatement statement : statements) {
				statement.stop();
			}
			// kill all triggers
			for (ScheduledFuture<?> future : futures) {
				try {
					future.cancel(true);
				} catch (Exception e) {
					logger.fatal("Unable to kill trigger: " + e);
				}
			}
			futures.clear();
		}
	}

	/**
	 * Destroy the spec and clean up.
	 */
	public void destroy() {
		synchronized (statements) {
			// make sure we are stopped
			stop();
			// destroy all statements
			for (EPStatement statement : collectQueryStatements()) {
				statement.destroy();
			}
			// reverse the collection to prevent inconsistencies while deleting
			Collections.reverse(statements);
			// destroy statements
			for (EPStatement statement : statements) {
				statement.destroy();
			}
			// release all readers that have already been aquired
			for (String reader : spec.getLogicalReaders().getLogicalReader()) {
				try {
					lrService.getLogicalReaderByName(reader).release(this);
				} catch (NoSuchNameExceptionResponse ex) {
					logger.debug("Reader " + reader + " doesn't exist");
				}
			}
			readers.clear();
		}
	}

	// TODO: THREADING!!!!
	private int uricounter = 0;

	public void subscribe(URI uri)
			throws DuplicateSubscriptionExceptionResponse {
		uricounter++;
		ecReportmanager.addURI(uri);
		if (uricounter == 1) {
			start();
		}
		// throw new DuplicateSubscriptionExceptionResponse(uri
		// + " is already subsribed.");
	}

	public void unsubscribe(URI uri) throws NoSuchSubscriberExceptionResponse {
		uricounter--;
		ecReportmanager.removeURI(uri);
		if (uricounter == 0) {
			stop();
		}
	}

	/**
	 * Prepare the spec for execution.
	 */
	public void init() {
		// schedule the start triggers
		if (rifidiBoundarySpec.getStartTriggers().size() != 0) {
			for (Trigger trigger : rifidiBoundarySpec.getStartTriggers()) {
				triggerpool.scheduleAtFixedRate(trigger, trigger
						.getDelayToNextExec(), trigger.getPeriod(),
						TimeUnit.MILLISECONDS);
			}
			logger.debug("Scheduled "
					+ rifidiBoundarySpec.getStartTriggers().size()
					+ " start triggers. ");
		} else {
			// if there are no start triggers the spec gets instantly
			// started
			start();
		}
		// schedule the stop triggers
		if (rifidiBoundarySpec.getStopTriggers().size() != 0) {
			for (Trigger trigger : rifidiBoundarySpec.getStopTriggers()) {
				triggerpool.scheduleAtFixedRate(trigger, trigger
						.getDelayToNextExec(), trigger.getPeriod(),
						TimeUnit.MILLISECONDS);
			}
			logger.debug("Scheduled "
					+ rifidiBoundarySpec.getStopTriggers().size()
					+ " start triggers. ");
		}
	}
}
package org.rifidi.edge.epcglobal.aleread.wrappers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.ECReportmanager;
import org.rifidi.edge.epcglobal.aleread.rifidievents.DestroyEvent;
import org.rifidi.edge.epcglobal.aleread.rifidievents.StartEvent;
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
	/** Statements created for this spec. */
	private ArrayList<EPStatement> collectionStatements;
	/** TODO: this needs fixing as the service might go away and reappear!!! */
	private LogicalReaderManagementService lrService;
	/** Readers used by this spec. */
	private Set<LogicalReader> readers;

	/** List containing statements that trigger when duration ran out. */
	private List<EPStatement> durationStatements;
	/** List containing statements that trigger when the tag set is stable. */
	private List<EPStatement> stableSetIntervalStatements;
	/** List containing statements that trigger when a stop trigger arrived. */
	private List<EPStatement> stopTriggerStatements;
	/** List containing statements that trigger when data is available. */
	private List<EPStatement> whenDataAvailableStatements;
	/**
	 * List containing statements that trigger when a destroy event is received.
	 */
	private List<EPStatement> destroyStatements;
	/**
	 * List containing all statements that are not in collectionStatements but
	 * in ALL of the lists above.
	 */
	private List<EPStatement> allNonCollectionStatements;
	/** RifidiReport manager for this spec. */
	private ECReportmanager ecReportmanager = null;
	/** Thread that runs the reportmanager. */
	private Thread reportThread = null;
	/** Boundary spec for this spec. */
	private RifidiBoundarySpec rifidiBoundarySpec;
	/** True if the spec is active. */
	private boolean started;
	/** True if the spec was destroied. */
	private boolean destroied;
	/** Threadsafe list for subscription uri. */
	private List<URI> subscriptionURIs;

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
	public RifidiECSpec(String name, ECSpec spec, EPServiceProvider esper,
			RifidiBoundarySpec rifidiBoundarySpec, Set<LogicalReader> readers,
			Set<String> primarykeys, List<RifidiReport> reports) {
		this.rifidiBoundarySpec = rifidiBoundarySpec;
		this.spec = spec;
		this.name = name;
		this.collectionStatements = new ArrayList<EPStatement>();
		this.esper = esper;
		this.readers = readers;
		this.primarykeys = primarykeys;
		this.subscriptionURIs = new CopyOnWriteArrayList<URI>();
		started = false;
		whenDataAvailableStatements = new ArrayList<EPStatement>();
		durationStatements = new ArrayList<EPStatement>();
		stopTriggerStatements = new ArrayList<EPStatement>();
		stableSetIntervalStatements = new ArrayList<EPStatement>();
		allNonCollectionStatements = new ArrayList<EPStatement>();
		destroyStatements = new ArrayList<EPStatement>();
		readers = new HashSet<LogicalReader>();
		if (!spec.isIncludeSpecInReports()) {
			ecReportmanager = new ECReportmanager(name, esper, reports, this,
					subscriptionURIs);
		} else {
			ecReportmanager = new ECReportmanager(name, esper, spec, reports,
					this, subscriptionURIs);
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
		synchronized (collectionStatements) {
			if (!started && !destroied) {
				if (collectionStatements.size() == 0) {
					logger.debug("Constructing statements");
					HashSet<String> readerNames = new HashSet<String>();
					for (LogicalReader reader : readers) {
						getReaderNames(reader, readerNames);
					}

					// SETUP
					// create the window window
					collectionStatements
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
						// TODO: test if this is actually working
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
					// assemble the set of allowed source readers from the list
					// of
					// readers provided by the logical reader
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
					collectionStatements
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
					// DURATION TIMING
					// regular timing using intervals, don't use if data
					// available
					// is set
					if (!rifidiBoundarySpec.isWhenDataAvailable()) {
						durationStatements
								.add(esper
										.getEPAdministrator()
										.createEPL(
												"on pattern[every org.rifidi.edge.epcglobal.aleread.rifidievents.StartEvent -> (timer:interval("
														+ rifidiBoundarySpec
																.getDuration()
														+ " msec) and not org.rifidi.edge.epcglobal.aleread.rifidievents.StopEvent)] select tags from "
														+ name
														+ "_collectwin as tags"));
					} else {
						// WHEN DATA AVAILABLE
						// timing for when data available, should replace
						// regular
						// timing if
						// provided!!!
						whenDataAvailableStatements
								.add(esper
										.getEPAdministrator()
										.createEPL(
												"on pattern[every (org.rifidi.edge.epcglobal.aleread.rifidievents.StartEvent -> "
														+ name
														+ "_collectwin where timer:within("
														+ rifidiBoundarySpec
																.getDuration()
														+ " msec))] select count(whine) from "
														+ name
														+ "_collectwin as whine"));

						whenDataAvailableStatements
								.add(esper
										.getEPAdministrator()
										.createEPL(
												"on pattern[every (org.rifidi.edge.epcglobal.aleread.rifidievents.StartEvent -> "
														+ name
														+ "_collectwin where timer:within("
														+ rifidiBoundarySpec
																.getDuration()
														+ " msec))] select tags from "
														+ name
														+ "_collectwin as tags"));

					}

					// triggers if a startevent arrives without followup
					// processed
					// events
					// needs to be used with both the regular and the data
					// available
					// queries
					durationStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on pattern[every org.rifidi.edge.epcglobal.aleread.rifidievents.StartEvent -> (timer:interval("
													+ rifidiBoundarySpec
															.getDuration()
													+ " msec) and not "
													+ name
													+ "_collectwin and not org.rifidi.edge.epcglobal.aleread.rifidievents.StopEvent)] select count(whine) from "
													+ name
													+ "_collectwin as whine"));
					// STOP TRIGGERS
					if (rifidiBoundarySpec.getStopTriggers().size() > 0) {
						// timing using a stop trigger
						stopTriggerStatements
								.add(esper
										.getEPAdministrator()
										.createEPL(
												"on pattern[every (org.rifidi.edge.epcglobal.aleread.rifidievents.StartEvent -> [0..] "
														+ name
														+ "_collectwin until (timer:interval("
														+ rifidiBoundarySpec
																.getDuration()
														+ " msec) and org.rifidi.edge.epcglobal.aleread.rifidievents.StopEvent))] select count(whine) from "
														+ name
														+ "_collectwin as whine"));

						stopTriggerStatements
								.add(esper
										.getEPAdministrator()
										.createEPL(
												"on pattern[every (org.rifidi.edge.epcglobal.aleread.rifidievents.StartEvent -> [0..] "
														+ name
														+ "_collectwin until (timer:interval("
														+ rifidiBoundarySpec
																.getDuration()
														+ " msec) and org.rifidi.edge.epcglobal.aleread.rifidievents.StopEvent))] select tags from "
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
												"on pattern[every (org.rifidi.edge.epcglobal.aleread.rifidievents.StartEvent -> "
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
												"on pattern[every (org.rifidi.edge.epcglobal.aleread.rifidievents.StartEvent -> (timer:interval("
														+ rifidiBoundarySpec
																.getStableSetInterval()
														+ " msec) and not "
														+ name
														+ "_collectwin))] select count(whine) from "
														+ name
														+ "_collectwin as whine"));
					}

					// clean up before collecting
					collectionStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on pattern[every org.rifidi.edge.epcglobal.aleread.rifidievents.StartEvent] delete from "
													+ name + "_collectwin"));
					destroyStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on org.rifidi.edge.epcglobal.aleread.rifidievents.DestroyEvent select count(whine) from "
													+ name
													+ "_collectwin as whine"));
					destroyStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on org.rifidi.edge.epcglobal.aleread.rifidievents.DestroyEvent select tags  from "
													+ name
													+ "_collectwin as tags"));

					allNonCollectionStatements.addAll(durationStatements);
					allNonCollectionStatements
							.addAll(stableSetIntervalStatements);
					allNonCollectionStatements.addAll(stopTriggerStatements);
					allNonCollectionStatements
							.addAll(whenDataAvailableStatements);
					allNonCollectionStatements.addAll(destroyStatements);

					// debug output
					StringBuilder builder = new StringBuilder();
					builder.append("Statements:\n");
					for (EPStatement statement : collectionStatements) {
						builder.append(statement.getText() + "\n");
					}
					builder.append("Query Statements:\n");
					for (EPStatement statement : allNonCollectionStatements) {
						builder.append(statement.getText() + "\n");
					}
					logger
							.debug("The following collectionStatements were created: \n"
									+ builder.toString());

					// Register collectionStatements to the manager.
					ecReportmanager.setDurationStatements(durationStatements);
					ecReportmanager
							.setStableSetIntervalStatements(stableSetIntervalStatements);
					ecReportmanager
							.setStopTriggerStatements(stopTriggerStatements);
					ecReportmanager
							.setWhenDataAvailableStatements(whenDataAvailableStatements);
					ecReportmanager.setDestroyStatements(destroyStatements);
					logger.debug("Done constructing statements");
				} else {

					logger.debug("Starting statements");
					// start statements for collecting data
					for (EPStatement statement : collectionStatements) {
						statement.start();
					}
					// start statements for filtering and reporting
					for (EPStatement statement : allNonCollectionStatements) {
						statement.start();
					}
					logger.debug("Done starting statements");
				}
				// start the manager
				reportThread = new Thread(ecReportmanager);
				reportThread.start();
				esper.getEPRuntime().sendEvent(new StartEvent("buhu"));
				logger.debug("Spec " + getName() + " started.");
				started = true;
			}
		}
	}

	/**
	 * Stop the spec.
	 */
	public void stop() {
		synchronized (collectionStatements) {
			if (started && !destroied) {
				started = false;
				logger.debug("Stopping spec " + getName());
				reportThread.interrupt();
				try {
					reportThread.join();
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
				}
				// stop all queries
				for (EPStatement statement : allNonCollectionStatements) {
					statement.stop();
				}
				// reverse the collection to prevent inconsistencies while
				// deleting
				Collections.reverse(new ArrayList<EPStatement>(
						collectionStatements));
				// destroy collectionStatements
				for (EPStatement statement : collectionStatements) {
					statement.stop();
				}
				logger.debug("Stopping spec " + getName() + " stopped.");
			}
		}
	}

	/**
	 * Destroy the spec and clean up.
	 */
	public void destroy() {
		esper.getEPRuntime().sendEvent(new DestroyEvent("wuhu"));
	}

	/**
	 * Shoot it, kill it, destroy it. And because java doesn't have friends I
	 * need this method to be declared public. Why!Why!Why!Why!Why! The one
	 * thing that C++ got right :(
	 */
	public void discard() {
		synchronized (collectionStatements) {
			if (!destroied) {
				// make sure we are stopped
				stop();
				// destroy all collectionStatements
				for (EPStatement statement : allNonCollectionStatements) {
					statement.destroy();
				}
				// reverse the collection to prevent inconsistencies while
				// deleting
				Collections.reverse(collectionStatements);
				// destroy collectionStatements
				for (EPStatement statement : collectionStatements) {
					statement.destroy();
				}
				// release all readers that have already been aquired
				for (String reader : spec.getLogicalReaders()
						.getLogicalReader()) {
					try {
						lrService.getLogicalReaderByName(reader).release(this);
					} catch (NoSuchNameExceptionResponse ex) {
						logger.debug("Reader " + reader + " doesn't exist");
					}
				}
				readers.clear();
				destroied = true;
			}
		}
	}

	/**
	 * Subscribe the given URI to this spec.
	 * 
	 * @param uri
	 * @throws DuplicateSubscriptionExceptionResponse
	 */
	public void subscribe(URI uri)
			throws DuplicateSubscriptionExceptionResponse {
		synchronized (subscriptionURIs) {
			if (subscriptionURIs.contains(uri)) {
				throw new DuplicateSubscriptionExceptionResponse("Uri " + uri
						+ " is already subscribed.");
			}
			subscriptionURIs.add(uri);
			if (subscriptionURIs.size() == 1) {
				start();
			}
		}
	}

	/**
	 * Unsubscribe the given URI from this spec.
	 * 
	 * @param uri
	 * @throws NoSuchSubscriberExceptionResponse
	 */
	public void unsubscribe(URI uri) throws NoSuchSubscriberExceptionResponse {
		synchronized (subscriptionURIs) {
			if (!subscriptionURIs.contains(uri)) {
				throw new NoSuchSubscriberExceptionResponse("Uri " + uri
						+ " is not subscribed to this spec.");
			}
			subscriptionURIs.remove(uri);
			if (subscriptionURIs.size() == 0) {
				stop();
			}
		}
	}

	/**
	 * @return the rifidiBoundarySpec
	 */
	public RifidiBoundarySpec getRifidiBoundarySpec() {
		return rifidiBoundarySpec;
	}
}
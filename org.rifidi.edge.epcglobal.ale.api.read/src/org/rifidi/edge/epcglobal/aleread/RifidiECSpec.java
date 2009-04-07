package org.rifidi.edge.epcglobal.aleread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECBoundarySpec;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;
import org.rifidi.edge.lr.LogicalReader;
import org.rifidi.edge.lr.LogicalReaderManagementService;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.UpdateListener;

/**
 * Class for managing the lifetime of an ECSpec. TODO: check if the esper engine
 * is still valid, mind your OSGi ;)
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
class RifidiECSpec {
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(RifidiECSpec.class);
	/** Start and stop triggers. */
	private Set<Trigger> startTriggers;
	private Set<Trigger> stopTriggers;
	/**
	 * Start the next spec of this type after this amount of time has passed
	 * since the start of the last one.
	 */
	private long repeatInterval;
	/** Stop processing of the spec after this time has expired. */
	private long duration;
	/** If no tags arrive within this interval stop processing. */
	private long stableSetInterval;
	/** Kill the spec as soon as data becomes available. */
	private boolean whenDataAvailable;
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
	/** Statements that will return values. */
	private ArrayList<EPStatement> queryStatements;
	/** Set containing all the scheduled triggers futures. */
	private Set<ScheduledFuture<?>> futures;
	/** TODO: this needs fixing as the service might go away and reappear!!! */
	private LogicalReaderManagementService lrService;
	/** Readers used by this spec. */
	private Set<LogicalReader> readers;

	/**
	 * Constructor.
	 * 
	 * @param spec
	 * @param managerServiceImpl
	 * @param esper
	 * @throws InvalidURIExceptionResponse
	 */
	public RifidiECSpec(String name, ECSpec spec,
			LogicalReaderManagementService lrService, EPServiceProvider esper,
			ScheduledExecutorService triggerpool)
			throws InvalidURIExceptionResponse,
			ECSpecValidationExceptionResponse {
		this.triggerpool = triggerpool;
		this.spec = spec;
		this.name = name;
		this.statements = new ArrayList<EPStatement>();
		this.queryStatements = new ArrayList<EPStatement>();
		this.futures = new HashSet<ScheduledFuture<?>>();
		this.esper = esper;
		readers = new HashSet<LogicalReader>();
		// check if we got valid logical readers line 2135 of spec
		String currentReader = "";
		try {
			for (String reader : spec.getLogicalReaders().getLogicalReader()) {
				currentReader = reader;
				lrService.getLogicalReaderByName(reader).aquire(this);
				readers.add(lrService.getLogicalReaderByName(reader));
			}
		} catch (NoSuchNameExceptionResponse e) {
			// release all readers that have already been aquired
			for (String reader : spec.getLogicalReaders().getLogicalReader()) {
				try {
					lrService.getLogicalReaderByName(reader).release(this);
				} catch (NoSuchNameExceptionResponse ex) {
					logger.debug("Reader " + reader + " doesn't exist");
				}
			}
			throw new ECSpecValidationExceptionResponse("Logical reader "
					+ currentReader + " doesn't exist.");
		}

		// check if we got a boundary spec line 2137
		ECBoundarySpec boundaryspec = spec.getBoundarySpec();
		if (boundaryspec == null) {
			throw new ECSpecValidationExceptionResponse(
					"No boundar spec specified.");
		}
		startTriggers = new HashSet<Trigger>();
		// collect start triggers
		if (boundaryspec.getStartTrigger() != null) {
			Trigger trig = createTrigger(boundaryspec.getStartTrigger());
			trig.setStart(true);
			startTriggers.add(trig);
		}
		if (boundaryspec.getExtension() != null
				&& boundaryspec.getExtension().getStartTriggerList() != null) {
			for (String uri : boundaryspec.getExtension().getStartTriggerList()
					.getStartTrigger()) {
				Trigger trig = createTrigger(uri);
				trig.setStart(true);
				startTriggers.add(trig);
			}
		}
		// collect stop triggers
		stopTriggers = new HashSet<Trigger>();
		if (boundaryspec.getStopTrigger() != null) {
			Trigger trig = createTrigger(boundaryspec.getStartTrigger());
			trig.setStart(false);
			stopTriggers.add(trig);
		}
		if (boundaryspec.getExtension() != null
				&& boundaryspec.getExtension().getStopTriggerList() != null) {
			for (String uri : boundaryspec.getExtension().getStopTriggerList()
					.getStopTrigger()) {
				Trigger trig = createTrigger(uri);
				trig.setStart(false);
				stopTriggers.add(trig);
			}
		}
		// get the interval between the starts of two ecspecs
		if (spec.getBoundarySpec().getRepeatPeriod() != null) {
			if (!"MS"
					.equals(spec.getBoundarySpec().getRepeatPeriod().getUnit())) {
				throw new ECSpecValidationExceptionResponse(
						"Only MS is accepted as time unit. Got "
								+ spec.getBoundarySpec().getRepeatPeriod()
										.getUnit());
			}
			repeatInterval = spec.getBoundarySpec().getRepeatPeriod()
					.getValue();
		}
		// time after a spec gets killed
		if (spec.getBoundarySpec().getDuration() != null) {
			if (!"MS".equals(spec.getBoundarySpec().getDuration().getUnit())) {
				throw new ECSpecValidationExceptionResponse(
						"Only MS is accepted as time unit. Got "
								+ spec.getBoundarySpec().getDuration()
										.getUnit());
			}
			duration = spec.getBoundarySpec().getDuration().getValue();
		}
		// time after a spec gets killed if no tags have arrived
		if (spec.getBoundarySpec().getStableSetInterval() != null) {
			if (!"MS".equals(spec.getBoundarySpec().getStableSetInterval()
					.getUnit())) {
				throw new ECSpecValidationExceptionResponse(
						"Only MS is accepted as time unit. Got "
								+ spec.getBoundarySpec().getStableSetInterval()
										.getUnit());
			}
			stableSetInterval = spec.getBoundarySpec().getStableSetInterval()
					.getValue();
		}

		// check if the time values are valid line 2197
		if (duration < 0) {
			throw new ECSpecValidationExceptionResponse(
					"Duration is smaller than 0.");
		}
		if (stableSetInterval < 0) {
			throw new ECSpecValidationExceptionResponse(
					"Stable set interval is smaller than 0.");
		}
		if (repeatInterval < 0) {
			throw new ECSpecValidationExceptionResponse(
					"Repeat interval is smaller than 0.");
		}
		// when data available indicates if the spec should stop as soon as the
		// first dataset has arrived
		if (spec.getBoundarySpec().getExtension() != null) {
			whenDataAvailable = spec.getBoundarySpec().getExtension()
					.isWhenDataAvailable();
		} else {
			whenDataAvailable = false;
		}
		// check if we can stop line 2203
		if (stopTriggers.size() == 0 && duration == 0 && stableSetInterval == 0
				&& !whenDataAvailable) {

		}
		// collect the primary keys
		// TODO: we need to check if the keys actually exist!!!!!
		if (spec.getExtension() != null
				&& spec.getExtension().getPrimaryKeyFields() != null) {
			primarykeys = new HashSet<String>(spec.getExtension()
					.getPrimaryKeyFields().getPrimaryKeyField());
		} else {
			primarykeys = new HashSet<String>();
		}
		// if no primary key is given the epc is the key
		if (primarykeys.size() == 0) {
			primarykeys.add("EPC");
		}
		// TODO: finish the report spec.
		// for(ECReportSpec repspec:spec.getReportSpecs().getReportSpec()){
		// repspec.getExtension().
		// }
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
	 * Creat a trigger from a given URI.
	 * 
	 * @param uri
	 * @return
	 * @throws InvalidURIExceptionResponse
	 */
	private Trigger createTrigger(String uri)
			throws InvalidURIExceptionResponse {
		if (uri.startsWith("urn:epcglobal:ale:trigger:rtc:")) {
			String[] values = uri.substring(31).split(".");
			try {
				// <period>.<offset>.<timezone>
				// the first value is the period and has to be supplied
				Long period = Long.parseLong(values[0]);
				Long offset = 0l;
				Long timezone = 0l;
				// get the offset if it exists
				if (values.length > 1) {
					offset = Long.parseLong(values[1]);
				}
				// get the timezone if it exists
				if (values.length == 3) {
					if (!(values[2].charAt(0) == 'Z')) {
						String[] timeVals = values[2].substring(1).split(":");
						timezone = (long) (Integer.parseInt(timeVals[0]) * 60 + Integer
								.parseInt(timeVals[1])) * 60 * 1000;
						if (values[2].charAt(0) == '-') {
							timezone *= -1;
						}
					}
				}
				return new Trigger(period, offset, timezone, this, esper
						.getEPRuntime());
			} catch (NumberFormatException e) {
				throw new InvalidURIExceptionResponse(uri
						+ " is not a valid trigger URI.");
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new InvalidURIExceptionResponse(uri
						+ " is not a valid trigger URI.");
			}
		}
		throw new InvalidURIExceptionResponse(uri
				+ " is not a valid trigger URI.");
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
												+ (duration > repeatInterval ? duration
														: repeatInterval)
												+ "sec) org.rifidi.edge.core.messages.TagReadEvent"));
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
				//remove trailing commas
				readersBuilder.deleteCharAt(readersBuilder.length()-1);
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
				if (!whenDataAvailable) {
					queryStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on pattern[every org.rifidi.edge.epcglobal.aleread.StartEvent -> (timer:interval("
													+ duration
													+ " msec) and not org.rifidi.edge.epcglobal.aleread.StopEvent)] select * from "
													+ name + "_collectwin"));
				} else {
					// WHEN DATA AVAILABLE
					// timing for when data available, should replace regular
					// timing if
					// provided!!!
					queryStatements.add(esper.getEPAdministrator().createEPL(
							"on pattern[every (org.rifidi.edge.epcglobal.aleread.StartEvent -> "
									+ name + "_collectwin where timer:within("
									+ duration
									+ " msec))] select count(whine) from "
									+ name + "_collectwin as whine"));

					queryStatements.add(esper.getEPAdministrator().createEPL(
							"on pattern[every (org.rifidi.edge.epcglobal.aleread.StartEvent -> "
									+ name + "_collectwin where timer:within("
									+ duration + " msec))] select * from "
									+ name + "_collectwin as whine"));

				}

				// triggers if a startevent arrives without followup processed
				// events
				// needs to be used with both the regular and the data avilable
				// queries
				queryStatements
						.add(esper
								.getEPAdministrator()
								.createEPL(
										"on pattern[every org.rifidi.edge.epcglobal.aleread.StartEvent -> (timer:interval("
												+ duration
												+ " msec) and not "
												+ name
												+ "_collectwin and not org.rifidi.edge.epcglobal.aleread.StopEvent)] select count(whine) from "
												+ name + "_collectwin as whine"));
				// STOP TRIGGERS
				if (stopTriggers.size() > 0) {
					// timing using a stop trigger
					queryStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on pattern[every (org.rifidi.edge.epcglobal.aleread.StartEvent -> [0..] "
													+ name
													+ "_collectwin until (timer:interval("
													+ duration
													+ " msec) and org.rifidi.edge.epcglobal.aleread.StopEvent))] select count(whine) from "
													+ name
													+ "_collectwin as whine"));

					queryStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on pattern[every (org.rifidi.edge.epcglobal.aleread.StartEvent -> [0..] "
													+ name
													+ "_collectwin until (timer:interval("
													+ duration
													+ " msec) and org.rifidi.edge.epcglobal.aleread.StopEvent))] select * from "
													+ name + "_collectwin"));

				}

				if (stableSetInterval > 0) {
					// TODO: incorporate INTERVAL!!!!
					// timing using stable set
					queryStatements.add(esper.getEPAdministrator().createEPL(
							"on pattern[every (org.rifidi.edge.epcglobal.aleread.StartEvent -> "
									+ name + "_collectwin -> (timer:interval("
									+ stableSetInterval + " msec) and not "
									+ name + "_collectwin))] select * from "
									+ name + "_collectwin"));

					queryStatements
							.add(esper
									.getEPAdministrator()
									.createEPL(
											"on pattern[every (org.rifidi.edge.epcglobal.aleread.StartEvent -> (timer:interval("
													+ stableSetInterval
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
				StringBuilder builder = new StringBuilder();
				builder.append("Statements:\n");
				for (EPStatement statement : statements) {
					builder.append(statement.getText() + "\n");
				}
				builder.append("Query Statements:\n");
				for (EPStatement statement : queryStatements) {
					builder.append(statement.getText() + "\n");
				}
				logger.debug("The following statements were created: \n"
						+ builder.toString());
				logger.debug("Spec " + getName() + " started.");
			}
		}

	}

	/**
	 * Stop the spec.
	 */
	public void stop() {
		synchronized (statements) {
			logger.debug("Spec " + getName() + " stopped.");
			// kill all queries
			for (EPStatement statement : queryStatements) {
				statement.destroy();
			}
			// reverse the collection to prevent inconsistencies while deleting
			Collections.reverse(statements);
			// destroy statements
			for (EPStatement statement : statements) {
				statement.destroy();
			}
			// kill all triggers
			for (ScheduledFuture<?> future : futures) {
				try {
					future.cancel(true);
				} catch (Exception e) {
					logger.fatal("Unable to kill trigger: " + e);
				}
			}
		}
	}

	/**
	 * Destroy the spec and clean up.
	 */
	public void destroy() {
		synchronized (statements) {
			// make sure we are stopped
			stop();
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

	/**
	 * Register a listener for reports
	 * 
	 * @param listener
	 */
	public void registerUpdateListener(UpdateListener listener) {
		synchronized (this) {
			for (EPStatement statement : queryStatements) {
				logger.debug("Registering " + listener);
				statement.addListener(listener);
			}
		}
	}

	/**
	 * Unregister a listener for reports
	 * 
	 * @param listener
	 */
	public void unregisterUpdateListener(UpdateListener listener) {
		synchronized (this) {
			for (EPStatement statement : queryStatements) {
				logger.debug("Unregistering " + listener);
				statement.removeListener(listener);
			}
		}
	}

	/**
	 * Prepare the spec for execution.
	 */
	public void init() {
		// schedule the start triggers
		if (startTriggers.size() != 0) {
			for (Trigger trigger : startTriggers) {
				triggerpool.scheduleAtFixedRate(trigger, trigger
						.getDelayToNextExec(), trigger.getPeriod(),
						TimeUnit.MILLISECONDS);
			}
			logger.debug("Scheduled " + startTriggers.size()
					+ " start triggers. ");
		} else {
			// if there are no start triggers the spec gets instantly
			// started
			start();
		}
		// schedule the stop triggers
		if (stopTriggers.size() != 0) {
			for (Trigger trigger : stopTriggers) {
				triggerpool.scheduleAtFixedRate(trigger, trigger
						.getDelayToNextExec(), trigger.getPeriod(),
						TimeUnit.MILLISECONDS);
			}
			logger.debug("Scheduled " + stopTriggers.size()
					+ " start triggers. ");
		}
	}
}
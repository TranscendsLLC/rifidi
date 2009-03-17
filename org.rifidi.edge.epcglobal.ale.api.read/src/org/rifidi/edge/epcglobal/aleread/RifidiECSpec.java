package org.rifidi.edge.epcglobal.aleread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.epcglobal.ale.api.read.data.ECBoundarySpec;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * Class for managing the lifetime of an ECSpec. TODO: check if the esper engine
 * is still valid, mind your OSGi ;)
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
class RifidiECSpec implements UpdateListener {
	/** Start and stop triggers. */
	private Set<Trigger> startTriggers;
	private Set<Trigger> stopTriggers;
	/**
	 * Start the next spec of this type after this amount of time has passed
	 * since the start of the last one.
	 */
	private long repeatInterval;
	/** Stop procesing of the spec after this time has expired. */
	private long duration;
	/** If no tags arrive within this interval stop processing. */
	private long stableSetInterval;
	/** Kill the spec as soon as data becomes available. */
	private boolean whenDataAvailable;
	/** Fields in here constitute the primary keys for the tags. */
	private Set<String> primarykeys;
	/** The spec this object was created from. */
	private ECSpec spec;
	/** Threadpool for executing the scheduled triggers. */
	private ScheduledExecutorService triggerpool;
	/** Name of the spec. */
	private String name;
	/** Esper statement for the ecspec. */
	private EPStatement statement;
	/** Esper instance to use. */
	private EPServiceProvider esper;

	/**
	 * Constructor.
	 * 
	 * @param spec
	 * @param managerServiceImpl
	 * @param esper
	 * @throws InvalidURIExceptionResponse
	 */
	public RifidiECSpec(String name, ECSpec spec, EPServiceProvider esper)
			throws InvalidURIExceptionResponse,
			ECSpecValidationExceptionResponse {
		this.spec = spec;
		this.name = name;
		// create the boundary conditions
		ECBoundarySpec boundaryspec = spec.getBoundarySpec();
		startTriggers = new HashSet<Trigger>();
		// collect start triggers
		if (boundaryspec.getStartTrigger() != null) {
			Trigger trig = createTrigger(boundaryspec.getStartTrigger());
			trig.setStart(true);
			startTriggers.add(trig);
		}
		for (String uri : boundaryspec.getExtension().getStartTriggerList()
				.getStartTrigger()) {
			Trigger trig = createTrigger(uri);
			trig.setStart(true);
			startTriggers.add(trig);
		}
		// collect stop triggers
		stopTriggers = new HashSet<Trigger>();
		if (boundaryspec.getStopTrigger() != null) {
			Trigger trig = createTrigger(boundaryspec.getStartTrigger());
			trig.setStart(false);
			stopTriggers.add(trig);
		}
		for (String uri : boundaryspec.getExtension().getStopTriggerList()
				.getStopTrigger()) {
			Trigger trig = createTrigger(uri);
			trig.setStart(false);
			stopTriggers.add(trig);
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
			duration = spec.getBoundarySpec().getStableSetInterval().getValue();
		}
		whenDataAvailable = spec.getBoundarySpec().getExtension()
				.isWhenDataAvailable();

		primarykeys = new HashSet<String>(spec.getExtension()
				.getPrimaryKeyFields().getPrimaryKeyField());

		// TODO: we might have to manage the size of the pool size but that
		// requires profiling
		triggerpool = new ScheduledThreadPoolExecutor(10);

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
				return new Trigger(period, offset, timezone, this);
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
	 * Start the spec.
	 */
	public void start() {
		ECSPECManagerServiceImpl.logger
				.debug("Spec " + getName() + " started.");
		if (statement == null) {
			String expression = "select pureIdentity from org.rifidi.edge.esper.ProcessedEvent where pureIdentity like '%sgtin%'";
			EPStatement statement = esper.getEPAdministrator().createEPL(
					expression);
			statement.addListener(this);
		}
	}

	/**
	 * Stop the spec.
	 */
	public void stop() {
		ECSPECManagerServiceImpl.logger
				.debug("Spec " + getName() + " stopped.");
		if (statement == null) {
			statement.removeAllListeners();
			statement.destroy();
			statement = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.espertech.esper.client.UpdateListener#update(com.espertech.esper
	 * .client.EventBean[], com.espertech.esper.client.EventBean[])
	 */
	@Override
	public void update(EventBean[] arg0, EventBean[] arg1) {
		System.out.println("update received");
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
			ECSPECManagerServiceImpl.logger.debug("Scheduled "
					+ startTriggers.size() + " start triggers. ");
		} else {
			// if there are now start triggers the spec gets instantly
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
			ECSPECManagerServiceImpl.logger.debug("Scheduled "
					+ stopTriggers.size() + " start triggers. ");
		}
	}
}
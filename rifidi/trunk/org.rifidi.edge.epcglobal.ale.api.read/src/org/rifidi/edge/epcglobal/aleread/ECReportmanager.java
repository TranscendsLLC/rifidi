/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread;

import java.net.URI;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.rifidi.edge.core.messages.DatacontainerEvent;
import org.rifidi.edge.core.messages.TagReadEvent;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReport;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportSpec;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReports;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReports.Reports;
import org.rifidi.edge.epcglobal.aleread.ALEReadAPI.TriggerCondition;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiReport;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;
import com.espertech.esper.event.map.MapEventBean;

/**
 * 
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ECReportmanager implements Runnable, StatementAwareUpdateListener {

	/** Reports that need to be processed. */
	private List<RifidiReport> rifidiReports;

	// TODO: synchronization?
	/** List containing statements that trigger when interval ran out. */
	private List<EPStatement> intervalStatements;
	/** List containing statements that trigger when the tag set is stable. */
	private List<EPStatement> stableSetIntervalStatements;
	/** List containing statements that trigger when a stop trigger arrived. */
	private List<EPStatement> stopTriggerStatements;
	/** List containing statements that trigger when data is available. */
	private List<EPStatement> whenDataAvailableStatements;
	/** Incoming events. */
	private LinkedBlockingQueue<EventTuple> eventQueue;
	/** Target uris for the rifidiReports. */
	private ConcurrentSkipListSet<URI> uris;
	/** Esper instance used by this instance. */
	private EPServiceProvider esper;
	/** Name of the ec spec. */
	private String specName;
	/**
	 * The spec associated with this reportmanager. Null if the spec is not sent
	 * back with the rifidiReports.
	 */
	private ECSpec spec;

	/**
	 * Constructor.
	 */
	public ECReportmanager(String specName, EPServiceProvider esper,
			List<RifidiReport> reports) {
		rifidiReports = reports;
		eventQueue = new LinkedBlockingQueue<EventTuple>();
		uris = new ConcurrentSkipListSet<URI>();
		this.esper = esper;
		this.specName = specName;
	}

	/**
	 * Constructor.
	 */
	public ECReportmanager(String specName, EPServiceProvider esper,
			ECSpec spec, List<RifidiReport> reports) {
		this(specName, esper, reports);
		this.spec = spec;
	}

	public void addURI(URI uri) {
		uris.add(uri);
	}

	public void removeURI(URI uri) {
		uris.remove(uri);
	}

	/**
	 * Add the incoming list of events to the processing queue.
	 * 
	 * @param eventTuple
	 */
	public void addEvents(EventTuple eventTuple) {
		eventQueue.add(eventTuple);
	}

	/**
	 * @param intervalStatements
	 *            the intervalStatements to set
	 */
	public void setIntervalStatements(List<EPStatement> intervalStatements) {
		this.intervalStatements = intervalStatements;
	}

	/**
	 * @param stableSetIntervalStatements
	 *            the stableSetIntervalStatements to set
	 */
	public void setStableSetIntervalStatements(
			List<EPStatement> stableSetIntervalStatements) {
		this.stableSetIntervalStatements = stableSetIntervalStatements;
	}

	/**
	 * @param stopTriggerStatements
	 *            the stopTriggerStatements to set
	 */
	public void setStopTriggerStatements(List<EPStatement> stopTriggerStatements) {
		this.stopTriggerStatements = stopTriggerStatements;
	}

	/**
	 * @param whenDataAvailableStatements
	 *            the whenDataAvailableStatements to set
	 */
	public void setWhenDataAvailableStatements(
			List<EPStatement> whenDataAvailableStatements) {
		this.whenDataAvailableStatements = whenDataAvailableStatements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.espertech.esper.client.StatementAwareUpdateListener#update(com
	 * .espertech.esper.client.EventBean[],
	 * com.espertech.esper.client.EventBean[],
	 * com.espertech.esper.client.EPStatement,
	 * com.espertech.esper.client.EPServiceProvider)
	 */
	@Override
	public void update(EventBean[] arg0, EventBean[] arg1, EPStatement arg2,
			EPServiceProvider arg3) {
		// TODO: fix, needs timing
		esper.getEPRuntime().sendEvent(new StartEvent("wuhu"));
		EventTuple tuple = new EventTuple();
		if (whenDataAvailableStatements.contains(arg2)) {
			tuple.condition = TriggerCondition.DATA_AVAILABLE;
		}
		if (stableSetIntervalStatements.contains(arg2)) {
			tuple.condition = TriggerCondition.STABLE_SET;
		}
		if (stopTriggerStatements.contains(arg2)) {
			tuple.condition = TriggerCondition.STOP_TRIGGER;
		}
		if (intervalStatements.contains(arg2)) {
			tuple.condition = TriggerCondition.INTERVAL;
		}
		tuple.beans = arg0;
		addEvents(tuple);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// subscribe to all statements
		for (EPStatement statement : intervalStatements) {
			statement.addListener(this);
		}
		for (EPStatement statement : stableSetIntervalStatements) {
			statement.addListener(this);
		}
		for (EPStatement statement : stopTriggerStatements) {
			statement.addListener(this);
		}
		for (EPStatement statement : whenDataAvailableStatements) {
			statement.addListener(this);
		}
		while (!Thread.currentThread().isInterrupted()) {
			try {
				EventTuple tagevents = eventQueue.take();
				Set<DatacontainerEvent> events = new HashSet<DatacontainerEvent>();
				// collect tags from event beans
				for (EventBean tagevent : tagevents.beans) {
					if (((MapEventBean) tagevent).getProperties().containsKey(
							"tags")) {
						events.add(((TagReadEvent) ((MapEventBean) tagevent)
								.get("tags")).getTag());
					} else {
						break;
					}
				}
				// process the set of events
				for (RifidiReport rifidiReport : rifidiReports) {
					rifidiReport.processEvents(events);
				}
				// send the report
				ECReports ecreports = new ECReports();
				ecreports.setALEID("Rifidi Edge Server");
				ecreports.setDate(getCurrentCalendar());
				if (spec != null) {
					ecreports.setECSpec(spec);
				}
				// one of TRIGGER REPEAT_PERIOD REQUESTED UNDEFINE
				ecreports.setInitiationCondition("TRIGGER");
				if (ecreports.getInitiationCondition().equals("TRIGGER")) {
					ecreports.setInitiationTrigger("FUUUUUUUUUUUUUUUUUUUUUCK");
				}
				ecreports.setTerminationCondition("TRIGGER");
				// one of TRIGGER DURATION STABLE_SET DATA_AVAILABLE
				// UNREQUEST
				// UNDEFINE
				if (ecreports.getTerminationCondition().equals("TRIGGER")) {
					ecreports.setTerminationTrigger("DAAAAAAAARRRRRRRRRN");
				}
				ecreports.setSpecName(specName);
				// running time
				ecreports.setTotalMilliseconds(10);
				Reports reportsPoltergeist = new Reports();
				ecreports.setReports(reportsPoltergeist);
				for (RifidiReport rifidiReport : rifidiReports) {
					ECReport rep = rifidiReport.send(tagevents.condition);
					if (rep != null) {
						ecreports.getReports().getReport().add(rep);
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Add a new report to the list of rifidiReports to be generated.
	 * 
	 * @param reportSpec
	 */
	public void addECReport(ECReportSpec reportSpec) {

	}

	private XMLGregorianCalendar getCurrentCalendar() {
		GregorianCalendar gc = (GregorianCalendar) GregorianCalendar
				.getInstance();
		DatatypeFactory dataTypeFactory = null;
		try {
			dataTypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException ex) {
			System.out.println("epic fail: " + ex);
		}
		return dataTypeFactory.newXMLGregorianCalendar(gc);
	}

	private class EventTuple {
		public EventBean[] beans;
		public TriggerCondition condition;
	}
}

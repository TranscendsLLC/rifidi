/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread;

import java.net.URI;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.rifidi.edge.core.messages.DatacontainerEvent;
import org.rifidi.edge.core.messages.TagReadEvent;
import org.rifidi.edge.epcglobal.ale.api.read.EPC;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECFilterListMember;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReport;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroup;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupCount;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupList;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupListMember;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupListMemberExtension;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportMemberField;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportOutputFieldSpec;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportSpec;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReports;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReports.Reports;
import org.rifidi.edge.epcglobal.aleread.filters.ALEField;
import org.rifidi.edge.epcglobal.aleread.filters.FilterFactory;
import org.rifidi.edge.epcglobal.aleread.filters.PatternMatcher;
import org.rifidi.edge.epcglobal.aleread.filters.ReportALEField;
import org.rifidi.edge.epcglobal.aleread.groups.GroupFactory;
import org.rifidi.edge.epcglobal.aleread.groups.GroupMatcher;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;
import com.espertech.esper.event.map.MapEventBean;

/**
 * Non thread
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ECReportmanager implements Runnable, StatementAwareUpdateListener {

	private enum TriggerCondition {
		DATA_AVAILABLE, STABLE_SET, STOP_TRIGGER, INTERVAL
	};

	/** Reports that need to be processed. */
	private Set<Report> reports;

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
	/** Target uris for the reports. */
	private ConcurrentSkipListSet<URI> uris;
	/** Esper instance used by this instance. */
	private EPServiceProvider esper;
	/** Factory for creating filter instances. */
	private FilterFactory filterFactory;
	/** Factory for creating grouping rules */
	private GroupFactory groupFactory;
	/** Name of the ec spec. */
	private String specName;
	/**
	 * The spec associated with this reportmanager. Null if the spec is not sent
	 * back with the reports.
	 */
	private ECSpec spec;

	/**
	 * Constructor.
	 */
	public ECReportmanager(String specName, EPServiceProvider esper) {
		reports = new HashSet<Report>();
		eventQueue = new LinkedBlockingQueue<EventTuple>();
		uris = new ConcurrentSkipListSet<URI>();
		filterFactory = new FilterFactory();
		groupFactory = new GroupFactory();
		this.esper = esper;
		this.specName = specName;
	}

	/**
	 * Constructor.
	 */
	public ECReportmanager(String specName, EPServiceProvider esper, ECSpec spec) {
		this(specName, esper);
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
				for (Report report : reports) {
					report.processEvents(events);
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
				for (Report report : reports) {
					ECReport rep = report.send(tagevents.condition);
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
	 * Add a new report to the list of reports to be generated.
	 * 
	 * @param reportSpec
	 */
	public void addECReport(ECReportSpec reportSpec) {
		int options = 0;
		if ("ADDITION".equals(reportSpec.getReportSet().getSet())) {
			options |= ECReportOptions.REPORT_ADDITIONS;
		} else if ("DELETION".equals(reportSpec.getReportSet().getSet())) {
			options |= ECReportOptions.REPORT_DELETIONS;
		} else if ("CURRENT".equals(reportSpec.getReportSet().getSet())) {
			options |= ECReportOptions.REPORT_CURRENT;
		}
		Map<ALEField, List<PatternMatcher>> includeFilters = new HashMap<ALEField, List<PatternMatcher>>();
		Map<ALEField, List<PatternMatcher>> excludeFilters = new HashMap<ALEField, List<PatternMatcher>>();
		if (reportSpec.getFilterSpec() != null) {
			for (ECFilterListMember filter : reportSpec.getFilterSpec()
					.getExtension().getFilterList().getFilter()) {

				if ("INCLUDE".equals(filter.getIncludeExclude())) {
					includeFilters.putAll(filterFactory.createMatcher(filter));
				} else if ("EXCLUDE".equals(filter.getIncludeExclude())) {
					excludeFilters.putAll(filterFactory.createMatcher(filter));
				}
				filter.getPatList().getPat();
			}
		}
		List<GroupMatcher> groups = new ArrayList<GroupMatcher>();
		ALEField groupfield = null;
		if (reportSpec.getGroupSpec() != null) {

			groupfield = new ALEField(reportSpec.getGroupSpec().getExtension()
					.getFieldspec());
			for (String pattern : reportSpec.getGroupSpec().getPattern()) {
				groups.add(groupFactory.createMatcher(groupfield, pattern));
			}
		}
		if (reportSpec.getOutput().isIncludeCount()) {
			options = options | ECReportOptions.INCLUDE_COUNT;
		}
		if (reportSpec.getOutput().isIncludeEPC()) {
			options = options | ECReportOptions.INCLUDE_EPC;
		}
		if (reportSpec.getOutput().isIncludeRawDecimal()) {
			options = options | ECReportOptions.INCLUDE_RAW_DECIMAL;
		}
		if (reportSpec.getOutput().isIncludeRawDecimal()) {
			options = options | ECReportOptions.INCLUDE_RAW_HEX;
		}
		if (reportSpec.getOutput().isIncludeTag()) {
			options = options | ECReportOptions.INCLUDE_TAG;
		}
		if (reportSpec.isReportIfEmpty()) {
			options = options | ECReportOptions.REPORT_IF_EMPTY;
		}
		if (reportSpec.isReportOnlyOnChange()) {
			options = options | ECReportOptions.REPORT_ONLY_ON_CHANGE;
		}
		Set<ReportALEField> reportFields = new HashSet<ReportALEField>();
		if (reportSpec.getOutput().getExtension() != null
				&& reportSpec.getOutput().getExtension().getFieldList() != null) {
			for (ECReportOutputFieldSpec spec : reportSpec.getOutput()
					.getExtension().getFieldList().getField()) {
				reportFields.add(new ReportALEField(spec.getName(), spec
						.getFieldspec()));
			}
		}

		reports.add(new Report(reportSpec.getReportName(), options,
				includeFilters, excludeFilters, groupfield, groups,
				reportFields));
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

	/**
	 * Temporary private class for handling reports.
	 * 
	 * @author Jochen Mader - jochen@pramari.com
	 * 
	 */
	private class Report {
		/** Name of the report. */
		private String name;
		private Boolean changed = true;
		private Set<DatacontainerEvent> tagreads;
		private Set<DatacontainerEvent> tagreadsToSend;
		private EPCDataContainerAdapter adapter;
		private Map<ALEField, List<PatternMatcher>> includeFilters;
		private Map<ALEField, List<PatternMatcher>> excludeFilter;
		private ALEField groupField;
		private List<GroupMatcher> groups;
		private int options = 0;
		private Set<ReportALEField> reportFields;

		/**
		 * Constructor.
		 * 
		 * @param name
		 * @param options
		 * @param reportSet
		 * @param include
		 * @param exclude
		 * @param groupField
		 * @param groups
		 */
		public Report(String name, int options,
				Map<ALEField, List<PatternMatcher>> include,
				Map<ALEField, List<PatternMatcher>> exclude,
				ALEField groupField, List<GroupMatcher> groups,
				Set<ReportALEField> reportFields) {
			super();
			this.name = name;
			this.options = options;
			this.groupField = groupField;
			this.groups = groups;
			this.reportFields = reportFields;
			includeFilters = include;
			excludeFilter = exclude;
			tagreads = new HashSet<DatacontainerEvent>();
			tagreadsToSend = new HashSet<DatacontainerEvent>();
			adapter = new EPCDataContainerAdapter();
		}

		public void start() {
			tagreads.clear();
			tagreadsToSend.clear();
		}

		public void processEvents(Set<DatacontainerEvent> incoming) {
			if (includeFilters.size() > 0 || excludeFilter.size() > 0) {
				Set<DatacontainerEvent> notincluded = new HashSet<DatacontainerEvent>();
				boolean matched = false;
				for (DatacontainerEvent event : incoming) {
					matched = false;
					for (ALEField field : includeFilters.keySet()) {
						String fieldString = adapter.getField(field, event);
						for (PatternMatcher matcher : includeFilters.get(field)) {
							if (matcher.match(fieldString)) {
								// remove the event from the list of events that
								// will
								// be dropped
								notincluded.remove(event);
								matched = true;
								break;
							}
							if (matched) {
								break;
							}
						}
						if (matched) {
							break;
						}
					}
					if (!matched) {
						notincluded.add(event);
						// we are already removing it, no need to process the
						// exclude
						// filters
						continue;
					}
					for (ALEField field : excludeFilter.keySet()) {
						String fieldString = adapter.getField(field, event);
						for (PatternMatcher matcher : excludeFilter.get(field)) {
							if (matcher.match(fieldString)) {
								// add event to the list of events that will be
								// dropped
								notincluded.add(event);
								matched = true;
								break;
							}
							if (matched) {
								break;
							}
						}
						if (matched) {
							break;
						}
					}
					matched = false;
				}
				// remove all events that were not mathched by an include filter
				incoming.removeAll(notincluded);
			}

			if ((options & ECReportOptions.REPORT_ADDITIONS) > 0) {
				// keep the tags that appear in both sets
				tagreads.retainAll(incoming);
				// remove the common tags from incoming
				incoming.removeAll(tagreads);
				// add all tags to tagreads
				tagreads.addAll(incoming);
				// prepare tags to send out
				// check if the set changes at all
				if ((options & ECReportOptions.REPORT_ONLY_ON_CHANGE) > 0) {
					if (!(tagreadsToSend.containsAll(incoming) && incoming
							.containsAll(tagreadsToSend))) {
						tagreadsToSend.clear();
						tagreadsToSend.addAll(incoming);
						changed = true;
					} else {
						changed = false;
					}
				} else {
					tagreadsToSend.clear();
					tagreadsToSend.addAll(incoming);
				}
			} else if ((options & ECReportOptions.REPORT_DELETIONS) > 0) {
				// remove the tags that appear in both sets
				tagreads.removeAll(incoming);
				// prepare tags that are about to be sent out
				// check if the set changes at all
				if ((options & ECReportOptions.REPORT_ONLY_ON_CHANGE) > 0) {
					if (!(tagreadsToSend.containsAll(incoming) && incoming
							.containsAll(tagreadsToSend))) {
						tagreadsToSend.clear();
						tagreadsToSend.addAll(tagreads);
						changed = true;
					} else {
						changed = false;
					}
				} else {
					tagreadsToSend.clear();
					tagreadsToSend.addAll(tagreads);
				}
				// create the current set of tags
				tagreads.addAll(incoming);
			} else if ((options & ECReportOptions.REPORT_CURRENT) > 0) {
				// check if the set changes at all
				if ((options & ECReportOptions.REPORT_ONLY_ON_CHANGE) > 0) {
					if (!(tagreadsToSend.containsAll(incoming) && incoming
							.containsAll(tagreadsToSend))) {
						tagreads.clear();
						tagreads.addAll(incoming);
						changed = true;
					} else {
						changed = false;
					}
				} else {
					tagreadsToSend.clear();
					tagreadsToSend.addAll(incoming);
				}
			} else {
				System.out.println("wtf?");
			}
		}

		public ECReport send(TriggerCondition condition) {
			ECReport report = new ECReport();
			if (((!((options & ECReportOptions.REPORT_ONLY_ON_CHANGE) > 0)) || ((options & ECReportOptions.REPORT_ONLY_ON_CHANGE) > 0 && changed))
					&& (tagreadsToSend.size() > 0 || (tagreadsToSend.size() == 0 && (options & ECReportOptions.REPORT_IF_EMPTY) > 0 == true))) {
				report.setReportName(name);

				List<DatacontainerEvent> tags = new ArrayList<DatacontainerEvent>();

				if (groupField != null) {
					Set<DatacontainerEvent> matchedEvents = new HashSet<DatacontainerEvent>();
					for (DatacontainerEvent event : tagreadsToSend) {
						String field = adapter.getField(groupField, event);
						for (GroupMatcher matcher : groups) {
							if (matcher.match(field, event)) {
								matchedEvents.add(event);
								break;
							}
						}
					}
					tagreadsToSend.remove(matchedEvents);
					for (GroupMatcher matcher : groups) {
						ECReportGroup group = new ECReportGroup();
						Map<String, List<DatacontainerEvent>> grouped = matcher
								.getGrouped();
						if (grouped.keySet().size() == 1) {
							String name = grouped.keySet().iterator().next();
							group.setGroupName(name);
							fillGroup(group, grouped.get(name));
							tags.addAll(grouped.get(name));
							report.getGroup().add(group);
						}
					}
				}
				tags.addAll(tagreadsToSend);
				// create the default group
				ECReportGroup group = new ECReportGroup();
				fillGroup(group, tags);
				report.getGroup().add(group);

				System.out.println("sending: ");
				for (ECReportGroup gr : report.getGroup()) {
					if(gr.getGroupList() != null){
						for (ECReportGroupListMember mem : gr.getGroupList()
								.getMember()) {
							System.out.println(mem.getTag().getValue());
						}	
					}
					
				}
				return report;
			}
			return null;
		}

		private void fillGroup(ECReportGroup group,
				List<DatacontainerEvent> grouped) {

			if ((options & ECReportOptions.INCLUDE_COUNT) > 0) {
				ECReportGroupCount count = new ECReportGroupCount();
				count.setCount(grouped.size());
			}
			for (DatacontainerEvent event : grouped) {

				ECReportGroupList ecReportGroupList = new ECReportGroupList();
				group.setGroupList(ecReportGroupList);
				group.getGroupList().getMember();
				ECReportGroupListMember member = new ECReportGroupListMember();
				if ((options & ECReportOptions.INCLUDE_RAW_DECIMAL) > 0) {
					EPC epc = new EPC();
					epc.setValue(adapter.getEpc(event, ALEDataTypes.EPC,
							ALEDataFormats.EPC_DECIMAL));
					member.setRawDecimal(epc);
				}
				if ((options & ECReportOptions.INCLUDE_RAW_HEX) > 0) {
					EPC epc = new EPC();
					epc.setValue(adapter.getEpc(event, ALEDataTypes.EPC,
							ALEDataFormats.EPC_HEX));
					member.setRawHex(epc);
				}
				if ((options & ECReportOptions.INCLUDE_TAG) > 0) {
					EPC epc = new EPC();
					epc.setValue(adapter.getEpc(event, ALEDataTypes.EPC,
							ALEDataFormats.EPC_TAG));
					member.setTag(epc);
				}
				if ((options & ECReportOptions.INCLUDE_EPC) > 0) {
					EPC epc = new EPC();
					epc.setValue(adapter.getEpc(event, ALEDataTypes.EPC,
							ALEDataFormats.EPC_PURE));
					member.setEpc(epc);
				}
				if (reportFields.size() > 0) {
					ECReportGroupListMemberExtension ext = new ECReportGroupListMemberExtension();
					for (ReportALEField field : reportFields) {
						ECReportMemberField ecrepfield = new ECReportMemberField();
						ecrepfield.setFieldspec(field.getFieldSpec());
						ecrepfield.setName(field.getFieldName());
						ecrepfield.setValue(adapter.getField(field, event));
					}
					member.setExtension(ext);
				}
				group.getGroupList().getMember().add(member);
			}
		}
	}

	private class EventTuple {
		public EventBean[] beans;
		public TriggerCondition condition;
	}
}

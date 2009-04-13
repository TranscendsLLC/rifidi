/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;

import org.rifidi.edge.core.messages.DatacontainerEvent;
import org.rifidi.edge.core.messages.EPCGeneration1Event;
import org.rifidi.edge.core.messages.TagReadEvent;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECFilterListMember;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportSpec;
import org.rifidi.edge.epcglobal.aleread.filters.ALEField;
import org.rifidi.edge.epcglobal.aleread.filters.FilterFactory;
import org.rifidi.edge.epcglobal.aleread.filters.PatternMatcher;
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
	/** What tags should be returned? */
	public enum ReportSet {
		ADDITION, DELETION, CURRENT
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
	private LinkedBlockingQueue<EventBean[]> eventQueue;
	/** Target uris for the reports. */
	private ConcurrentSkipListSet<URI> uris;
	/** Esper instance used by this instance. */
	private EPServiceProvider esper;
	/** Factory for creating filter instances. */
	private FilterFactory filterFactory;
	/** Factory for creating grouping rules */
	private GroupFactory groupFactory;

	/**
	 * Constructor.
	 */
	public ECReportmanager(EPServiceProvider esper) {
		reports = new HashSet<Report>();
		eventQueue = new LinkedBlockingQueue<EventBean[]>();
		uris = new ConcurrentSkipListSet<URI>();
		filterFactory = new FilterFactory();
		groupFactory = new GroupFactory();
		this.esper = esper;
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
	 * @param events
	 */
	public void addEvents(EventBean[] events) {
		eventQueue.add(events);
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
		boolean empty = false;
		if (whenDataAvailableStatements.contains(arg2)) {
			System.out.println("when data available");
		}
		if (stableSetIntervalStatements.contains(arg2)) {
			System.out.println("stable set");
		}
		if (stopTriggerStatements.contains(arg2)) {
			System.out.println("stop trigger");
		}
		if (intervalStatements.contains(arg2)) {
			System.out.println("interval statement");
		}
		addEvents(arg0);
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
				EventBean[] tagevents = eventQueue.take();
				Set<DatacontainerEvent> events = new HashSet<DatacontainerEvent>();
				// collect tags from event beans
				for (EventBean tagevent : tagevents) {
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
				for (Report report : reports) {
					report.send();
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
		// check the report set
		ReportSet reportSet = null;
		if ("ADDITION".equals(reportSpec.getReportSet().getSet())) {
			reportSet = ReportSet.ADDITION;
		} else if ("DELETION".equals(reportSpec.getReportSet().getSet())) {
			reportSet = ReportSet.DELETION;
		} else if ("CURRENT".equals(reportSpec.getReportSet().getSet())) {
			reportSet = ReportSet.CURRENT;
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
		// reportSpec.getOutput();
		// reportSpec.getGroupSpec();
		// reportSpec.getExtension().getStatProfileNames();
		reports.add(new Report(reportSpec.getReportName(), reportSet,
				reportSpec.isReportIfEmpty(),
				reportSpec.isReportOnlyOnChange(), includeFilters,
				excludeFilters, groupfield, groups));
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
		private ReportSet reportSet;
		private Boolean reportIfEmpty;
		private Boolean reportOnlyOnchange;
		private Boolean changed = true;
		private Set<DatacontainerEvent> tagreads;
		private Set<DatacontainerEvent> tagreadsToSend;
		private EPCDataContainerAdapter adapter;
		private Map<ALEField, List<PatternMatcher>> includeFilters;
		private Map<ALEField, List<PatternMatcher>> excludeFilter;
		private ALEField groupField;
		private List<GroupMatcher> groups;

		/**
		 * @param name
		 * @param reportSet
		 * @param reportIfEmpty
		 * @param reportOnlyOnchange
		 */
		public Report(String name, ReportSet reportSet, Boolean reportIfEmpty,
				Boolean reportOnlyOnchange,
				Map<ALEField, List<PatternMatcher>> include,
				Map<ALEField, List<PatternMatcher>> exclude,
				ALEField groupField, List<GroupMatcher> groups) {
			super();
			this.name = name;
			this.reportSet = reportSet;
			this.reportIfEmpty = reportIfEmpty;
			this.reportOnlyOnchange = reportOnlyOnchange;
			this.groupField = groupField;
			this.groups = groups;
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
			if(includeFilters.size()>0 || excludeFilter.size()>0){
				Set<DatacontainerEvent> notincluded = new HashSet<DatacontainerEvent>();
				boolean matched = false;
				for (DatacontainerEvent event : incoming) {
					matched = false;
					for (ALEField field : includeFilters.keySet()) {
						String fieldString = adapter.getField(field, event);
						for (PatternMatcher matcher : includeFilters.get(field)) {
							if (matcher.match(fieldString)) {
								// remove the event from the list of events that will
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
						// we are already removing it, no need to prcess the exclude
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
			
			if (ReportSet.ADDITION.equals(reportSet)) {
				// keep the tags that appear in both sets
				tagreads.retainAll(incoming);
				// remove the common tags from incoming
				incoming.removeAll(tagreads);
				// add all tags to tagreads
				tagreads.addAll(incoming);
				// prepare tags to send out
				// check if the set changes at all
				if (reportOnlyOnchange) {
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
			} else if (ReportSet.DELETION.equals(reportSet)) {
				// remove the tags that appear in both sets
				tagreads.removeAll(incoming);
				// prepare tags that are about to be sent out
				// check if the set changes at all
				if (reportOnlyOnchange) {
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
			} else if (ReportSet.CURRENT.equals(reportSet)) {
				// check if the set changes at all
				if (reportOnlyOnchange) {
					if (!(tagreadsToSend.containsAll(incoming) && incoming
							.containsAll(tagreadsToSend))) {
						tagreads.clear();
						tagreads.addAll(incoming);
						changed = true;
					} else {
						changed = false;
					}
				} else {
					tagreads.clear();
					tagreads.addAll(incoming);
				}
			} else {
				System.out.println("wtf?");
			}
		}

		public void send() {
			if (((!reportOnlyOnchange) || (reportOnlyOnchange && changed))
					&& (tagreadsToSend.size() > 0 || (tagreadsToSend.size() == 0 && reportIfEmpty == true))) {
				List<DatacontainerEvent> tags=new ArrayList<DatacontainerEvent>();
				if(groupField!=null){
					Set<DatacontainerEvent> matchedEvents=new HashSet<DatacontainerEvent>();
					for(DatacontainerEvent event:tagreadsToSend){
						String field=adapter.getField(groupField, event);
						for(GroupMatcher matcher:groups){
							if(matcher.match(field, event)){
								matchedEvents.add(event);
								break;
							}
						}	
					}
					tagreadsToSend.remove(matchedEvents);
					for(GroupMatcher matcher:groups){
						tags.addAll(matcher.getGrouped());
					}
				}
				tags.addAll(tagreadsToSend);
				System.out.println("sending: ");
				for (DatacontainerEvent tagread : tagreadsToSend) {
					if (tagread instanceof EPCGeneration1Event) {
					}
					System.out.println(adapter.getEpc(tagread,
							ALEDataTypes.EPC, ALEDataFormats.EPC_TAG));
				}
			}
		}
	}
}

package org.rifidi.edge.epcglobal.aleread.wrappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.messages.DatacontainerEvent;
import org.rifidi.edge.epcglobal.ale.api.read.EPC;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReport;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroup;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupCount;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupList;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupListMember;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupListMemberExtension;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportMemberField;
import org.rifidi.edge.epcglobal.aleread.ALEDataFormats;
import org.rifidi.edge.epcglobal.aleread.ALEDataTypes;
import org.rifidi.edge.epcglobal.aleread.ECReportOptions;
import org.rifidi.edge.epcglobal.aleread.EPCDataContainerAdapter;
import org.rifidi.edge.epcglobal.aleread.filters.ALEField;
import org.rifidi.edge.epcglobal.aleread.filters.PatternMatcher;
import org.rifidi.edge.epcglobal.aleread.filters.ReportALEField;
import org.rifidi.edge.epcglobal.aleread.groups.GroupMatcher;

/**
 * Wrapper for ecreports.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class RifidiReport {
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(RifidiReport.class);
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
	 * @param include
	 * @param exclude
	 * @param groupField
	 * @param groups
	 * @param reportFields
	 */
	public RifidiReport(String name, int options,
			Map<ALEField, List<PatternMatcher>> include,
			Map<ALEField, List<PatternMatcher>> exclude, ALEField groupField,
			List<GroupMatcher> groups, Set<ReportALEField> reportFields) {
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
				if(includeFilters.size()>0){
					if (!matched) {
						notincluded.add(event);
						// we are already removing it, no need to process the
						// exclude
						// filters
						continue;
					}	
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

	public ECReport send() {
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
				tagreadsToSend.removeAll(matchedEvents);
				for (GroupMatcher matcher : groups) {
					Map<String, List<DatacontainerEvent>> grouped = matcher
							.getGrouped();
					for(String name:grouped.keySet()){
						ECReportGroup group = new ECReportGroup();
						group.setGroupName(name);
						fillGroup(group, grouped.get(name));
						report.getGroup().add(group);
					}
				}
			}
			tags.addAll(tagreadsToSend);
			// create the default group
			ECReportGroup group = new ECReportGroup();
			fillGroup(group, tags);
			report.getGroup().add(group);
			return report;
		}
		return null;
	}

	private void fillGroup(ECReportGroup group, List<DatacontainerEvent> grouped) {

		if ((options & ECReportOptions.INCLUDE_COUNT) > 0) {
			ECReportGroupCount count = new ECReportGroupCount();
			count.setCount(grouped.size());
		}
		ECReportGroupList ecReportGroupList = new ECReportGroupList();
		group.setGroupList(ecReportGroupList);
		for (DatacontainerEvent event : grouped) {
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
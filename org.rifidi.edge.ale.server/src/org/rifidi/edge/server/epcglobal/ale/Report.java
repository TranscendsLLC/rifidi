package org.rifidi.edge.server.epcglobal.ale;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fosstrak.tdt.TDTEngine;
import org.rifidi.edge.epcglobal.ale.ECFilterSpec;
import org.rifidi.edge.epcglobal.ale.ECReaderStat;
import org.rifidi.edge.epcglobal.ale.ECReaderStat.Sightings;
import org.rifidi.edge.epcglobal.ale.ECReport;
import org.rifidi.edge.epcglobal.ale.ECReportGroup;
import org.rifidi.edge.epcglobal.ale.ECReportGroupCount;
import org.rifidi.edge.epcglobal.ale.ECReportGroupList;
import org.rifidi.edge.epcglobal.ale.ECReportGroupListMember;
import org.rifidi.edge.epcglobal.ale.ECReportGroupListMemberExtension;
import org.rifidi.edge.epcglobal.ale.ECReportGroupListMemberExtension.FieldList;
import org.rifidi.edge.epcglobal.ale.ECReportGroupListMemberExtension.Stats;
import org.rifidi.edge.epcglobal.ale.ECReportMemberField;
import org.rifidi.edge.epcglobal.ale.ECReportOutputFieldSpec;
import org.rifidi.edge.epcglobal.ale.ECReportOutputSpecExtension;
import org.rifidi.edge.epcglobal.ale.ECReportSpec;
import org.rifidi.edge.epcglobal.ale.ECReportSpecExtension;
import org.rifidi.edge.epcglobal.ale.ECSightingStat;
import org.rifidi.edge.epcglobal.ale.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.ECTagStat;
import org.rifidi.edge.epcglobal.ale.ECTagStat.StatBlocks;
import org.rifidi.edge.epcglobal.ale.ImplementationExceptionResponse;
import org.rifidi.edge.server.epcglobal.ale.EventCycle;
import org.rifidi.edge.server.epcglobal.ale.Pattern;
import org.rifidi.edge.server.epcglobal.ale.Tag;
import org.rifidi.edge.server.epcglobal.ale.TagHelper;

import com.google.common.collect.Sets;

public class Report {

	/** logger. */
	//private static final Logger LOG = Logger.getLogger(Report.class);
	
	/** name of this report. */
	private final String name;
	/** current event cycle delivers tags. */
	private final EventCycle currentEventCycle;
	
	
	/** patterns of tags which are included in this report. */
	private final Set<Pattern> includePatterns = new HashSet<Pattern>();
	/** patterns of tags which are excluded from this report. */
	private final Set<Pattern> excludePatterns = new HashSet<Pattern>();
	/** patterns to group the tags of this report. */
	private final Set<Pattern> groupPatterns = new HashSet<Pattern>();
		
	/** type of this report (current, additions or deletions). */
	private String reportType;

	/** ec report. */
	private ECReport report;
	/** ec report specification. */
	private ECReportSpec reportSpec;
	
	/**
	 * Constructor set parameters, read specifiaction and initializes patterns.
	 * 
	 * @param reportSpec defines how the report should be generated
	 * @param currentEventCycle this report belongs to
	 * @throws ImplementationException if an implementation exception occurs
	 */
	public Report(ECReportSpec reportSpec, EventCycle currentEventCycle) throws ImplementationExceptionResponse {
		
		// set name
		name = reportSpec.getReportName();
		
		//LOG.debug("Create report '" + name + "'");
		
		// create ECReport
		report = new ECReport();
		
		// set ECReport name
		report.setReportName(name);
		
		// set type
		reportType = reportSpec.getReportSet().getSet();

		// set ECReportSpec
		this.reportSpec = reportSpec;
		
		// set currentEventCycle
		this.currentEventCycle = currentEventCycle;
				
		// init patterns
		initFilterPatterns();
		initGroupPatterns();

	}

	/**
	 * This method adds a tag to the report.
	 * 
	 * @param tag to add
	 * @throws ECSpecValidationException if the tag is invalid
	 * @throws ImplementationException if an implementation exception occurs
	 */
	public void addTag(Tag tag) throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse {

		// get tag URI
		String tagURI = tag.getTagIDAsPureURI();
	
		// check if the tag is a member of this report (use filter patterns and set spec)
		if (isMember(tagURI)) {
	
			//LOG.debug("Event '" + tag + "' is member of report '" + name + "'");
			
				// add tag to report
				addTagToReportGroup(tag);
		}
	}
	
	
	/**
	 * helper method to display tags that were added or deleted.
	 * @param reportTags a map holding the tags that were either added or deleted.
	 */
	private void writeTraceInformation(Map<String, Tag> reportTags) {
		String out = '\n' + "+++++++++++++++++++++++++++++++++++++++++++++++++++++" + '\n';
		out +=  '\t' + "eventcycle " + currentEventCycle.getName() + '\n';
		out +=  '\t' + "round " + currentEventCycle.getRounds() + '\n';
		if (reportTags == null) {
			out += '\t' + "no tags" + '\n';
			out +=  "+++++++++++++++++++++++++++++++++++++++++++++++++++++" + '\n';
			//LOG.info(out);
			return;
		}
		
		
		for (Tag tag : reportTags.values()) {
			out += '\t' + tag.getTagIDAsPureURI() + '\n';
		}
		out +=  "+++++++++++++++++++++++++++++++++++++++++++++++++++++" + '\n';
		//LOG.trace(out);
	}
	
	/**
	 * This method returns the new ec report.
	 * 
	 * @return ec report
	 * @throws ECSpecValidationException if a tag is invalid
	 * @throws ImplementationException if an implementation exception occurs
	 */
	public ECReport getECReport() throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse {
		Set<Tag> currentCycleTags = Sets.newHashSet(currentEventCycle.getTags());
		Set<Tag> lastCycleTags = Sets.newHashSet(currentEventCycle.getLastEventCycleTags());
		
		//generate new ECReport
		if (ECReportSetEnum.isSameECReportSet(ECReportSetEnum.ADDITIONS, reportType)) {
			
			// get additional tags
			Map<String, Tag> reportTags = new HashMap<String, Tag>();

			// add tags from current EventCycle 
			for (Tag tag : currentCycleTags) {
				reportTags.put(tag.getTagIDAsPureURI(), tag);
			}
				
			// remove tags from last EventCycle
			if (lastCycleTags != null) {
				for (Tag tag : lastCycleTags) {
					reportTags.remove(tag.getTagIDAsPureURI());
				}
			}

			for (Tag tag : reportTags.values()) {
				addTag(tag);
			}
			//writeDebugInformation(reportTags);
	
		} else if (ECReportSetEnum.isSameECReportSet(ECReportSetEnum.CURRENT, reportType)) {

			// get unique tags fixed the duplicate tags per report
			Map<String, Tag> reportTags = new HashMap<String, Tag>();
			// get tags from current EventCycle 
			for (Tag tag : currentCycleTags) {
				reportTags.put(tag.getTagIDAsPureURI(), tag);				
			}
			
			for(Tag tag : reportTags.values()){
				addTag(tag);
			}
		} else if (ECReportSetEnum.isSameECReportSet(ECReportSetEnum.DELETIONS, reportType)) {
			
			// get removed tags
			Map<String, Tag> reportTags = new HashMap<String, Tag>();
				
			// add tags from last EventCycle
			if (lastCycleTags != null) {
				for (Tag tag : lastCycleTags) {
					reportTags.put(tag.getTagIDAsPureURI(), tag);
				}
			}
				
			// remove tags from current EventCycle
			for (Tag tag : currentCycleTags) {
				reportTags.remove(tag.getTagIDAsPureURI());
			}
				
			// add tags to report with filtering
			for (Tag tag : reportTags.values()) {
				addTag(tag);
			}
			//if (LOG.isTraceEnabled()) {
			//	writeTraceInformation(reportTags);
			//}
			//} else {
			//	LOG.info("unknown reportType: " + reportType);
			//}
		
		}
		
		if (reportSpec.isReportIfEmpty() || !isEmpty()) {
			ECReport temp = report;	
			report = new ECReport();
			report.setReportName(name);	
			return temp;
		} else {
			report = new ECReport();
			report.setReportName(name);
			return null;
		}
	}

	//
	// private methods
	//
	
	/**
	 * This method initializes the filter patterns on the basis of the ec report specification.
	 */
	private void initFilterPatterns() {
	
		//LOG.debug("Init filter patterns");
		
		// get filter spec
		ECFilterSpec filterSpec = reportSpec.getFilterSpec();
		if (filterSpec != null) {
			
			// add ECIncludePatterns from spec to includePatterns set
			List<String> ecIncludePatterns = filterSpec.getIncludePatterns().getIncludePattern();
			if (ecIncludePatterns != null) {
				for (String pattern : ecIncludePatterns) {
					try {
						includePatterns.add(new Pattern(pattern, PatternUsage.FILTER));
					} catch (ECSpecValidationExceptionResponse e) {
						//LOG.debug("Specification Validation Exception: ", e);
					}
				}
			}
			
			// add ECExcludePatterns from spec to excludePatterns set
			List<String> ecExcludePatterns = filterSpec.getExcludePatterns().getExcludePattern();
			if (ecExcludePatterns != null) {
				for (String pattern : ecExcludePatterns) {
					try {
						excludePatterns.add(new Pattern(pattern, PatternUsage.FILTER));
					} catch (ECSpecValidationExceptionResponse e) {
						//LOG.debug("Specification Validation Exception: ", e);
					}
				}
			}
		}
		
	}
	
	/**
	 * This method initializes the group patterns on the basis of the ec report specification.
	 */
	private void initGroupPatterns() {
		
		//LOG.debug("Init group patterns");
		if (reportSpec.getGroupSpec() != null) {
			// get group spec
			List<String> groupSpec = reportSpec.getGroupSpec().getPattern();
			// add ECGroupPatterns from spec to groupPatterns set
			for (String pattern : groupSpec) {
				try {
					groupPatterns.add(new Pattern(pattern, PatternUsage.GROUP));
				} catch (ECSpecValidationExceptionResponse e) {
					//LOG.debug("Specification Validation Exception: ", e);
				}	
			}
		}		
	}
	
	/**
	 * This method checks on the basis of the filter patterns if the specified tag could be a member of this report.
	 * 
	 * @param tagURI to check for possible membership
	 * @return true if the tag could be a member of this report and false otherwise
	 * @throws ECSpecValidationException if the tag is invalid
	 * @throws ImplementationException if an implementation exception occurs
	 */
	private boolean isMember(String tagURI) throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse {
				
		if (ECReportSetEnum.isSameECReportSet(ECReportSetEnum.ADDITIONS, reportType)) {
		
			// if report type is additions the tag is only a member if it wasn't a member of the last event cycle	
			Set<Tag> tags = Sets.newHashSet(currentEventCycle.getLastEventCycleTags());
			if (tags != null) {
				for (Tag tag : tags) {
					if (tag.getTagIDAsPureURI().equals(tagURI)) {
						return false;
					}
				}
			}
		}			

		// check if tagURI is member of an exclude pattern
		for (Pattern pattern : excludePatterns) {
			if (pattern.isMember(tagURI)) {
				return false;
			}
		}
		
		// check if there are include patterns specified
		if (includePatterns.size() == 0) {
			return true;
		} else {
			
			// check if tagURI is a member of an include pattern
			for (Pattern pattern : includePatterns) {
				if (pattern.isMember(tagURI)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * This method adds a tag to the matching group of the report.
	 * 
	 * @param tag to add
	 * @throws ECSpecValidationException if the tag is invalid
	 * @throws ImplementationException if an implementation exception occurs
	 */
	private void addTagToReportGroup(Tag tag) throws ImplementationExceptionResponse, ECSpecValidationExceptionResponse {
		
		// get tag URI
		String tagURI = tag.getTagIDAsPureURI();
		// if this one is null, try something different to compense crashes...
		if (null == tagURI) {
			//tagURI = TagHelper.getTDTEngine().bin2hex(tagURI);
		}
		
		// get group name (use group patterns)
		String groupName = getGroupName(tagURI);
		
		//LOG.debug("The group name for tag '" + tagURI + "' is '" + groupName + "'");
		
		// get matching group
		ECReportGroup matchingGroup = null;
		List<ECReportGroup> groups = report.getGroup();
		//if (groups == null) {
		if (groups.isEmpty()) {
			matchingGroup = null;
		} else {
			for (ECReportGroup group : groups) {
				if (groupName == null) {
					if (group.getGroupName() == null) {
						matchingGroup = group;
					}
				} else {
					if (groupName.equals(group.getGroupName())) {
						matchingGroup = group;
					}
				}
			}
		}
	
		// create group if group does not already exist
		if (matchingGroup == null) {
			
			//LOG.debug("Group '" + groupName + "' does not already exist, create it");
						
			// create group
			matchingGroup = new ECReportGroup();
			
			// set name
			matchingGroup.setGroupName(groupName);
			
			// set count
			if (reportSpec.getOutput().isIncludeCount()) {
				ECReportGroupCount groupCount = new ECReportGroupCount();
				groupCount.setCount(0);
				matchingGroup.setGroupCount(groupCount);
			}
			
			// create and set group list
			matchingGroup.setGroupList(new ECReportGroupList());
			
			// add to groups
			report.getGroup().add(matchingGroup);
			
		}
		
		// create group list member
		ECReportGroupListMember groupMember = new ECReportGroupListMember();
			
		TDTEngine tdt = TagHelper.getTDTEngine();
		// RAW DECIMAL	
		if (TagHelper.isReportOutputSpecIncludeRawDecimal(reportSpec.getOutput())) {
			TagHelper.addTagAsRawDecimal(tdt, groupMember, tag);
		}
		// TAG ENCODING
		if (TagHelper.isReportOutputSpecIncludeTagEncoding(reportSpec.getOutput())) {
			TagHelper.addTagAsTagEncoding(tdt, groupMember, tag);
		}
		// RAW HEX
		if (TagHelper.isReportOutputSpecIncludeRawHex(reportSpec.getOutput())) {
			TagHelper.addTagAsRawHex(tdt, groupMember, tag);
		}
		// EPC
		if (TagHelper.isReportOutputSpecIncludeEPC(reportSpec.getOutput())) {
			TagHelper.addTagAsEPC(tdt, groupMember, tag);
		}
		
		// check if we need to add tag stats
		ECReportSpecExtension ecReportSpecExtension = reportSpec.getExtension();
		if ((null != ecReportSpecExtension) && 
				(null != ecReportSpecExtension.getStatProfileNames())) {
			
			//LOG.debug("adding stat profile");
			addStatProfiles(
					tag,
					groupMember,
					ecReportSpecExtension.
						getStatProfileNames().getStatProfileName());
		}
		 
		//ORANGE: check if we need to add user memory in the report
		ECReportOutputSpecExtension outputExtension = reportSpec.getOutput().getExtension(); 
		if (outputExtension != null) {
			if (outputExtension.getFieldList() != null) {
				for (ECReportOutputFieldSpec outputFieldSpec : outputExtension.getFieldList().getField()) {
					String fieldName = outputFieldSpec.getFieldspec().getFieldname();
					if (fieldName.equalsIgnoreCase("UserMemory") && outputFieldSpec.isIncludeFieldSpecInReport()) {
						addUserMemoryToReport(tag, groupMember,fieldName) ;
					}
				}
			}	
		}
		//ORANGE End
		
		// add list member to group list
		List<ECReportGroupListMember> members = matchingGroup.getGroupList().getMember();		
		members.add(groupMember);
		
		// increment group counter
		if (reportSpec.getOutput().isIncludeCount()) {
			matchingGroup.getGroupCount().setCount(matchingGroup.getGroupCount().getCount() + 1);
		}
		
		//LOG.debug("Tag '" + tagURI + "' successfully added to group '" + groupName + "' of report '" + name + "'");
		
	}

	/**
	 * for each statistics profile name add the respective statistics profile.
	 * @param tag the tag holding information the statistics.
	 * @param groupMember the group member where to add the statistics.
	 * @param statProfileName a list of statistic profile names.
	 */
	private void addStatProfiles(Tag tag, ECReportGroupListMember groupMember,
			List<String> statProfileName) {

		ECReportGroupListMemberExtension extension = 
			new ECReportGroupListMemberExtension();
		groupMember.setExtension(extension);
		
		extension.setStats(new Stats());
		List<ECTagStat> ecTagStats = extension.getStats().getStat();
		for (String profile : statProfileName) {		
			//LOG.debug("adding stat profile: " + profile);
		
			ECTagStat ecTagStat = new ECTagStat();
			ecTagStats.add(ecTagStat);
			
			ecTagStat.setProfile(profile);
			ecTagStat.setStatBlocks(new StatBlocks());
			ECReaderStat readerStat = new ECReaderStat();
			ecTagStat.getStatBlocks().getStatBlock().add(readerStat);
			
			readerStat.setReaderName(tag.getReader());
			readerStat.setSightings(new Sightings());
			readerStat.getSightings().getSighting().add(new ECSightingStat());
		}
	}
	
	/**
	 * ORANGE: Gets the value of the user memory and added to the generated report.
	 *
	 * @param tag the tag holding information of user memory.
	 * @param groupMember the group member where to add the user memory.
	 * @param fieldName the field name "UserMemory".
	 */
	private void addUserMemoryToReport (Tag tag, ECReportGroupListMember groupMember, String fieldName) {
		ECReportGroupListMemberExtension extension = new ECReportGroupListMemberExtension();
		groupMember.setExtension(extension);
		extension.setFieldList(new FieldList());
		List<ECReportMemberField>  ecReportMemberFields = extension.getFieldList().getField();
		ECReportMemberField ecReportMemberField = new ECReportMemberField();
		ecReportMemberFields.add(ecReportMemberField);
		ecReportMemberField.setName(fieldName);
		ecReportMemberField.setValue(tag.getUserMemory());
	}

	

	/**
	 * This method get the matching group of this report for the specified tag.
	 * 
	 * @param tagURI to search group for
	 * @return group name
	 * @throws ECSpecValidationException if the tag is invalid
	 * @throws ImplementationException if an implementation exception occurs
	 */
	private String getGroupName(String tagURI) throws ImplementationExceptionResponse, ECSpecValidationExceptionResponse {
		
		for (Pattern pattern : groupPatterns) {
			if (pattern.isMember(tagURI)) {
				return pattern.getGroupName(tagURI);
			}
		}
			
		return null;
		
	}
	
	/**
	 * This method indicates if the report contains any tags.
	 * 
	 * @return true if the report is empty and false otherwise
	 */
	private boolean isEmpty() {
		
		List<ECReportGroup> groups = report.getGroup();
		if (groups != null) {
			for (ECReportGroup group : groups) {
				ECReportGroupList groupList = group.getGroupList();
				if (groupList.getMember().size() > 0) {
					return false;
				}
			}
		}
		return true;

	}
}

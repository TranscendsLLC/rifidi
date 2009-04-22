/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rifidi.edge.epcglobal.ale.api.read.data.ECFilterListMember;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportOutputFieldSpec;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.ECReportOptions;
import org.rifidi.edge.epcglobal.aleread.filters.ALEField;
import org.rifidi.edge.epcglobal.aleread.filters.FilterFactory;
import org.rifidi.edge.epcglobal.aleread.filters.PatternMatcher;
import org.rifidi.edge.epcglobal.aleread.filters.ReportALEField;
import org.rifidi.edge.epcglobal.aleread.groups.GroupFactory;
import org.rifidi.edge.epcglobal.aleread.groups.GroupMatcher;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiReport;

/**
 * Factory for creating reports from ECReportSpecs
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class RifidiReportFactoryimpl implements RifidiReportFactory {
	/** Factory for creating filter instances. */
	private FilterFactory filterFactory;
	/** Factory for creating grouping rules */
	private GroupFactory groupFactory;

	/**
	 * Constructor.
	 */
	public RifidiReportFactoryimpl() {
		filterFactory = new FilterFactory();
		groupFactory = new GroupFactory();
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.epcglobal.aleread.service.RifidiReportFactory#createReport(org.rifidi.edge.epcglobal.ale.api.read.data.ECReportSpec)
	 */
	public RifidiReport createReport(ECReportSpec reportSpec)
			throws ECSpecValidationExceptionResponse {
		int options = 0;
		if ("ADDITION".equals(reportSpec.getReportSet().getSet())) {
			options |= ECReportOptions.REPORT_ADDITIONS;
		} else if ("DELETION".equals(reportSpec.getReportSet().getSet())) {
			options |= ECReportOptions.REPORT_DELETIONS;
		} else if ("CURRENT".equals(reportSpec.getReportSet().getSet())) {
			options |= ECReportOptions.REPORT_CURRENT;
		} else {
			throw new ECSpecValidationExceptionResponse(
					"Unknown report set type "
							+ reportSpec.getReportSet().getSet());
		}
		Map<ALEField, List<PatternMatcher>> includeFilters = new HashMap<ALEField, List<PatternMatcher>>();
		Map<ALEField, List<PatternMatcher>> excludeFilters = new HashMap<ALEField, List<PatternMatcher>>();
		if (reportSpec.getFilterSpec() != null && reportSpec.getFilterSpec().getExtension()!=null) {
			//TODO: ALE1.0 Take care of this
			//reportSpec.getFilterSpec().getExcludePatterns();
			//reportSpec.getFilterSpec().getIncludePatterns();
			for (ECFilterListMember filter : reportSpec.getFilterSpec()
					.getExtension().getFilterList().getFilter()) {

				if ("INCLUDE".equals(filter.getIncludeExclude())) {
					includeFilters.putAll(filterFactory.createMatcher(filter));
				} else if ("EXCLUDE".equals(filter.getIncludeExclude())) {
					excludeFilters.putAll(filterFactory.createMatcher(filter));
				} else {
					throw new ECSpecValidationExceptionResponse(
							"Unknown filter type " + filter.getIncludeExclude());
				}
				// TODO: implement, this is an ALE 1.0 remnant
				filter.getPatList().getPat();
			}
		}

		List<GroupMatcher> groups = new ArrayList<GroupMatcher>();
		ALEField groupfield = null;
		if (reportSpec.getGroupSpec() != null && reportSpec.getGroupSpec().getExtension()!=null) {
			//TODO: ALE 1.0 take care of this
			//reportSpec.getGroupSpec().getPattern()
			groupfield = new ALEField(reportSpec.getGroupSpec().getExtension()
					.getFieldspec());
			for (String pattern : reportSpec.getGroupSpec().getPattern()) {
				groups.add(groupFactory.createMatcher(groupfield, pattern));
			}
		}
		// collect the different options
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
		// collect the fields for the report
		Set<ReportALEField> reportFields = new HashSet<ReportALEField>();
		if (reportSpec.getOutput().getExtension() != null
				&& reportSpec.getOutput().getExtension().getFieldList() != null) {
			for (ECReportOutputFieldSpec spec : reportSpec.getOutput()
					.getExtension().getFieldList().getField()) {
				reportFields.add(new ReportALEField(spec.getName(), spec
						.getFieldspec()));
			}
		}
		// check if we are actually gettin an output.
		if (reportFields.size() == 0
				&& (options & (ECReportOptions.INCLUDE_RAW_DECIMAL
						| ECReportOptions.INCLUDE_RAW_HEX
						| ECReportOptions.INCLUDE_TAG | ECReportOptions.INCLUDE_EPC)) == 0) {
			throw new ECSpecValidationExceptionResponse("No output specified. ");
		}
		return new RifidiReport(reportSpec.getReportName(), options,
				includeFilters, excludeFilters, groupfield, groups,
				reportFields);
	}
}

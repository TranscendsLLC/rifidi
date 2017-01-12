package org.rifidi.edge.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.rifidi.edge.epcglobal.ale.ECBoundarySpec;
import org.rifidi.edge.epcglobal.ale.ECFilterSpec;
import org.rifidi.edge.epcglobal.ale.ECGroupSpec;
import org.rifidi.edge.epcglobal.ale.ECReportOutputSpec;
import org.rifidi.edge.epcglobal.ale.ECReportSpec;
import org.rifidi.edge.epcglobal.ale.ECSpec;
import org.rifidi.edge.epcglobal.ale.ECSpec.LogicalReaders;
import org.rifidi.edge.epcglobal.ale.ECSpec.ReportSpecs;
import org.rifidi.edge.epcglobal.ale.ECSpecValidationException;
import org.rifidi.edge.epcglobal.ale.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.ECTime;
import org.rifidi.edge.epcglobal.ale.ImplementationExceptionResponse;
import org.rifidi.edge.server.epcglobal.ale.ECReportSetEnum;
import org.rifidi.edge.server.epcglobal.ale.Pattern;
import org.rifidi.edge.server.epcglobal.ale.PatternUsage;
import org.rifidi.edge.server.epcglobal.alelr.services.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ECSpecValidator {

	/**
	 * private handle onto the logical reader manager.
	 */
	@Autowired
	private ReaderService readerService;

	/** logger */
	private static final Logger LOG = Logger.getLogger(ECSpecValidator.class);

	/**
	 * constructor for class.
	 */
	public ECSpecValidator() {

	}

	/**
	 * This method validates an ec specification under criterias of chapter
	 * 8.2.11 of ALE specification version 1.0.
	 * 
	 * @param spec
	 *            to validate
	 * @throws ECSpecValidationException
	 *             if the specification is invalid
	 * @throws ImplementationException
	 *             if an implementation exception occurs
	 */
	public void validateSpec(ECSpec spec) throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse {

		if (readerService == null) {
			throw new IllegalStateException("Logical Reader Service is null - aborting");
		}

		// check if the logical readers are known to the implementation
		checkReadersAvailable(spec.getLogicalReaders());

		checkBoundarySpec(spec.getBoundarySpec());
		checkReportSpecs(spec.getReportSpecs());

		// Validate reportSet value: CURRENT ADDITIONS DELETIONS
		List<ECReportSpec> ecReportSpecList = spec.getReportSpecs().getReportSpec();
		for (ECReportSpec ecReportSpec : ecReportSpecList) {
			String reportType = ecReportSpec.getReportSet().getSet();

			if (!(reportType.equals(ECReportSetEnum.CURRENT.name()) || reportType.equals(ECReportSetEnum.ADDITIONS.name())
					|| reportType.equals(ECReportSetEnum.DELETIONS.name()))) {

				throw new ECSpecValidationExceptionResponse("Not valid report type value " + reportType
						+ ". Expected values are: " + ECReportSetEnum.CURRENT + ", " + ECReportSetEnum.ADDITIONS + ", "
						+ ECReportSetEnum.DELETIONS);

			}
		}

	}

	/**
	 * verifies that the spec contains some readers and that those readers are
	 * available in the ALE.
	 * 
	 * @param logicalReaders
	 *            the logical reader definition from the ECSpec.
	 * @param readerManager
	 *            handle to the logical reader manager.
	 * @return true if OK, throws exception otherwise.
	 * @throws ECSpecValidationException
	 *             if no reader specified or the specified readers do not exist
	 *             in the ALE.
	 */
	public boolean checkReadersAvailable(LogicalReaders logicalReaders) throws ECSpecValidationExceptionResponse {
		if ((null == logicalReaders) || (logicalReaders.getLogicalReader().size() == 0)) {
			throw logAndCreateECSpecValidationException("ECSpec does not specify at least one reader");
		}
		for (String logicalReaderName : logicalReaders.getLogicalReader()) {
			if (!readerService.contains(logicalReaderName)) {
				throw logAndCreateECSpecValidationException("LogicalReader '" + logicalReaderName + "' is unknown.");
			}
		}
		return true;
	}

	/**
	 * verifies the boundary spec of an ECSpec.
	 * 
	 * @param boundarySpec
	 *            the boundary spec to verify.
	 * @throws ECSpecValidationException
	 *             if the specification does not meet the specification.
	 * @return true if OK, throws exception otherwise.
	 */
	public boolean checkBoundarySpec(ECBoundarySpec boundarySpec) throws ECSpecValidationExceptionResponse {

		// boundaries parameter of ECSpec is null or omitted
		if (boundarySpec == null) {
			throw logAndCreateECSpecValidationException("The boundaries parameter of ECSpec is null.");
		}

		// start and stop tiggers
		checkTrigger(boundarySpec.getStartTrigger());
		checkTrigger(boundarySpec.getStopTrigger());

		// check if duration, stableSetInterval or repeatPeriod is negative
		checkTimeNotNegative(boundarySpec.getDuration(), "The duration field of ECBoundarySpec is negative.");
		checkTimeNotNegative(boundarySpec.getStableSetInterval(),
				"The stableSetInterval field of ECBoundarySpec is negative.");
		checkTimeNotNegative(boundarySpec.getRepeatPeriod(), "The repeatPeriod field of ECBoundarySpec is negative.");

		// check if start trigger is non-empty and repeatPeriod is non-zero
		checkStartTriggerConstraintsOnRepeatPeriod(boundarySpec);

		// check if a stopping condition is specified
		checkBoundarySpecStoppingCondition(boundarySpec);

		return true;
	}

	/**
	 * check that the provided time value is not negative.
	 * 
	 * @param duration
	 *            the time value to check.
	 * @param string
	 *            a message string to show in the exception.
	 * @return true if OK, throws Exception otherwise.
	 * @throws ECSpecValidationException
	 *             if the time value is negative.
	 */
	public boolean checkTimeNotNegative(ECTime duration, String string) throws ECSpecValidationExceptionResponse {
		if (duration != null) {
			if (duration.getValue() < 0) {
				throw logAndCreateECSpecValidationException("The duration field of ECBoundarySpec is negative.");
			}
		}
		return true;
	}

	/**
	 * if a start trigger is specified, then the repeat period must be 0.
	 * 
	 * @param boundarySpec
	 *            the boundary spec to test.
	 * @return true if OK, throws exception otherwise.
	 * @throws ECSpecValidationException
	 *             if a start trigger is specified, then the repeat period must
	 *             be 0. if not, throw an exception.
	 */
	public boolean checkStartTriggerConstraintsOnRepeatPeriod(ECBoundarySpec boundarySpec)
			throws ECSpecValidationExceptionResponse {
		if ((boundarySpec.getStartTrigger() != null) && (boundarySpec.getRepeatPeriod().getValue() != 0)) {
			throw logAndCreateECSpecValidationException(
					"The startTrigger field of ECBoundarySpec is non-empty and the repeatPeriod field of ECBoundarySpec is non-zero.");
		}
		return true;
	}

	/**
	 * check the stopping condition of the EC boundary spec:<br/>
	 * if there is no stop trigger or no duration value or no stableSetInterval,
	 * throw an exception.
	 * 
	 * @param boundarySpec
	 *            the boundary spec to test.
	 * @return true if OK, throws exception otherwise.
	 * @throws ECSpecValidationException
	 *             if there is no stop trigger or no duration value or no
	 *             stableSetInterval, throw an exception.
	 */
	public boolean checkBoundarySpecStoppingCondition(ECBoundarySpec boundarySpec)
			throws ECSpecValidationExceptionResponse {
		if ((boundarySpec.getStopTrigger() == null) && (boundarySpec.getDuration() == null)
				&& (boundarySpec.getStableSetInterval() == null)) {
			throw logAndCreateECSpecValidationException("No stopping condition is specified in ECBoundarySpec.");
		}
		return true;
	}

	/**
	 * checks the report specs.
	 * 
	 * @param reportSpecs
	 *            the report specs to verify.
	 * @throws ECSpecValidationException
	 *             when the specifications do not meet the requirements.
	 * @return true if the specification is OK, throws exception otherwise.
	 */
	public boolean checkReportSpecs(ReportSpecs reportSpecs) throws ECSpecValidationExceptionResponse {
		// check if there is a ECReportSpec instance
		if ((reportSpecs == null) || (reportSpecs.getReportSpec().size() == 0)) {
			throw logAndCreateECSpecValidationException("List of ECReportSpec is empty or null.");
		}
		final List<ECReportSpec> reportSpecList = reportSpecs.getReportSpec();

		// check that no two ECReportSpec instances have identical names
		checkReportSpecNoDuplicateReportSpecNames(reportSpecList);

		// check filters
		for (ECReportSpec reportSpec : reportSpecList) {
			checkFilterSpec(reportSpec.getFilterSpec());
		}

		// check grouping patterns
		for (ECReportSpec reportSpec : reportSpecList) {
			checkGroupSpec(reportSpec.getGroupSpec());
		}

		// check if there is a output type specified for each ECReportSpec
		for (ECReportSpec reportSpec : reportSpecList) {
			checkReportOutputSpec(reportSpec.getReportName(), reportSpec.getOutput());
		}
		return true;
	}

	/**
	 * verify that no two report specs have the same name.
	 * 
	 * @param reportSpecList
	 *            the list of report specs to check.
	 * @return a list containing all the names of the different report specs.
	 * @throws ECSpecValidationException
	 *             when there are two report specs with the same name.
	 */
	public Set<String> checkReportSpecNoDuplicateReportSpecNames(List<ECReportSpec> reportSpecList)
			throws ECSpecValidationExceptionResponse {
		Set<String> reportSpecNames = new HashSet<String>();
		for (ECReportSpec reportSpec : reportSpecList) {
			LOG.debug("Verify report spec name not specified twice: " + reportSpec.getReportName());
			if (reportSpecNames.contains(reportSpec.getReportName())) {
				throw logAndCreateECSpecValidationException(
						"Two ReportSpecs instances have identical names '" + reportSpec.getReportName() + "'.");
			} else {
				reportSpecNames.add(reportSpec.getReportName());
			}
		}
		return reportSpecNames;
	}

	/**
	 * verify the report output specification.
	 * 
	 * @param outputSpec
	 *            the output specification.
	 * @return true if the specification is OK, otherwise throws exception.
	 * @throws ECSpecValidationException
	 *             violates the specification.
	 */
	public boolean checkReportOutputSpec(String reportName, ECReportOutputSpec outputSpec)
			throws ECSpecValidationExceptionResponse {
		if (null == outputSpec) {
			throw logAndCreateECSpecValidationException("there is no output spec for report spec: " + reportName);
		}
		if (!outputSpec.isIncludeEPC() && !outputSpec.isIncludeTag() && !outputSpec.isIncludeRawHex()
				&& !outputSpec.isIncludeRawDecimal() && !outputSpec.isIncludeCount()) {
			throw logAndCreateECSpecValidationException(
					"The ECReportOutputSpec of ReportSpec '" + reportName + "' has no output type specified.");
		}
		return true;
	}

	/**
	 * check the group spec patterns do not have intersecting groups -> all
	 * group patterns have to be disjoint:<br/>
	 * <ul>
	 * <li>the same pattern is not allowed to occur twice</li>
	 * <li>two different pattern with intersecting selectors are not allowed
	 * </li>
	 * <li>no pattern at all is allowed</li>
	 * </ul>
	 * 
	 * @param groupSpec
	 *            the group spec to tested.
	 * @throws ECSpecValidationException
	 *             upon violation.
	 * @return true if filter group spec is valid. exception otherwise.
	 */
	public boolean checkGroupSpec(ECGroupSpec groupSpec) throws ECSpecValidationExceptionResponse {
		if (groupSpec != null) {
			String[] patterns = groupSpec.getPattern().toArray(new String[] {});
			for (int i = 0; i < patterns.length - 1; i++) {
				final String pattern1 = patterns[i];
				for (int j = i + 1; j < patterns.length; j++) {
					final String pattern2 = patterns[j];
					if (!patternDisjoint(pattern1, pattern2)) {
						throw logAndCreateECSpecValidationException("The two grouping patterns '" + pattern1 + "' and '"
								+ pattern2 + "' are not disjoint.");
					}
				}
			}
		}
		return true;
	}

	/**
	 * check the filter spec. if the filter spec is null, it is ignored.
	 * 
	 * @param filterSpec
	 *            the filter spec to verify.
	 * @throws ECSpecValidationException
	 *             upon violation of the filter pattern.
	 * @return true if filter spec is valid. exception otherwise.
	 */
	public boolean checkFilterSpec(ECFilterSpec filterSpec) throws ECSpecValidationExceptionResponse {
		if (filterSpec != null) {
			// check include patterns
			if (filterSpec.getIncludePatterns() != null) {
				for (String pattern : filterSpec.getIncludePatterns().getIncludePattern()) {
					new Pattern(pattern, PatternUsage.FILTER);
				}
			}

			// check exclude patterns
			if (filterSpec.getExcludePatterns() != null) {
				for (String pattern : filterSpec.getExcludePatterns().getExcludePattern()) {
					new Pattern(pattern, PatternUsage.FILTER);
				}
			}
		}
		return true;
	}

	/**
	 * test if two pattern are disjoint.
	 * 
	 * @param pattern1
	 *            the first and reference pattern.
	 * @param pattern2
	 *            the second pattern.
	 * @return true if pattern not disjoint, false otherwise.
	 */
	public boolean patternDisjoint(String pattern1, String pattern2) throws ECSpecValidationExceptionResponse {
		Pattern pattern = new Pattern(pattern1, PatternUsage.GROUP);
		return pattern.isDisjoint(pattern2);
	}

	/**
	 * log the given string and then create from the string an
	 * ECSpecValidationException.
	 * 
	 * @param string
	 *            the log and exception string.
	 * @return the ECSpecValidationException created from the input string.
	 */
	private ECSpecValidationExceptionResponse logAndCreateECSpecValidationException(String string) {
		LOG.debug(string);
		return new ECSpecValidationExceptionResponse(string);
	}

	/**
	 * This method checks if the trigger is valid or not.
	 * 
	 * @param trigger
	 *            to check
	 * @throws ECSpecValidationException
	 *             if the trigger is invalid.
	 */
	private void checkTrigger(String trigger) throws ECSpecValidationExceptionResponse {
		// TODO: implement checkTrigger
		LOG.debug("CHECK TRIGGER not implemented");
	}

}

package org.rifidi.edge.epcglobal.aleread.service;

import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiReport;

/**
 * Factory for creating report objects.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface RifidiReportFactory {
	/**
	 * Create a new report definition from the ALE description.
	 * 
	 * @param reportSpec
	 * @return
	 * @throws ECSpecValidationExceptionResponse
	 */
	public abstract RifidiReport createReport(ECReportSpec reportSpec)
			throws ECSpecValidationExceptionResponse;

}
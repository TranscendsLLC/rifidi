package org.rifidi.edge.epcglobal.aleread.service;

import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiReport;

public interface RifidiReportFactory {

	public abstract RifidiReport createReport(ECReportSpec reportSpec)
			throws ECSpecValidationExceptionResponse;

}
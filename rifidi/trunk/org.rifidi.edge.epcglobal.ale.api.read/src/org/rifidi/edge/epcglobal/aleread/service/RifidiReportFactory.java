/*
 * 
 * RifidiReportFactory.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
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
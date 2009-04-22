/* 
 *  ReportHandler.java
 *  Created:	Apr 22, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.editor
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.editor.reports;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReport;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportGroup;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportGroupListMember;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReports;
import org.rifidi.edge.client.ale.api.xsd.epcglobal.EPC;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ReportHandler {

	private Log logger = LogFactory.getLog(ReportHandler.class);

	public ArrayList<String> handleReports(ECReports reports) {
		ArrayList<String> strings = new ArrayList<String>();
		logger.debug("Handling incomming reports");

		List<ECReport> theReports = reports.getReports().getReport();
		// collect all the tags
		List<EPC> epcs = new LinkedList<EPC>();
		if (theReports != null) {
			for (ECReport report : theReports) {
				if (report.getGroup() != null) {
					for (ECReportGroup group : report.getGroup()) {
						if (group.getGroupList() != null) {
							for (ECReportGroupListMember member : group
									.getGroupList().getMember()) {
								if (member.getRawHex() != null) {
									epcs.add(member.getRawHex());

								}
							}
						}
					}
				}
			}
		}

		if (epcs.size() == 0) {
			logger.debug("no epc received");

		} else {

			for (EPC epc : epcs) {

				strings.add(epc.getValue());

			}

		}

		return strings;

	}

}

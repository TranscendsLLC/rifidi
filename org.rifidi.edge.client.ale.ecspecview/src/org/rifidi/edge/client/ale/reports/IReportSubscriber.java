/* 
 *  IReportSubscriber.java
 *  Created:	May 26, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.ecspecview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.reports;

import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReports;

/**
 * This interface should be implemented in order to receive reports from
 * ReportReceiverSingleton. The implementing Class should then be subscribed to
 * the latter.
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public interface IReportSubscriber {

	/**
	 * The reports get delivered by the ReportReceiverSingleton through this
	 * method.
	 * 
	 * @param reports
	 *            - reports to be delivered.
	 */
	public void pushReport(ECReports reports);

}

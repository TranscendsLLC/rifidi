/**
 * 
 */
package org.rifidi.edge.client.ale.reports;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReports;

/**
 * @author Jochen Mader - jochen@pramari.com
 *
 */
@XmlRootElement
public class ReportAnswer {
	public ECReports reports;
}

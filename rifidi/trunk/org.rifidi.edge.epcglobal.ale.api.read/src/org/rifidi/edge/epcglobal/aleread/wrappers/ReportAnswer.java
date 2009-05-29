/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.wrappers;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.epcglobal.ale.api.read.data.ECReports;

/**
 * Required to send a report back. It's just a simple, stupid container to have
 * a root element.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
@XmlRootElement
public class ReportAnswer {
	/** The reports to send back. */
	public ECReports reports;
}

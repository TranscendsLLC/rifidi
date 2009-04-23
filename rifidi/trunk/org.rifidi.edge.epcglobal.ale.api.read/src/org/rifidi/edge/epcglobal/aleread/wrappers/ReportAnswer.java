/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.wrappers;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.epcglobal.ale.api.read.data.ECReports;

/**
 * @author Jochen Mader - jochen@pramari.com
 *
 */
@XmlRootElement
public class ReportAnswer {
	public ECReports reports;
}

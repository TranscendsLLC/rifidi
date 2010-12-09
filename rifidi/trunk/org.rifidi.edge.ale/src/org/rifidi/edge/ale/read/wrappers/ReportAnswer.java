/*
 * 
 * ReportAnswer.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.ale.read.wrappers;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.ale.api.read.data.ECReports;

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

/*
 * 
 * ReportALEField.java
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
package org.rifidi.edge.epcglobal.aleread.filters;

import org.rifidi.edge.epcglobal.ale.api.read.data.ECFieldSpec;

/**
 * Representation of a field in the report.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ReportALEField extends ALEField {

	private String name;

	/**
	 * @param spec
	 * @param fieldName
	 */
	public ReportALEField(String fieldName, ECFieldSpec spec) {
		super(spec);
	}

	/**
	 * @return the name
	 */
	public String getFieldName() {
		return name;
	}

}

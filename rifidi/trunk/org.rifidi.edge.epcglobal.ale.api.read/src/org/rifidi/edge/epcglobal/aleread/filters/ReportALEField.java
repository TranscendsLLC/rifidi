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

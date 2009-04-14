/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.filters;

import org.rifidi.edge.epcglobal.ale.api.read.data.ECFieldSpec;

/**
 * @author Jochen Mader - jochen@pramari.com
 *
 */
public class ReportALEField extends ALEField{

	private String name;
	/**
	 * @param spec
	 * @param fieldName
	 */
	public ReportALEField(String fieldName, ECFieldSpec spec) {
		super(spec);
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return the name
	 */
	public String getFieldName() {
		return name;
	}

}

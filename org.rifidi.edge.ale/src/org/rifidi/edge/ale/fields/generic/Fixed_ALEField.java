/*
 *  Fixed_ALEField.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.fields.generic;

import org.rifidi.edge.ale.fields.ALEField;
import org.rifidi.edge.tags.data.PhysicalTagModel;

/**
 * ALE Field for Absolute Addresses. 
 * 
 * @author Kyle Neumeier kyle@pramari.com
 * 
 */
public class Fixed_ALEField implements ALEField {

	private PhysicalTagModel _model;
	public Fixed_ALEField(PhysicalTagModel model){
		_model = model;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.fields.ALEField#getData()
	 */
	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.fields.ALEField#getData(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String getData(String type, String format) {
		// TODO Auto-generated method stub
		return null;
	}
}

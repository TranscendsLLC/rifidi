/*
 * StringWidgetData.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.mbean.ui.widgets.data;

import javax.management.JMX;
import javax.management.MBeanAttributeInfo;

/**
 * This is the concrete class for data used to construct an String Widget
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class StringWidgetData extends AbstractWidgetData {

	/**
	 * Constructor.  
	 * 
	 * @param element
	 */
	public StringWidgetData(MBeanAttributeInfo element) {
		super(element);
	}

	/**
	 * Get the regular expression used for validation associated with this
	 * widget.
	 * 
	 * TODO: for now, match anything. Worry about true validation later.
	 * 
	 * @return
	 */
	public String getRegex() {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.widgets.data.AbstractWidgetData#getDefaultValue
	 * ()
	 */
	@Override
	public String getDefaultValue() {
		Object defVal = element.getDescriptor().getFieldValue(
				JMX.DEFAULT_VALUE_FIELD);
		if (defVal == null) {
			return "";
		} else {
			return (String) defVal;
		}
	}

}

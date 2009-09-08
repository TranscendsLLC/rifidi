/*
 * BooleanWidgetData.java
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

import javax.management.MBeanAttributeInfo;

/**
 * This is the concrete class for data used to construct a Boolean Widget
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class BooleanWidgetData extends AbstractWidgetData {

	/**
	 * Constructor.
	 * 
	 * @param element
	 */
	public BooleanWidgetData(MBeanAttributeInfo element) {
		super(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.mbean.ui.widgets.data.AbstractWidgetData#
	 * getDefaultValue()
	 */
	@Override
	public Object getDefaultValue() {
		return null;
	}

}

/*
 * IntegerWidgetData.java
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
 * This is the concrete class for data used to construct an Integer Widget
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class IntegerWidgetData extends AbstractWidgetData {

	/**
	 * Constructor.  
	 * 
	 * @param element
	 */
	public IntegerWidgetData(MBeanAttributeInfo element) {
		super(element);
	}
	
	/**
	 * @return The maximum value of this widget
	 */
	public int maxValue(){
		Object o = element.getDescriptor().getFieldValue(JMX.MAX_VALUE_FIELD);
		if(o == null ){
			return Integer.MAX_VALUE;
		}else{
			return (Integer)o;
		}
	}
	
	/**
	 * @return The minimum value for this widget
	 */
	public int minValue(){
		Object o = element.getDescriptor().getFieldValue(JMX.MIN_VALUE_FIELD);
		if(o == null ){
			return Integer.MIN_VALUE;
		}else{
			return (Integer)o;
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.client.mbean.ui.widgets.data.AbstractWidgetData#getDefaultValue()
	 */
	@Override
	public Integer getDefaultValue() {
		Object defVal = element.getDescriptor().getFieldValue(JMX.DEFAULT_VALUE_FIELD);
		if(defVal==null){
			return 0;
		}else{
			return (Integer)defVal;
		}
	}
	
	

}

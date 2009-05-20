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

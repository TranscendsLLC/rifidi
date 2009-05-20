package org.rifidi.edge.client.mbean.ui.widgets.data;

import javax.management.JMX;
import javax.management.MBeanAttributeInfo;

/**
 * Concreate class for data used to construct a Long Widget
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class LongWidgetData extends AbstractWidgetData {

	/**
	 * Constructor.
	 * 
	 * @param element
	 */
	public LongWidgetData(MBeanAttributeInfo element) {
		super(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.mbean.ui.widgets.data.AbstractWidgetData#
	 * getDefaultValue()
	 */
	@Override
	public Long getDefaultValue() {
		Object o = element.getDescriptor().getFieldValue(
				JMX.DEFAULT_VALUE_FIELD);
		if (o == null) {
			return 0l;
		} else {
			return (Long) o;
		}
	}

	/**
	 * @return The maximum value of this widget
	 */
	public Long maxValue() {
		Object o = element.getDescriptor().getFieldValue(JMX.MAX_VALUE_FIELD);
		if (o == null) {
			return Long.MAX_VALUE;
		} else {
			return (Long) o;
		}
	}

	/**
	 * @return The minimum value for this widget
	 */
	public Long minValue() {
		Object o = element.getDescriptor().getFieldValue(JMX.MIN_VALUE_FIELD);
		if (o == null) {
			return Long.MIN_VALUE;
		} else {
			return (Long) o;
		}
	}

}

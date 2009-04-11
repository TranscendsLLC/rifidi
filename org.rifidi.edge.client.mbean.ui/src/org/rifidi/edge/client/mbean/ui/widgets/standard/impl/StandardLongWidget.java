/**
 * 
 */
package org.rifidi.edge.client.mbean.ui.widgets.standard.impl;

import javax.management.Attribute;

import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractNumberWidget;
import org.rifidi.edge.client.mbean.ui.widgets.data.LongWidgetData;

/**
 * A control used to display a long. Note that because the underlying control is
 * a spinner, it can only display values within the integer range
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class StandardLongWidget<T extends LongWidgetData> extends
		AbstractNumberWidget<T> {

	/**
	 * @param data
	 */
	public StandardLongWidget(T data) {
		super(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractNumberWidget
	 * #buildCustomSpinner()
	 */
	@Override
	protected void buildCustomSpinner() {
		if (data.maxValue() > Integer.MAX_VALUE) {
			spinner.setMaximum(Integer.MAX_VALUE);
		} else {
			spinner.setMaximum(data.maxValue().intValue());
		}

		if (data.minValue() < Integer.MIN_VALUE) {
			spinner.setMinimum(Integer.MIN_VALUE);
		} else {
			spinner.setMinimum(data.minValue().intValue());
		}
		spinner.setSelection(data.getDefaultValue().intValue());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#setValue(java
	 * .lang.Object)
	 */
	@Override
	public String setValue(Attribute value) {
		spinner.setSelection(((Long) value.getValue()).intValue());
		return null;
	}

	@Override
	public Attribute getAttribute() {
		return new Attribute(getElementName(), Long
				.parseLong(spinner.getText()));
	}

}

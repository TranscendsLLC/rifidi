package org.rifidi.edge.client.mbean.ui.widgets.standard.impl;

import javax.management.Attribute;

import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractNumberWidget;
import org.rifidi.edge.client.mbean.ui.widgets.data.IntegerWidgetData;

/**
 * A default implementation of a Number Widget, which uses a spinner for
 * Integers and Floats. One problem with this implemetation is that a spinenr
 * cannot display anything larger than MAX_INT, so is not really big enough for
 * a true Float
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class StandardIntegerWidget<T extends IntegerWidgetData> extends
		AbstractNumberWidget<T> {

	/**
	 * Construct a new Number widget
	 * 
	 * @param data
	 *            An IntegerWidgetData or FloatWidgetData
	 */
	public StandardIntegerWidget(T data) {
		super(data);
	}

	@Override
	protected void buildCustomSpinner() {
		if (data.maxValue() > Integer.MAX_VALUE) {
			spinner.setMaximum(Integer.MAX_VALUE);
		} else {
			spinner.setMaximum(data.maxValue());
		}

		if (data.minValue() < Integer.MIN_VALUE) {
			spinner.setMinimum(Integer.MIN_VALUE);
		} else {
			spinner.setMinimum(data.minValue());
		}

		spinner.setSelection(data.getDefaultValue());


	}

	@Override
	public String setValue(Object value) {
		spinner.setSelection((Integer) value);
		return null;
	}

	@Override
	public Attribute getAttribute() {
		return new Attribute(getElementName(), Integer.parseInt(spinner
				.getText()));
	}

}

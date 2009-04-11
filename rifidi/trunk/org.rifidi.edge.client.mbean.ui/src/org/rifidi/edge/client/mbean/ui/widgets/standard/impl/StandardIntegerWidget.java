package org.rifidi.edge.client.mbean.ui.widgets.standard.impl;

import javax.management.Attribute;

import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractNumberWidget;
import org.rifidi.edge.client.mbean.ui.widgets.data.IntegerWidgetData;

/**
 * A control used to display an integer
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
		spinner.setMaximum(data.maxValue());
		spinner.setMinimum(data.minValue());
		spinner.setSelection(data.getDefaultValue());

	}

	@Override
	public String setValue(Attribute value) {
		spinner.setSelection((Integer) value.getValue());
		return null;
	}

	@Override
	public Attribute getAttribute() {
		return new Attribute(getElementName(), Integer.parseInt(spinner
				.getText()));
	}

}

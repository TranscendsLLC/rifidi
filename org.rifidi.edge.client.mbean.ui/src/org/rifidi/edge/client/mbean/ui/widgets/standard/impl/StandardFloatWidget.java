
package org.rifidi.edge.client.mbean.ui.widgets.standard.impl;

import javax.management.Attribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractNumberWidget;
import org.rifidi.edge.client.mbean.ui.widgets.data.FloatWidgetData;

/**
 * A widget that displays float. Note that Because it uses a spinner, the value
 * ranges are limited
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class StandardFloatWidget<T extends FloatWidgetData> extends
		AbstractNumberWidget<T> {

	/**
	 * @param data
	 */
	public StandardFloatWidget(T data) {
		super(data);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractNumberWidget#createSpinner(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createSpinner(Composite parent) {
		spinner = new Spinner(parent, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.minimumWidth = 150;
		spinner.setLayoutData(gridData);
		
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractNumberWidget#initializeSpinner()
	 */
	@Override
	protected void initializeSpinner() {
		spinner.setDigits(data.getNumDecimalPlaces());
		int offset = (int) Math.pow(10, data.getNumDecimalPlaces());
		int max = data.maxValue().intValue() * offset;
		int min = data.minValue().intValue() * offset;
		int value = (data.getDefaultValue().intValue() * offset);
	
		if (max > Integer.MAX_VALUE) {
			spinner.setMaximum(Integer.MAX_VALUE);
		} else {
			spinner.setMaximum(max);
		}
	
		if (min < Integer.MIN_VALUE) {
			spinner.setMinimum(min);
		} else {
			spinner.setMinimum(min);
		}
	
		spinner.setSelection(value);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#getAttribute()
	 */
	@Override
	public Attribute getAttribute() {
		return new Attribute(getElementName(), Float.parseFloat(spinner
				.getText()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractNumberWidget#getValueAsInteger(javax.management.Attribute)
	 */
	@Override
	protected Integer getValueAsInteger(Attribute value) {
		Float f = (Float)value.getValue();
		int offset = (int) Math.pow(10, data.getNumDecimalPlaces());
		return f.intValue() * offset;
	}

}

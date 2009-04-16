package org.rifidi.edge.client.mbean.ui.widgets.standard.impl;

import javax.management.Attribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractIntegerWidget
	 * #createSpinner(org.eclipse.swt.widgets.Composite)
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractNumberWidget
	 * #initializeSpinner()
	 */
	@Override
	protected void initializeSpinner() {
		spinner.setMaximum(data.maxValue());
		spinner.setMinimum(data.minValue());
		spinner.setSelection(data.getDefaultValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractNumberWidget
	 * #getValueAsInteger(javax.management.Attribute)
	 */
	@Override
	protected Integer getValueAsInteger(Attribute value) {
		return (Integer) value.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractWidget
	 * #getAttribute()
	 */
	@Override
	public Attribute getAttribute() {
		return new Attribute(getElementName(), Integer.parseInt(spinner
				.getText()));
	}

}

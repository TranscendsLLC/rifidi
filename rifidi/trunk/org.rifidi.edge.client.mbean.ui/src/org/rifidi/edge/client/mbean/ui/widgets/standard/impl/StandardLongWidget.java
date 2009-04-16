/**
 * 
 */
package org.rifidi.edge.client.mbean.ui.widgets.standard.impl;

import javax.management.Attribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
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

	@Override
	protected void createSpinner(Composite parent) {
		spinner = new Spinner(parent, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.minimumWidth = 150;
		spinner.setLayoutData(gridData);
	
	}

	@Override
	protected void initializeSpinner() {
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

	@Override
	public Attribute getAttribute() {
		return new Attribute(getElementName(), Long
				.parseLong(spinner.getText()));
	}

	@Override
	protected Integer getValueAsInteger(Attribute value) {
		return ((Long) value.getValue()).intValue();
	}

}

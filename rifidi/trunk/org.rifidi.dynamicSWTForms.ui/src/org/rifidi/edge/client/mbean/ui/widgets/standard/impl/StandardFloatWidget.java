/**
 * 
 */
package org.rifidi.edge.client.mbean.ui.widgets.standard.impl;

import javax.management.Attribute;

import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractNumberWidget;
import org.rifidi.edge.client.mbean.ui.widgets.data.FloatWidgetData;

/**
 * @author kyle
 *
 */
public class StandardFloatWidget<T extends FloatWidgetData> extends AbstractNumberWidget<T> {

	/**
	 * @param data
	 */
	public StandardFloatWidget(T data) {
		super(data);
	}
	
	@Override
	protected void buildCustomSpinner() {
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
	

	/* (non-Javadoc)
	 * @see org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#setValue(java.lang.Object)
	 */
	@Override
	public String setValue(Object value) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#getAttribute()
	 */
	@Override
	public Attribute getAttribute() {
		return new Attribute(getElementName(), Float.parseFloat(spinner.getText()));
	}



}

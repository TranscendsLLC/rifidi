/**
 * 
 */
package org.rifidi.dynamicswtforms.ui.widgets.abstractwidgets;

import org.eclipse.swt.widgets.Spinner;
import org.rifidi.dynamicswtforms.ui.widgets.AbstractWidget;
import org.rifidi.dynamicswtforms.ui.widgets.data.AbstractWidgetData;

/**
 *This is a convenience abstract class to use for widgets who want to display a
 * number widget as a spinner control. Implementors will still need to build the
 * control
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractNumberWidget extends AbstractWidget {

	protected Spinner spinner;

	/**
	 * @param data
	 */
	public AbstractNumberWidget(AbstractWidgetData data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#disable()
	 */
	@Override
	public void disable() {
		this.spinner.setEnabled(false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#enable()
	 */
	@Override
	public void enable() {
		if (this.data.isEditable()) {
			this.spinner.setEnabled(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#getValue()
	 */
	@Override
	public String getValue() {
		return spinner.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#setValue(java
	 * .lang.String)
	 */
	@Override
	public String setValue(String value) {
		try {
			int intval = Integer.parseInt(value);
			this.spinner.setSelection(intval);

		} catch (NumberFormatException ex) {
			return "Cannot convert " + value + " to integer";

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#validate()
	 */
	@Override
	public String validate() {
		return null;
	}

}

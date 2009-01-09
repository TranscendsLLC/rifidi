package org.rifidi.dynamicswtforms.ui.widgets.standard.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.rifidi.dynamicswtforms.ui.widgets.abstractwidgets.AbstractNumberWidget;
import org.rifidi.dynamicswtforms.ui.widgets.data.AbstractWidgetData;
import org.rifidi.dynamicswtforms.ui.widgets.data.FloatWidgetData;
import org.rifidi.dynamicswtforms.ui.widgets.data.IntegerWidgetData;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;

/**
 * A default implementation of a Number Widget, which uses a spinner for
 * Integers and Floats. One problem with this implemetation is that a spinenr
 * cannot display anything larger than MAX_INT, so is not really big enough for
 * a true Float
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class NumberWidget extends AbstractNumberWidget {

	/**
	 * Set to true if the user has modified the data since the last time the
	 * listeners have been notified
	 */
	private boolean dirty = false;

	/**
	 * Construct a new Number widget
	 * 
	 * @param data
	 *            An IntegerWidgetData or FloatWidgetData
	 */
	public NumberWidget(AbstractWidgetData data) {
		super(data);
	}

	/**
	 * {@link AbstractNumberWidget#createControl(Composite)}
	 */
	@Override
	public void createControl(Composite parent) {
		spinner = new Spinner(parent, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.minimumWidth = 150;
		spinner.setLayoutData(gridData);

		if (data.getType() == FormElementType.INTEGER) {
			buildIntegerSpinner();
		} else {
			buildFloatSpinner();
		}

		spinner.setEnabled(data.isEditable());

	}

	/**
	 * Build a spinner for an integer
	 */
	private void buildIntegerSpinner() {
		IntegerWidgetData intData = (IntegerWidgetData) data;
		if (intData.maxValue() > Integer.MAX_VALUE) {
			spinner.setMaximum(Integer.MAX_VALUE);
		} else {
			spinner.setMaximum(intData.maxValue());
		}

		if (intData.minValue() < Integer.MIN_VALUE) {
			spinner.setMinimum(Integer.MIN_VALUE);
		} else {
			spinner.setMinimum(intData.minValue());
		}

		spinner.setSelection(Integer.parseInt(intData.getDefaultValue()));

		spinner.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				dirty = true;

			}

		});

		spinner.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					if (dirty == true) {
						dirty = false;
						notifyListenersDataChanged(spinner.getText());
					}
				} else
					notifyListenersKeyReleased();

			}

		});
	}

	/**
	 * Build a spinner for a Float
	 */
	private void buildFloatSpinner() {
		FloatWidgetData floatData = (FloatWidgetData) data;
		spinner.setDigits(floatData.getNumDecimalPlaces());
		int offset = (int) Math.pow(10, floatData.getNumDecimalPlaces());
		int max = floatData.maxValue().intValue() * offset;
		int min = floatData.minValue().intValue() * offset;
		int value = Integer.valueOf(data.getDefaultValue()) * offset;

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

}

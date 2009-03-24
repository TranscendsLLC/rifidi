/**
 * 
 */
package org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.rifidi.edge.client.mbean.ui.widgets.data.AbstractWidgetData;

/**
 *This is a convenience abstract class to use for widgets who want to display a
 * number widget as a spinner control. Implementors will still need to build the
 * control
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractNumberWidget<T extends AbstractWidgetData>
		extends AbstractWidget<T> {

	/** The spinner for this widget */
	protected Spinner spinner;

	/**
	 * Set to true if the user has modified the data since the last time the
	 * listeners have been notified
	 */
	private boolean dirty = false;

	/**
	 * @param data
	 */
	public AbstractNumberWidget(T data) {
		super(data);
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
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#validate()
	 */
	@Override
	public String validate() {
		return null;
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
		buildCustomSpinner();
		spinner.setEnabled(data.isEditable());
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

	protected abstract void buildCustomSpinner();

}

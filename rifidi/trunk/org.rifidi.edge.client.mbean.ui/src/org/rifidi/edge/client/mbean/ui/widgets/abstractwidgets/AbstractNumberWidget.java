package org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets;

import javax.management.Attribute;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.rifidi.edge.client.mbean.ui.widgets.data.AbstractWidgetData;

/**
 *This is a convenience abstract class to use for widgets who want to display a
 * number widget as a spinner control. Implementors will still need to build the
 * control
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class AbstractNumberWidget<T extends AbstractWidgetData>
		extends AbstractWidget<T> {

	/** The spinner for this widget */
	protected Spinner spinner;

	/**
	 * Constructor
	 * 
	 * @param data
	 */
	public AbstractNumberWidget(T data) {
		super(data);
	}

	/**
	 * {@link AbstractNumberWidget#createControl(Composite)}
	 */
	@Override
	public void createControl(Composite parent) {
		createSpinner(parent);
		initializeSpinner();
		spinner.setEnabled(data.isEditable());
		addSpinnerListeners();
	}

	/**
	 * Subclasses should use this method to actually create the spinner control.
	 * They should assign the newly created spinner to the protected spinner
	 * variable. They should also set the spinner's layout data.
	 * 
	 * @param parent
	 */
	protected abstract void createSpinner(Composite parent);

	/**
	 * Subclasses should use this method to set the spinner's min, max, and
	 * initial values.
	 */
	protected abstract void initializeSpinner();

	/**
	 * This method adds swt listeners to the spinner control and notifies
	 * MBeanInfoWidgetListeners when the value of the spinner changes in some
	 * way. By default it adds a a modify listener, whicn notifies listeners
	 * whenever data changes. Subclasses can override this method if they want
	 * to provide different behavior for this spinner.
	 */
	protected void addSpinnerListeners() {
		spinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				notifyListenersDataChanged(spinner.getText());
			}
		});
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractWidget
	 * #setValue(javax.management.Attribute)
	 */
	@Override
	public String setValue(Attribute value) {
		spinner.setSelection(getValueAsInteger(value));
		super.notifyListenersClean();
		return null;
	}

	/**
	 * Subclasses should return the value in the attribute as an Integer
	 * 
	 * @param value
	 * @return
	 */
	protected abstract Integer getValueAsInteger(Attribute value);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractWidget
	 * #dispose()
	 */
	@Override
	public void dispose() {
		if (spinner != null) {
			spinner.dispose();
		}
	}

}

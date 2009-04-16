package org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets;

import java.util.ArrayList;

import javax.management.Attribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.rifidi.edge.client.mbean.ui.MBeanInfoWidgetListener;
import org.rifidi.edge.client.mbean.ui.widgets.data.AbstractWidgetData;

/**
 * An abstract implementation of a Widget. Most widgets should extend this class
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractWidget<T extends AbstractWidgetData> implements
		Comparable<AbstractWidget<T>> {

	/** The listeners to this widget */
	private ArrayList<MBeanInfoWidgetListener> listners;
	/** The data used to construct this widget */
	protected T data;

	/**
	 * The constructor
	 * 
	 * @param data
	 *            The data that is used to construct this widget
	 */
	public AbstractWidget(T data) {
		this.data = data;
		listners = new ArrayList<MBeanInfoWidgetListener>();
	}

	/**
	 * @return 'form element' name for this widget
	 */
	public String getElementName() {
		return data.getName();
	}

	/**
	 * Get the widget data associated with this widget
	 * 
	 * @return
	 */
	public T getWidgetData() {
		return data;
	}

	/**
	 * Create the label for this widget
	 * 
	 * @param parent
	 */
	public void createLabel(Composite parent) {
		Label label = new Label(parent, SWT.None);
		label.setText(data.getDisplayName());
	}

	/**
	 * Add a listener to this widget
	 * 
	 * @param listener
	 */
	public void addListener(MBeanInfoWidgetListener listener) {
		this.listners.add(listener);

	}

	/**
	 * Remove a listner from this widget
	 * 
	 * @param listener
	 */
	public void removeListener(MBeanInfoWidgetListener listener) {
		this.listners.remove(listener);

	}

	/**
	 * Helper method for subclasses to use. Notify listeners of data changed
	 * 
	 * @param data
	 */
	protected void notifyListenersDataChanged(String data) {
		for (MBeanInfoWidgetListener l : listners) {
			l.dataChanged(this.data.getName(), data);
		}

	}

	/**
	 * Helper method for subclasses to use. Notify listeners of key released
	 */
	protected void notifyListenersKeyReleased() {
		for (MBeanInfoWidgetListener l : listners) {
			l.keyReleased(this.data.getName());
		}
	}

	/**
	 * Helper method for sublcasses to user. Notify listeners when they change
	 * from dirty to clean
	 */
	protected void notifyListenersClean() {
		for (MBeanInfoWidgetListener l : listners) {
			l.clean(this.data.getName());
		}
	}

	/**
	 * Create the control for this widget
	 * 
	 * @param parent
	 */
	public abstract void createControl(Composite parent);

	/**
	 * Disallow users from editing this widget
	 */
	public abstract void disable();

	/**
	 * Allow a user to edit this widget (as long as it is editable)
	 */
	public abstract void enable();

	/**
	 * Get the current value of this widget
	 * 
	 * @return The value as a string, null if there was a problem
	 */
	public abstract Attribute getAttribute();

	/**
	 * Set the value for this widget
	 * 
	 * @param value
	 *            The new value
	 * @return A string if there was a problem setting the value (such as a
	 *         NumberFormatException), null if it was set successfully
	 */
	public abstract String setValue(Attribute value);

	/**
	 * Validate the current value of the widget in some way
	 * 
	 * @return null if the value is valid, an error message if it is not
	 */
	public abstract String validate();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AbstractWidget<T> o) {
		if (o.data.getOrder() == -1) {
			return 1;
		}
		return data.getOrder().compareTo(o.data.getOrder());
	}
}

package org.rifidi.edge.client.mbean.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.openmbean.OpenMBeanAttributeInfo;

import org.eclipse.swt.widgets.Composite;
import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractWidget;

/**
 * A Form is the container elements for "form elements". All Forms should extend
 * this class.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractMBeanInfoComposite implements
		MBeanInfoWidgetListener {

	/** The MBeanAttributeInfo that describes this form */
	protected MBeanInfo formRoot;
	/** The collection of Widgets that belong to this form */
	protected ArrayList<AbstractWidget<?>> widgets;
	/** The listeners who are listening to changes to widgets */
	private ArrayList<MBeanInfoWidgetListener> listeners;
	/** The categories to filter on */
	protected Set<String> categories;
	/** True if we should regard the categories as include */
	protected boolean includeExcelude;

	public AbstractMBeanInfoComposite(MBeanInfo info, Set<String> categories,
			boolean includeExclude) {
		this.formRoot = info;
		this.categories = categories;
		this.includeExcelude = includeExclude;
		this.widgets = new ArrayList<AbstractWidget<?>>();
	}

	/**
	 * Create the controls in a form-specific way. Most of the time, this will
	 * involve creating a composite and adding the widgets to that composite.
	 * 
	 * This method should add widgets to the 'widgets' list as it creates them.
	 * In addition, it should register itself as a MBeanInfoWidgetListener for
	 * each of the widgets
	 * 
	 * @param parent
	 *            The parent Composite
	 */
	public abstract void createControls(Composite parent);

	/**
	 * Use this method to display an error on the form
	 * 
	 * @param widgetName
	 *            The name of the widget that has an error
	 * @param message
	 *            The error message
	 */
	public abstract void setError(String widgetName, String message);

	/**
	 * Use this method to remove a displayed error message
	 * 
	 * @param widgetName
	 *            The name of the widget
	 */
	public abstract void unsetError(String widgetName);

	/**
	 * 
	 * @return A list of MBeanAttributeInfos that are filtered according to the
	 *         filterSet
	 */
	protected Set<MBeanAttributeInfo> filterAttributes() {
		Set<MBeanAttributeInfo> attributesToDraw = new HashSet<MBeanAttributeInfo>();

		for (MBeanAttributeInfo attrInfo : this.formRoot.getAttributes()) {
			if (attrInfo instanceof OpenMBeanAttributeInfo) {
				String category = (String) attrInfo.getDescriptor()
						.getFieldValue("org.rifidi.edge.category");
				if (includeExcelude) {
					if (this.categories.contains(category)) {
						attributesToDraw.add(attrInfo);
					}
				} else {
					if (!this.categories.contains(category)) {
						attributesToDraw.add(attrInfo);
					}
				}
			}
		}
		return attributesToDraw;
	}

	/**
	 * Enable all the widgets in this form. This will allow the user to change
	 * the value of widgets
	 */
	public void enable() {
		for (AbstractWidget<?> w : widgets) {
			w.enable();
		}
	}

	/**
	 * Disable all the widgets in this form. This will disallow the user from
	 * changing the value of widgets
	 */
	public void disable() {
		for (AbstractWidget<?> w : widgets) {
			w.disable();
		}
	}

	/**
	 * This method validates the inputs in the form. It can be used in
	 * conjunction with the keyReleased() method in the DynamicSWTWidgetListner
	 * to provide validation as the user types
	 * 
	 * @return null if all widgets contain valid data. Otherwise return an error
	 *         message
	 */
	public String validate() {
		for (AbstractWidget<?> widget : widgets) {
			String s = widget.validate();
			if (s != null) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Gets the value of a widget associated with this form
	 * 
	 * @param widgetName
	 *            The name of the widget to get the value of
	 * @return The value as as string, null if the widget can't be found
	 */
	public Attribute getAttribute(String widgetName) {
		for (AbstractWidget<?> widget : widgets) {
			if (widget.getElementName().equalsIgnoreCase(widgetName)) {
				return widget.getAttribute();
			}
		}
		return null;
	}

	/**
	 * Set the value of a widget associated with this form
	 * 
	 * @param widgetName
	 *            The name of the widget in this form
	 * @param value
	 *            The new value
	 * @return null if the set was successful. Return a reason otherwise
	 */
	public String setValue(String widgetName, String value) {
		for (AbstractWidget<?> widget : widgets) {
			if (widget.getElementName().equalsIgnoreCase(widgetName)) {
				return widget.setValue(value);
			}
		}
		return "Form does not contain widget with name " + widgetName;
	}

	/**
	 * Add a MBeanInfoWidgetListener to this form
	 * 
	 * @param listener
	 *            The listner to add
	 */
	public void addListner(MBeanInfoWidgetListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Remove a MBeanInfoWidgetListener from this form
	 * 
	 * @param listener
	 *            The listner to remove
	 */
	public void removeListner(MBeanInfoWidgetListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * This method returns a map where the key is the widget names, and the
	 * values are the values of the widgets
	 * 
	 * @return
	 */
	public AttributeList getAttributes() {
		AttributeList list = new AttributeList();
		for (AbstractWidget<?> widget : widgets) {
			list.add(widget.getAttribute());
		}
		return list;
	}

	/**
	 * The form should listen to each of its children widgets for changes, and
	 * notify listeners to this form
	 */
	@Override
	public void dataChanged(String newData) {
		for (MBeanInfoWidgetListener l : listeners) {
			l.dataChanged(newData);
		}
	}

	/**
	 * The form should listen to each of its children widgets for changes, and
	 * notify listeners to this form
	 */
	@Override
	public void keyReleased() {
		for (MBeanInfoWidgetListener l : listeners) {
			l.keyReleased();
		}

	}

}

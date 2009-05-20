package org.rifidi.edge.client.mbean.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.openmbean.OpenMBeanAttributeInfo;

import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractWidget;

/**
 * A Form is the container elements for "form elements". All Forms should extend
 * this class.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class AbstractMBeanInfoComposite implements
		MBeanInfoWidgetListener {

	/** The MBeanAttributeInfo that describes this form */
	protected MBeanInfo formRoot;
	/** The collection of Widgets that belong to this form */
	private Map<String, AbstractWidget<?>> widgets;
	/** The listeners who are listening to changes to widgets */
	private ArrayList<MBeanInfoWidgetListener> listeners;
	/** The categories to filter on */
	protected Set<String> categories;
	/** True if we should regard the categories as include */
	protected boolean includeExcelude;

	/**
	 * Constructor.
	 * 
	 * @param info
	 *            The MBeanInfo that has the description of the attributes to
	 *            display in this form
	 * @param categories
	 *            The categories to filter on
	 * @param includeExclude
	 *            if true, include only the listed categories. If false, exclude
	 *            the listed categories
	 */
	public AbstractMBeanInfoComposite(MBeanInfo info, Set<String> categories,
			boolean includeExclude) {
		this.formRoot = info;
		this.categories = categories;
		this.includeExcelude = includeExclude;
		this.widgets = new HashMap<String, AbstractWidget<?>>();
		this.listeners = new ArrayList<MBeanInfoWidgetListener>();
	}

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
	 * Add a widget to the hashmap
	 * 
	 * @param widget
	 */
	protected void addWidget(AbstractWidget<?> widget) {
		widgets.put(widget.getElementName(), widget);
		widget.addListener(this);
	}

	/**
	 * Return a list of widgets which have been sorted according to their order
	 * variable
	 * 
	 * @return A sorted list of widgets
	 */
	protected List<AbstractWidget<?>> getSortedListOfWidgets() {
		List<AbstractWidget<?>> list = new ArrayList<AbstractWidget<?>>(widgets
				.values());
		Collections.sort(list);
		return list;
	}

	/**
	 * Get the widget with the specified name
	 * 
	 * @param widgetName
	 *            The name of the widget to get
	 * @return
	 */
	protected AbstractWidget<?> getWidget(String widgetName) {
		return widgets.get(widgetName);
	}

	/**
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
		for (AbstractWidget<?> w : widgets.values()) {
			w.enable();
		}
	}

	/**
	 * Disable all the widgets in this form. This will disallow the user from
	 * changing the value of widgets
	 */
	public void disable() {
		for (AbstractWidget<?> w : widgets.values()) {
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
		for (AbstractWidget<?> widget : widgets.values()) {
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
		AbstractWidget<?> widget = widgets.get(widgetName);
		if (widget != null) {
			return widget.getAttribute();
		}

		return null;
	}

	/**
	 * Set the value of a widget associated with this form
	 * 
	 * @param attribute
	 *            - the new attribute to set
	 * @return null if the set was successful. Return a reason otherwise
	 */
	public String setValue(Attribute attribute) {
		AbstractWidget<?> widget = widgets.get(attribute.getName());
		if (widget != null) {
			return widget.setValue(attribute);
		}
		return "Form does not contain widget with name " + attribute.getName();
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
		for (AbstractWidget<?> widget : widgets.values()) {
			list.add(widget.getAttribute());
		}
		return list;
	}

	/**
	 * The form should listen to each of its children widgets for changes, and
	 * notify listeners to this form
	 */
	@Override
	public void dataChanged(String widgetName, String newData) {
		for (MBeanInfoWidgetListener l : listeners) {
			l.dataChanged(widgetName, newData);
		}
	}

	/**
	 * The form should listen to each of its children widgets for changes, and
	 * notify listeners to this form
	 */
	@Override
	public void keyReleased(String widgetName) {
		for (MBeanInfoWidgetListener l : listeners) {
			l.keyReleased(widgetName);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.MBeanInfoWidgetListener#clean(java.lang
	 * .String)
	 */
	@Override
	public void clean(String widgetName) {
		for (MBeanInfoWidgetListener l : listeners) {
			l.clean(widgetName);
		}
	}

	/**
	 * Remove listeners and clean up.
	 */
	public void dispose() {
		for (AbstractWidget<?> w : this.widgets.values()) {
			w.removeListener(this);
			w.dispose();
		}
		this.widgets.clear();
	}

}

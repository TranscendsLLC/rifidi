package org.rifidi.dynamicswtforms.ui.widgets;

import org.eclipse.swt.widgets.Composite;
import org.jdom.Element;
import org.rifidi.dynamicswtforms.ui.widgets.listeners.DynamicSWTWidgetListener;

/**
 * A DynamicSWTFromWidget corresponds to "form element" in a dynamicSWTForm.
 * Concrete widgets should not implement this interface and should implement
 * AbstractWidget instead
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface DynamicSWTFormWidget {

	/**
	 * Create the control for this widget
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent);

	/**
	 * Create the label for this widget
	 * 
	 * @param parent
	 */
	public void createLabel(Composite parent);

	/**
	 * @return 'form element' name for this widget
	 */
	public String getElementName();

	/**
	 * Allow a user to edit this widget (as long as it is editable)
	 */
	public void enable();

	/**
	 * Disallow users from editing this widget
	 */
	public void disable();

	/**
	 * Set the value for this widget
	 * 
	 * @param value
	 *            The new value
	 * @return A string if there was a problem setting the value (such as a
	 *         NumberFormatException), null if it was set successfully
	 */
	public String setValue(String value);

	/**
	 * Get the current value of this widget
	 * 
	 * @return The value as a string, null if there was a problem
	 */
	public String getValue();

	/**
	 * Validate the current value of the widget in some way
	 * 
	 * @return null if the value is valid, an error message if it is not
	 */
	public String validate();

	/**
	 * Get the XML value of this widget
	 * 
	 * @return A JDOM Element
	 */
	public Element getXML();

	/**
	 * Add a listener to this widget
	 * 
	 * @param listener
	 */
	public void addListener(DynamicSWTWidgetListener listener);

	/**
	 * Remove a listner from this widget
	 * 
	 * @param listener
	 */
	public void removeListener(DynamicSWTWidgetListener listener);

}

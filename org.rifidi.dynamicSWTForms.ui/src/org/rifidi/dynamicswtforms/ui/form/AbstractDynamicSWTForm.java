package org.rifidi.dynamicswtforms.ui.form;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.widgets.Composite;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.rifidi.dynamicswtforms.ui.exceptions.DynamicSWTFormInvalidXMLException;
import org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget;
import org.rifidi.dynamicswtforms.ui.widgets.listeners.DynamicSWTWidgetListener;
import org.rifidi.dynamicswtforms.xml.constants.FormData;

/**
 * A Form is the container elements for "form elements". All Forms should extend
 * this class.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractDynamicSWTForm implements
		DynamicSWTWidgetListener {

	/**
	 * The XML that describes this form
	 */
	protected Element formRoot;

	/**
	 * The collection of Widgets that belong to this form
	 */
	protected ArrayList<DynamicSWTFormWidget> widgets;

	/**
	 * The listeners who are listening to changes to widgets
	 */
	private ArrayList<DynamicSWTWidgetListener> listeners;

	/**
	 * The name of this form
	 */
	private String name;

	/**
	 * Makes a new Form
	 * 
	 * @param xml
	 *            The XML that describes this form
	 * @throws DynamicSWTFormInvalidXMLException
	 */
	public AbstractDynamicSWTForm(String xml)
			throws DynamicSWTFormInvalidXMLException {
		SAXBuilder builder = new SAXBuilder();
		Reader reader = new StringReader(xml);
		try {
			init(builder.build(reader).getRootElement());
		} catch (JDOMException e) {
			throw new DynamicSWTFormInvalidXMLException(e);
		} catch (IOException e) {
			throw new DynamicSWTFormInvalidXMLException(e);
		}
	}

	/**
	 * Make a new form from an already parsed XML
	 * 
	 * @param formRoot
	 */
	public AbstractDynamicSWTForm(Element formRoot) {
		init(formRoot);
	}

	/**
	 * Initialize the various data structures in this object
	 * 
	 * @param formRoot
	 *            The XML that describes this form
	 */
	private void init(Element formRoot) {
		this.listeners = new ArrayList<DynamicSWTWidgetListener>();
		widgets = new ArrayList<DynamicSWTFormWidget>();
		this.formRoot = formRoot;
		this.name = formRoot.getAttributeValue(FormData.NAME.name());
	}

	/**
	 * Create the controls in a form-specific way. Most of the time, this will
	 * involve creating a composite and adding the widgets to that composite.
	 * 
	 * This method should add widgets to the 'widgets' list as it creates them.
	 * In addition, it should register itself as a DynamicSWTWidgetListener for
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
	 * Enable all the widgets in this form. This will allow the user to change
	 * the value of widgets
	 */
	public void enable() {
		for (DynamicSWTFormWidget w : widgets) {
			w.enable();
		}
	}

	/**
	 * Disable all the widgets in this form. This will disallow the user from
	 * changing the value of widgets
	 */
	public void disable() {
		for (DynamicSWTFormWidget w : widgets) {
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
		for (DynamicSWTFormWidget widget : widgets) {
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
	public String getValue(String widgetName) {
		for (DynamicSWTFormWidget widget : widgets) {
			if (widget.getElementName().equalsIgnoreCase(widgetName)) {
				return widget.getValue();
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
		for (DynamicSWTFormWidget widget : widgets) {
			if (widget.getElementName().equalsIgnoreCase(widgetName)) {
				return widget.setValue(value);
			}
		}
		return "Form does not contain widget with name " + widgetName;
	}

	/**
	 * Add a DynamicSWTWidgetListener to this form
	 * 
	 * @param listener
	 *            The listner to add
	 */
	public void addListner(DynamicSWTWidgetListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Remove a DynamicSWTWidgetListener from this form
	 * 
	 * @param listener
	 *            The listner to remove
	 */
	public void removeListner(DynamicSWTWidgetListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * This method returns a map where the key is the widget names, and the
	 * values are the values of the widgets
	 * 
	 * @return
	 */
	public HashMap<String, String> getWidgetNameValueMap() {
		HashMap<String, String> retVal = new HashMap<String, String>();
		for (DynamicSWTFormWidget widget : widgets) {
			retVal.put(widget.getElementName(), widget.getValue());
		}
		return retVal;
	}

	/**
	 * Gets the data from this form as an XML Element
	 * 
	 * @return a JDOM Element
	 */
	public Element getXML() {
		Element e = new Element(name);
		for (DynamicSWTFormWidget widget : widgets) {
			e.addContent(widget.getXML());
		}
		return e;
	}

	/**
	 * Gets the data from this form as an XML string
	 * 
	 * @param compact
	 *            if true the return string will not be pretty.
	 * @return An XML string
	 */
	public String getXMLAsString(boolean compact) {
		Element e = getXML();
		XMLOutputter outputter;
		if (compact) {
			outputter = new XMLOutputter(Format.getCompactFormat());
		} else {
			outputter = new XMLOutputter(Format.getPrettyFormat());
		}
		String s = outputter.outputString(e);
		return s;
	}

	/**
	 * The form should listen to each of its children widgets for changes, and
	 * notify listeners to this form
	 */
	@Override
	public void dataChanged(String newData) {
		for (DynamicSWTWidgetListener l : listeners) {
			l.dataChanged(newData);
		}
	}

	/**
	 * The form should listen to each of its children widgets for changes, and
	 * notify listeners to this form
	 */
	@Override
	public void keyReleased() {
		for (DynamicSWTWidgetListener l : listeners) {
			l.keyReleased();
		}

	}

}

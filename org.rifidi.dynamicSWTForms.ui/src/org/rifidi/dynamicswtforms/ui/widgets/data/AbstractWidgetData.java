package org.rifidi.dynamicswtforms.ui.widgets.data;

import org.jdom.Element;
import org.rifidi.dynamicswtforms.xml.constants.FormElementData;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;

/**
 * The base abstract class for the data used to construct widgets
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AbstractWidgetData {

	/**
	 * The JDOM element for this 'form element'
	 */
	protected Element element;

	/**
	 * Construct a new WidgetData object
	 * 
	 * @param element
	 *            The JDOM element for the 'form element'
	 */
	public AbstractWidgetData(Element element) {
		this.element = element;
	}

	/**
	 * @return The name of this 'form element'
	 */
	public String getName() {
		return element.getChildText(FormElementData.ELEMENT_NAME.name());
	}

	/**
	 * @return The display name of this 'form element'
	 */
	public String getDisplayName() {
		return element.getChildText(FormElementData.DISPLAY_NAME.name());
	}

	/**
	 * @return the default value for this 'form element'
	 */
	public String getDefaultValue() {
		return element.getChildText(FormElementData.DEFAULT_VALUE.name());
	}

	/**
	 * @return true if this 'form element' should be allowed to be edited by the
	 *         user, false otherwise
	 */
	public boolean isEditable() {
		String editable = element.getChildText(FormElementData.EDITABLE.name());
		return editable.equalsIgnoreCase("true");
	}

	/**
	 * @return The type of this 'form element'.
	 * @see FormElementType
	 */
	public FormElementType getType() {
		return FormElementType.valueOf(element.getName());
	}

}

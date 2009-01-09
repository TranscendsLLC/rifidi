package org.rifidi.dynamicswtforms.ui.widgets.data;

import org.jdom.Element;
import org.rifidi.dynamicswtforms.xml.constants.FormElementData;

/**
 * This is the concrete class for data used to construct an String Widget
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class StringWidgetData extends AbstractWidgetData {

	public StringWidgetData(Element element) {
		super(element);
	}

	/**
	 * @return A regular expression for validating the input to this widget
	 */
	public String getRegex() {
		return element.getChildText(FormElementData.REGEX.name());
	}

}

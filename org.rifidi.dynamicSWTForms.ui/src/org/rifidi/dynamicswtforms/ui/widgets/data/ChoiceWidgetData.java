package org.rifidi.dynamicswtforms.ui.widgets.data;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.rifidi.dynamicswtforms.xml.constants.FormElementData;

/**
 * This is the concrete class for data used to construct a Choice Widget
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ChoiceWidgetData extends AbstractWidgetData {

	public ChoiceWidgetData(Element element) {
		super(element);
	}

	/**
	 * 
	 * @return A list of the possible choices for this widget
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> possibleChoices() {
		ArrayList<String> choices = new ArrayList<String>();

		List<Element> values = element.getChild(
				FormElementData.POSSIBLE_VALUES.name()).getChildren();
		for (Element e : values) {
			choices.add(e.getValue());
		}
		return choices;

	}

}

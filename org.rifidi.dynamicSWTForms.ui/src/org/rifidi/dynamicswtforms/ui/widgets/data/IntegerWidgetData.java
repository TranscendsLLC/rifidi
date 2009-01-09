package org.rifidi.dynamicswtforms.ui.widgets.data;

import org.jdom.Element;
import org.rifidi.dynamicswtforms.xml.constants.FormElementData;

/**
 * This is the concrete class for data used to construct an Integer Widget
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class IntegerWidgetData extends AbstractWidgetData {

	public IntegerWidgetData(Element element) {
		super(element);
	}
	
	/**
	 * @return The maximum value of this widget
	 */
	public int maxValue(){
		return Integer.parseInt(element.getChildText(FormElementData.MAX.name()));
	}
	
	/**
	 * @return The minimum value for this widget
	 */
	public int minValue(){
		return Integer.parseInt(element.getChildText(FormElementData.MIN.name()));
	}

}

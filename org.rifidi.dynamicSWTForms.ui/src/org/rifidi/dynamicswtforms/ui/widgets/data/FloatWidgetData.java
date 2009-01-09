package org.rifidi.dynamicswtforms.ui.widgets.data;

import org.jdom.Element;
import org.rifidi.dynamicswtforms.xml.constants.FormElementData;

/**
 * This is the concrete class for data used to construct a Float Widget
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class FloatWidgetData extends AbstractWidgetData {

	public FloatWidgetData(Element element) {
		super(element);
	}

	/**
	 * @return The maximum value of this widget
	 */
	public Float maxValue() {
		return Float.valueOf(element.getChildText(FormElementData.MAX.name()));
	}

	/**
	 * @return The minimum value for this widget
	 */
	public Float minValue() {
		return Float.valueOf(element.getChildText(FormElementData.MIN.name()));
	}

	/**
	 * @return The number of decimal places to display
	 */
	public int getNumDecimalPlaces() {
		return Integer.valueOf(element
				.getChildText(FormElementData.DECIMAL_PLACES.name()));
	}

}

package org.rifidi.edge.client.mbean.ui.widgets.data;
//TODO: Comments
import java.util.ArrayList;

import javax.management.JMX;
import javax.management.MBeanAttributeInfo;

/**
 * This is the concrete class for data used to construct a Choice Widget
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ChoiceWidgetData extends AbstractWidgetData {

	public ChoiceWidgetData(MBeanAttributeInfo element) {
		super(element);
	}

	/**
	 * TODO: for now this returns an empty list. Figure this out later
	 * 
	 * 
	 * @return A list of the possible choices for this widget
	 */
	public ArrayList<String> possibleChoices() {
		ArrayList<String> choices = new ArrayList<String>();
		return choices;

	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.client.mbean.ui.widgets.data.AbstractWidgetData#getDefaultValue()
	 */
	@Override
	public String getDefaultValue() {
		Object defVal = element.getDescriptor().getFieldValue(JMX.DEFAULT_VALUE_FIELD);
		if(defVal==null){
			return "";
		}else{
			return (String)defVal;
		}
	}

}

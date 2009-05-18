
package org.rifidi.edge.client.mbean.ui.widgets.data;

import javax.management.JMX;
import javax.management.MBeanAttributeInfo;

/**
 * This is the concrete class for data used to construct an Integer Widget
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class IntegerWidgetData extends AbstractWidgetData {

	/**
	 * Constructor.  
	 * 
	 * @param element
	 */
	public IntegerWidgetData(MBeanAttributeInfo element) {
		super(element);
	}
	
	/**
	 * TODO: Method level comment.  
	 * 
	 * @return The maximum value of this widget
	 */
	public int maxValue(){
		Object o = element.getDescriptor().getFieldValue(JMX.MAX_VALUE_FIELD);
		if(o == null ){
			return Integer.MAX_VALUE;
		}else{
			return (Integer)o;
		}
	}
	
	/**
	 * TODO: Method level comment.  
	 * 
	 * @return The minimum value for this widget
	 */
	public int minValue(){
		Object o = element.getDescriptor().getFieldValue(JMX.MIN_VALUE_FIELD);
		if(o == null ){
			return Integer.MIN_VALUE;
		}else{
			return (Integer)o;
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.client.mbean.ui.widgets.data.AbstractWidgetData#getDefaultValue()
	 */
	@Override
	public Integer getDefaultValue() {
		Object defVal = element.getDescriptor().getFieldValue(JMX.DEFAULT_VALUE_FIELD);
		if(defVal==null){
			return 0;
		}else{
			return (Integer)defVal;
		}
	}
	
	

}

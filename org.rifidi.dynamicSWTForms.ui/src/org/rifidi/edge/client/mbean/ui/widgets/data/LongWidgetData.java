/**
 * 
 */
package org.rifidi.edge.client.mbean.ui.widgets.data;

import javax.management.JMX;
import javax.management.MBeanAttributeInfo;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 *
 */
public class LongWidgetData extends AbstractWidgetData {

	/**
	 * @param element
	 */
	public LongWidgetData(MBeanAttributeInfo element) {
		super(element);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.client.mbean.ui.widgets.data.AbstractWidgetData#getDefaultValue()
	 */
	@Override
	public Long getDefaultValue() {
		Object o = element.getDescriptor().getFieldValue(JMX.DEFAULT_VALUE_FIELD);
		if(o ==null){
			return 0l;
		}else{
			return (Long)o;
		}
	}

}

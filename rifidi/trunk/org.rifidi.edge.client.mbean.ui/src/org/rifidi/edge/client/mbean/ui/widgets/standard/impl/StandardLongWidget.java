/**
 * 
 */
package org.rifidi.edge.client.mbean.ui.widgets.standard.impl;

import javax.management.Attribute;

import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractNumberWidget;
import org.rifidi.edge.client.mbean.ui.widgets.data.LongWidgetData;

/**
 * @author kyle
 *
 */
public class StandardLongWidget<T extends LongWidgetData> extends AbstractNumberWidget<T> {

	/**
	 * @param data
	 */
	public StandardLongWidget(T data) {
		super(data);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractNumberWidget#buildCustomSpinner()
	 */
	@Override
	protected void buildCustomSpinner() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#setValue(java.lang.Object)
	 */
	@Override
	public String setValue(Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attribute getAttribute() {
		return new Attribute(getElementName(), Long.parseLong(spinner.getText()));
	}

}

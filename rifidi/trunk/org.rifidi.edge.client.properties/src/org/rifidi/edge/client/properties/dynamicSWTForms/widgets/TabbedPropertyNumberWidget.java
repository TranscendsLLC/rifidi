/**
 * 
 */
package org.rifidi.edge.client.properties.dynamicSWTForms.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.rifidi.dynamicswtforms.ui.widgets.data.AbstractWidgetData;
import org.rifidi.dynamicswtforms.ui.widgets.standard.impl.NumberWidget;

/**
 * A number widget that uses the TabbedPropertySheetWidgetFactory
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TabbedPropertyNumberWidget extends NumberWidget {

	private TabbedPropertySheetWidgetFactory factory;

	/**
	 * @param data
	 */
	public TabbedPropertyNumberWidget(AbstractWidgetData data,
			TabbedPropertySheetWidgetFactory factory) {
		super(data);
		this.factory = factory;
	}

	@Override
	public void createLabel(Composite parent) {
		factory.createLabel(parent, data.getDisplayName());
	}

}

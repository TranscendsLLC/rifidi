
package org.rifidi.edge.client.sal.properties.mbeanwidgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.rifidi.edge.client.mbean.ui.widgets.data.StringWidgetData;
import org.rifidi.edge.client.mbean.ui.widgets.standard.impl.StringWidget;

/**
 * 
 * 
 * @author kyle
 */
public class FlatFormStringMbeanWidget<T extends StringWidgetData> extends
		StringWidget<T> {

	private TabbedPropertySheetWidgetFactory factory;

	/**
	 * Constructor.  
	 * 
	 * @param data
	 * @param factory
	 */
	public FlatFormStringMbeanWidget(T data,
			TabbedPropertySheetWidgetFactory factory) {
		super(data);
		this.factory = factory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.widgets.standard.impl.StringWidget#createText
	 * (org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createText(Composite parent) {
		this.text = factory.createText(parent, "");
		this.text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
	}
}

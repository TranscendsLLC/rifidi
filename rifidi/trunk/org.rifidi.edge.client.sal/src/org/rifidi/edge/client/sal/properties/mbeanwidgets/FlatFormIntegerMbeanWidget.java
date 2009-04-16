/**
 * 
 */
package org.rifidi.edge.client.sal.properties.mbeanwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.rifidi.edge.client.mbean.ui.widgets.data.IntegerWidgetData;
import org.rifidi.edge.client.mbean.ui.widgets.standard.impl.StandardIntegerWidget;

/**
 * @author kyle
 * 
 */
public class FlatFormIntegerMbeanWidget<T extends IntegerWidgetData> extends
		StandardIntegerWidget<T> {

	private TabbedPropertySheetWidgetFactory factory;

	/**
	 * Constructor
	 * 
	 * @param data
	 * 
	 */
	public FlatFormIntegerMbeanWidget(T data,
			TabbedPropertySheetWidgetFactory factory) {
		super(data);
		this.factory = factory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.widgets.standard.impl.StandardIntegerWidget
	 * #createSpinner(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createSpinner(Composite parent) {
		spinner = new Spinner(parent, SWT.BORDER);
		factory.adapt(spinner, true, true);
		spinner.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
	}

}

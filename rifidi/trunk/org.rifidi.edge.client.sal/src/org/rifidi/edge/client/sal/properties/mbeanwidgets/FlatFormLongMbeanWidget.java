package org.rifidi.edge.client.sal.properties.mbeanwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.rifidi.edge.client.mbean.ui.widgets.data.LongWidgetData;
import org.rifidi.edge.client.mbean.ui.widgets.standard.impl.StandardLongWidget;

/**
 * A widget for drawing a Long widget using the forms UI
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class FlatFormLongMbeanWidget<T extends LongWidgetData> extends
		StandardLongWidget<T> {

	/** The factory for producing forms UI widgets */
	private TabbedPropertySheetWidgetFactory factory;

	/**
	 * Constructor.
	 * 
	 * @param data
	 */
	public FlatFormLongMbeanWidget(T data,
			TabbedPropertySheetWidgetFactory factory) {
		super(data);
		this.factory = factory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.widgets.standard.impl.StandardLongWidget
	 * #createSpinner(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createSpinner(Composite parent) {
		spinner = new Spinner(parent, SWT.BORDER);
		factory.adapt(spinner, true, true);
		spinner.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
	}

}

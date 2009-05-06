/**
 * 
 */
package org.rifidi.edge.client.sal.properties.mbeanwidgets;
//TODO: Comments
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.rifidi.edge.client.mbean.ui.widgets.data.LongWidgetData;
import org.rifidi.edge.client.mbean.ui.widgets.standard.impl.StandardLongWidget;

/**
 * @author kyle
 * 
 */
public class FlatFormLongMbeanWidget<T extends LongWidgetData> extends
		StandardLongWidget<T> {

	private TabbedPropertySheetWidgetFactory factory;

	/**
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

/*
 * FlatFormIntegerMbeanWidget.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
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
 * A Widget for drawing an integer control using the FormsUI
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class FlatFormIntegerMbeanWidget<T extends IntegerWidgetData> extends
		StandardIntegerWidget<T> {

	/** The factory for creating formsUI widgets */
	private TabbedPropertySheetWidgetFactory factory;

	/**
	 * Constructor
	 * 
	 * @param data
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

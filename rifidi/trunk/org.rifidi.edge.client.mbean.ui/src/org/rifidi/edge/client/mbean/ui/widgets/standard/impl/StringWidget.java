package org.rifidi.edge.client.mbean.ui.widgets.standard.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractStringWidget;
import org.rifidi.edge.client.mbean.ui.widgets.data.StringWidgetData;

/**
 * A default implementation of a String Widget, which uses a Text control for
 * Strings.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class StringWidget<T extends StringWidgetData> extends AbstractStringWidget<T> {

	/**
	 * Contstruct a new StringWidget
	 * 
	 * @param data
	 *            A StringWidgetData
	 */
	public StringWidget(T data) {
		super(data);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractStringWidget#createText(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createText(Composite parent) {
		// Create a text control that will wrap if the width of the text exceeds
		// 150 pixels
		text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 150;
		text.setLayoutData(gridData);
		
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractStringWidget#initializeText()
	 */
	@Override
	protected void initializeText() {
		text.setText(data.getDefaultValue());
	}
	
	

}

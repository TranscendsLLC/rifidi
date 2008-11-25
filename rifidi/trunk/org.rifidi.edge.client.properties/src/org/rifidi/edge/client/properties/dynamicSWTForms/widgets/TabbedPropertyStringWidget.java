/**
 * 
 */
package org.rifidi.edge.client.properties.dynamicSWTForms.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.rifidi.dynamicswtforms.ui.widgets.abstractwidgets.AbstractStringWidget;
import org.rifidi.dynamicswtforms.ui.widgets.data.AbstractWidgetData;

/**
 * A string widget that uses the TabbedPropertySheetWidgetFactory
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TabbedPropertyStringWidget extends AbstractStringWidget {

	private TabbedPropertySheetWidgetFactory factory;
	private boolean dirty;

	public TabbedPropertyStringWidget(AbstractWidgetData data,
			TabbedPropertySheetWidgetFactory factory) {
		super(data);
		this.factory = factory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#createControl
	 * (org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		text = factory.createText(parent, "", SWT.MULTI | SWT.WRAP);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 250;
		text.setLayoutData(gridData);
		text.setEnabled(data.isEditable());
		text.setText(data.getDefaultValue());

		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				dirty = true;

			}

		});

		text.addVerifyListener(new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent e) {
				if (e.character == SWT.CR) {
					if (dirty == true) {
						dirty = false;
						notifyListenersDataChanged(text.getText());
					}
					e.doit = false;
				}

			}

		});

		text.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character != SWT.CR) {
					notifyListenersKeyReleased();
				}

			}
		});

	}

	@Override
	public void createLabel(Composite parent) {
		factory.createLabel(parent, data.getDisplayName());
	}

}

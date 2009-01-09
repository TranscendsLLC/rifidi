package org.rifidi.dynamicswtforms.ui.widgets.standard.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.rifidi.dynamicswtforms.ui.widgets.abstractwidgets.AbstractStringWidget;
import org.rifidi.dynamicswtforms.ui.widgets.data.AbstractWidgetData;

/**
 * A default implementation of a String Widget, which uses a Text control for
 * Strings.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class StringWidget extends AbstractStringWidget {

	/**
	 * Set to true if the user has modified the data since the last time the
	 * listeners have been notified
	 */
	private boolean dirty;

	/**
	 * Contstruct a new StringWidget
	 * 
	 * @param data
	 *            A StringWidgetData
	 */
	public StringWidget(AbstractWidgetData data) {
		super(data);
	}

	/**
	 * {@link AbstractStringWidget#createControl(Composite)}
	 */
	@Override
	public void createControl(Composite parent) {
		// Create a text control that will wrap if the width of the text exceeds
		// 150 pixels
		text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 150;
		text.setLayoutData(gridData);
		text.setEditable(data.isEditable());
		text.setText(data.getDefaultValue());

		// Set dirty=true if the user modifies the text
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				dirty = true;

			}

		});

		// take control of verify listeners so when the user hits return, a
		// newline is not created on the Text control
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

		// notify listeners of a user typing a key
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

}

/*
 * StandardChoiceWidget.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.mbean.ui.widgets.standard.impl;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractChoiceWidget;
import org.rifidi.edge.client.mbean.ui.widgets.data.ChoiceWidgetData;

/**
 * A default implementation of the Choice Widget, which uses a combo control to
 * allow users to pick between choices
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class StandardChoiceWidget<T extends ChoiceWidgetData> extends AbstractChoiceWidget<T> {

	/**
	 * Set to true if the user has modified the data since the last time the
	 * listeners have been notified
	 */
	private boolean dirty;

	/**
	 * Construct a new StandardChoiceWidget
	 * 
	 * @param data
	 *            ChoiceWidgetData
	 */
	public StandardChoiceWidget(T data) {
		super(data);
	}

	/**
	 * link {@link AbstractChoiceWidget#createControl(Composite)}
	 */
	@Override
	public void createControl(Composite parent) {
		// create the combo
		combo = new Combo(parent, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.minimumWidth = 150;
		combo.setLayoutData(gridData);

		List<String> choices = ((ChoiceWidgetData) data).possibleChoices();
		for (String s : choices) {
			combo.add(s);
		}
		combo.select(choices.indexOf(data.getDefaultValue()));
		combo.setEnabled(data.isEditable());

		// if the selection changes, change dirty to true
		combo.addSelectionListener(new SelectionListener() {
			@Override
			// This method is called when enter is pressed
			public void widgetDefaultSelected(SelectionEvent e) {
				dirty = true;
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				dirty = true;
			}
		});

		// notify the listeners if enter is pressed and dirty is true
		combo.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					if (dirty == true) {
						dirty = false;
						notifyListenersDataChanged(combo.getItem(combo
								.getSelectionIndex()));
					}
				} else
					notifyListenersKeyReleased();
			}
		});
	}
}

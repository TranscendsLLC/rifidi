/* 
 * CreateBottomComposite.java
 *  Created:	Aug 5, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.site.ui.content;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class CreateBottomComposite {

	/**
	 * 
	 */
	private static CreateBottomComposite instance = new CreateBottomComposite();
	
	/**
	 * @return the instance
	 */
	public static CreateBottomComposite getInstance() {
		return instance;
	}

	/**
	 * 
	 */
	private CreateBottomComposite() {
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public Composite createBottomComposite(Composite parent) {
		
		// === Create the new composite ===
		Composite newComp = new Composite(parent, SWT.NONE);
		newComp.setLayout(new RowLayout());
		
		// === Add and Remove Button ===
		Button executeCommandButton = new Button(newComp, SWT.PUSH);
		executeCommandButton.setText("&Previous");
		executeCommandButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
			}
		});

		Button executePropertyButton = new Button(newComp, SWT.PUSH);
		executePropertyButton.setText("&Next");
		executePropertyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
			}
		});

		Button stopCurCommandButton = new Button(newComp, SWT.PUSH);
		stopCurCommandButton.setText("Cancel");
		stopCurCommandButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		
		return newComp;
	}
}

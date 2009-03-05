/* 
 *  Test.java
 *  Created:	Feb 18, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.ui
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.ui.views;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 *
 */
public class Test extends Composite {

	private Group group = null;
	private Label label1 = null;
	private Text text = null;
	
	private Text text1 = null;
	private Label label = null;
	private Text text11 = null;
	private Button button = null;
	private Group group1 = null;
	private Button button1 = null;
	private Text text12 = null;
	private Button button2 = null;
	private Text text13 = null;
	private Group group2 = null;
	public Test(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		createGroup();
		createGroup1();
		setSize(new Point(736, 1015));
		setLayout(new GridLayout(2,true));
		
		
		
	}

	/**
	 * This method initializes group	
	 *
	 */
	private void createGroup() {
		group = new Group(this, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText("Set Endpoints");
		label1 = new Label(group, SWT.NONE);
		label1.setText("Filter + Collection");
		text = new Text(group, SWT.BORDER);
		label = new Label(group, SWT.NONE);
		label.setText("Logical Reader");
		text11 = new Text(group, SWT.BORDER);
	}

	/**
	 * This method initializes group1	
	 *
	 */
	private void createGroup1() {
		group1 = new Group(this, SWT.NONE);
		group1.setLayout(new GridLayout());
		group1.setText("Test");
		button1 = new Button(group1, SWT.NONE);
		button1.setText("F+C");
		text12 = new Text(group1, SWT.BORDER);
		text12.setText("getVendorVersion");
		button2 = new Button(group1, SWT.NONE);
		button2.setText("Logical Reader");
		text13 = new Text(group1, SWT.BORDER);
		text13.setText("getVendorVersion");
	}

	/**
	 * This method initializes group2	
	 *
	 */
	private void createGroup2() {
		group2 = new Group(this, SWT.NONE);
		group2.setLayout(new GridLayout());
	}

}  //  @jve:decl-index=0:visual-constraint="324,21"

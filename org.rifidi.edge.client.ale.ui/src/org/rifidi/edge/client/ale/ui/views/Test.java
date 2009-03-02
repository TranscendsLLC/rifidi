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
	private Label label = null;
	private Button radioButton = null;
	private Button radioButton1 = null;
	private Label label2 = null;
	private Text text1 = null;
	private Group group1 = null;
	private Label label3 = null;
	private Text text2 = null;
	private Label label4 = null;
	private Text text3 = null;
	private Label label5 = null;
	private Text text4 = null;
	private Group group2 = null;
	private Label label6 = null;
	private Text text5 = null;
	private Group group3 = null;
	private Group group4 = null;
	private Button radioButton2 = null;
	private Button radioButton3 = null;
	private Button radioButton4 = null;
	private Button radioButton5 = null;
	private Group group5 = null;
	private Group group6 = null;
	private Group group7 = null;
	private Button radioButton6 = null;
	private Button radioButton7 = null;
	private Button radioButton8 = null;
	private Button radioButton9 = null;
	private Group group8 = null;
	private Group group9 = null;
	private Button radioButton10 = null;
	private Button radioButton11 = null;
	private Button radioButton12 = null;
	private Button radioButton13 = null;
	private Group group10 = null;
	private Button radioButton14 = null;
	private Button radioButton15 = null;
	private Button button = null;

	public Test(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		createGroup();
		setSize(new Point(736, 1015));
		setLayout(new GridLayout(3,true));
		label2 = new Label(this, SWT.NONE);
		label2.setText("Logical Readers:");
		text1 = new Text(this, SWT.BORDER);
		createGroup1();
		createGroup2();
		createGroup5();
		button = new Button(this, SWT.NONE);
		button.setText("Define");
		button.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				System.out.println("widgetSelected()"); // TODO Auto-generated Event stub widgetSelected()
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});
	}

	/**
	 * This method initializes group	
	 *
	 */
	private void createGroup() {
		group = new Group(this, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText("Create new Event Cycle");
		label1 = new Label(group, SWT.NONE);
		label1.setText("Event Cycle Name:");
		text = new Text(group, SWT.BORDER);
		label = new Label(group, SWT.NONE);
		label.setText("Include Spec in Reports:");
		radioButton = new Button(group, SWT.RADIO);
		radioButton.setText("Yes");
		radioButton1 = new Button(group, SWT.RADIO);
		radioButton1.setText("No");
		radioButton1.setSelection(true);
	}

	/**
	 * This method initializes group1	
	 *
	 */
	private void createGroup1() {
		group1 = new Group(this, SWT.NONE);
		group1.setLayout(new GridLayout());
		group1.setText("Boundary");
		label3 = new Label(group1, SWT.NONE);
		label3.setText("Stable Set [ms] :");
		text2 = new Text(group1, SWT.BORDER);
		label4 = new Label(group1, SWT.NONE);
		label4.setText("Repeat After [ms] :");
		text3 = new Text(group1, SWT.BORDER);
		label5 = new Label(group1, SWT.NONE);
		label5.setText("Duration [ms] :");
		text4 = new Text(group1, SWT.BORDER);
	}

	/**
	 * This method initializes group2	
	 *
	 */
	private void createGroup2() {
		group2 = new Group(this, SWT.NONE);
		group2.setLayout(new GridLayout());
		group2.setText("Report Spec");
		label6 = new Label(group2, SWT.NONE);
		label6.setText("Name:");
		text5 = new Text(group2, SWT.BORDER);
		createGroup3();
		createGroup4();
	}

	/**
	 * This method initializes group3	
	 *
	 */
	private void createGroup3() {
		group3 = new Group(group2, SWT.NONE);
		group3.setLayout(new GridLayout());
		group3.setText("Report if Empty");
		radioButton2 = new Button(group3, SWT.RADIO);
		radioButton2.setText("Yes");
		radioButton3 = new Button(group3, SWT.RADIO);
		radioButton3.setText("No");
		radioButton3.setSelection(true);
	}

	/**
	 * This method initializes group4	
	 *
	 */
	private void createGroup4() {
		group4 = new Group(group2, SWT.NONE);
		group4.setLayout(new GridLayout());
		group4.setText("Report only on Change");
		radioButton4 = new Button(group4, SWT.RADIO);
		radioButton4.setText("Yes");
		radioButton5 = new Button(group4, SWT.RADIO);
		radioButton5.setText("No");
		radioButton5.setSelection(true);
	}

	/**
	 * This method initializes group5	
	 *
	 */
	private void createGroup5() {
		group5 = new Group(this, SWT.NONE);
		group5.setLayout(new GridLayout());
		createGroup6();
		group5.setText("Output Options");
		createGroup7();
		createGroup8();
		createGroup9();
		createGroup10();
	}

	/**
	 * This method initializes group6	
	 *
	 */
	private void createGroup6() {
		group6 = new Group(group5, SWT.NONE);
		group6.setLayout(new GridLayout());
		group6.setText("Include Tag");
		radioButton6 = new Button(group6, SWT.RADIO);
		radioButton6.setText("Yes");
		radioButton7 = new Button(group6, SWT.RADIO);
		radioButton7.setText("No");
		radioButton7.setSelection(true);
	}

	/**
	 * This method initializes group7	
	 *
	 */
	private void createGroup7() {
		group7 = new Group(group5, SWT.NONE);
		group7.setLayout(new GridLayout());
		group7.setText("Include EPC");
		radioButton8 = new Button(group7, SWT.RADIO);
		radioButton8.setText("Yes");
		radioButton9 = new Button(group7, SWT.RADIO);
		radioButton9.setText("No");
		radioButton9.setSelection(true);
	}

	/**
	 * This method initializes group8	
	 *
	 */
	private void createGroup8() {
		group8 = new Group(group5, SWT.NONE);
		group8.setLayout(new GridLayout());
		group8.setText("Include RAW Hex");
		radioButton10 = new Button(group8, SWT.RADIO);
		radioButton10.setText("Yes");
		radioButton11 = new Button(group8, SWT.RADIO);
		radioButton11.setText("No");
		radioButton11.setSelection(true);
	}

	/**
	 * This method initializes group9	
	 *
	 */
	private void createGroup9() {
		group9 = new Group(group5, SWT.NONE);
		group9.setLayout(new GridLayout());
		group9.setText("Include RAW Decimal");
		radioButton12 = new Button(group9, SWT.RADIO);
		radioButton12.setText("Yes");
		radioButton13 = new Button(group9, SWT.RADIO);
		radioButton13.setText("No");
		radioButton13.setSelection(true);
	}

	/**
	 * This method initializes group10	
	 *
	 */
	private void createGroup10() {
		group10 = new Group(group5, SWT.NONE);
		group10.setLayout(new GridLayout());
		group10.setText("Include Tag Count");
		radioButton14 = new Button(group10, SWT.RADIO);
		radioButton14.setText("Yes");
		radioButton15 = new Button(group10, SWT.RADIO);
		radioButton15.setText("No");
		radioButton15.setSelection(true);
	}

}  //  @jve:decl-index=0:visual-constraint="324,21"

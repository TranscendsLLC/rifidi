/* 
 *  EcSpecEditorTabFactory.java
 *  Created:	Apr 24, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.ecspecview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.ecspecview.views;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.rifidi.edge.client.ale.ecspecview.handlers.NewECSpec;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class EcSpecEditorTabFactory {
	/** the instance of this singleton */
	private static EcSpecEditorTabFactory instance = null;
	/** the form that hosts all the widgets */
	private ScrolledForm subscriberForm = null;
	/** the toolkit that creates all the forms/widgets */
	private FormToolkit subscriberToolkit = null;
	/** auxiliary variable for the widgets array */
	private int i=0;
	/** array of widgets to dispose when remove is clicked */
	private Widget[][] widgets=new Widget[3][10];
	
	
	
	
//	private Composite sectionClient = null;

	/** the constructor */
	private EcSpecEditorTabFactory() {

	}

	/**
	 * Gets the unique instance
	 * 
	 * @return EcSpecEditorTabFactory - the one instance
	 */
	public static EcSpecEditorTabFactory getInstance() {
		if (instance == null) {
			instance = new EcSpecEditorTabFactory();
		}
		return instance;
	}

	/**
	 * Creates a report tab (CTabItem) on the CTabFolder parent
	 * 
	 * @param parent
	 * @return CTabItem
	 */
	public CTabItem createReportTab(CTabFolder parent) {
		return null;

	}

	/**
	 * Creates a subscribers tab (CTabItem) on the CTabFolder parent
	 * 
	 * @param parent
	 * @return CTabItem
	 */
	public CTabItem createSubscribersTab(CTabFolder parent) {
		/** the CTabItem that will be returned later on */
		CTabItem subscriberTab = new CTabItem(parent, SWT.NONE);
		
		/** the toolkit that creates all the forms/widgets */
		subscriberToolkit = new FormToolkit(parent.getDisplay());
		subscriberForm = subscriberToolkit.createScrolledForm(parent);
		subscriberTab.setControl(subscriberForm);
		TableWrapLayout subscrLayout = new TableWrapLayout();
		subscrLayout.numColumns = 3;
		subscriberForm.getBody().setLayout(subscrLayout);
		subscriberForm.setText("Subscribers");
		/** button for saving the subscribers */
		Button btnSaveSubscribers = subscriberToolkit.createButton(
				subscriberForm.getBody(), "Save", SWT.PUSH);
		/** button for adding one more subscriber */
		Button btnAddSubscriber = subscriberToolkit.createButton(subscriberForm
				.getBody(), "Add", SWT.PUSH);
		TableWrapData twdd = new TableWrapData();
		twdd.colspan=2;
		btnAddSubscriber.setLayoutData(twdd);
		/** selection listener */
		btnAddSubscriber.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				addSubscriber(subscriberForm.getBody());
				subscriberForm.reflow(true);

			}

		});
		/** add one subscriber line for starters */
		addSubscriber(subscriberForm.getBody());
		/** never forget the reflow */
		subscriberForm.reflow(true);
		return subscriberTab;

	}

	private void addSubscriber(Composite sectionClient) {
		/** text for the subscriber string*/
//		Text txtSubscriberLine = null;
		/** combo to select the subscriber type */
		Combo cboSubType = new Combo(sectionClient, SWT.READ_ONLY);
		/** Create the data for the combo */
		String[] arSubscrData = new String[3];
		arSubscrData[0] = "TCP";
		arSubscrData[1] = "JMS";
		arSubscrData[2] = "Mail";
		/** set data */
		cboSubType.setItems(arSubscrData);
//		cboSubType.select(1);

		
		final Text txtSubscriberLine = subscriberToolkit.createText(sectionClient, "",
				SWT.BORDER);
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		txtSubscriberLine.setLayoutData(twd);
		cboSubType.addModifyListener(new ModifyListener() {


			@Override
			public void modifyText(ModifyEvent e) {
				Combo combo=(Combo) e.widget;
				if (combo.getText().equalsIgnoreCase("TCP")) {
					txtSubscriberLine.setText("http://");
				}
				if (combo.getText().equalsIgnoreCase("JMS")) {
					txtSubscriberLine.setText("jms://");
				}
				if (combo.getText().equalsIgnoreCase("Mail")){
					txtSubscriberLine.setText("mailto:");
				}
				
			}

		});
		Button btnRemove=subscriberToolkit.createButton(sectionClient, "remove", SWT.PUSH);
		final int temp = i;
		btnRemove.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				for(int j=0;j<3;j++){
					
					widgets[j][temp].dispose();
					subscriberForm.reflow(true);
				}
				
			}
			
		});
		widgets[0][i]=cboSubType;
		widgets[1][i]=txtSubscriberLine;
		widgets[2][i]=btnRemove;
		i++;
	}

}

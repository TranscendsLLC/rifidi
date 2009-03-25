/* 
 *  AleEditorView.java
 *  Created:	Mar 23, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.editor
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.editor.view;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AleEditorView extends ViewPart {

	private FormToolkit toolkit = null;
	private ScrolledForm form = null;
	private CTabFolder folder = null;
	private ArrayList<Composite> reportSpecComposites = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		folder = new CTabFolder(parent, SWT.BOTTOM);
		CTabItem ctiEdit = new CTabItem(folder, SWT.NONE, 0);
		form = toolkit.createScrolledForm(folder);
		ctiEdit.setControl(form);
		ctiEdit.setText("Edit");
		folder.setSelection(ctiEdit);
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		form.setText("NewEcSpec");
		layout.numColumns = 2;
		GridData gd = new GridData();
		gd.horizontalSpan = 2;

		Label lblSpecName = toolkit.createLabel(form.getBody(), "EcSpec Name:");
		Text txtSpecName = toolkit.createText(form.getBody(), "", SWT.BORDER);
		txtSpecName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lblInclSpecInRep = toolkit.createLabel(form.getBody(),
				"Include Spec in Reports?");
		Button btnInclSpecInRepYes = toolkit.createButton(form.getBody(),
				"Yes", SWT.RADIO);
		Button btnInclSpecInRepNo = toolkit.createButton(form.getBody(), "No",
				SWT.RADIO);

		gd = new GridData();
		gd.horizontalSpan = 2;
		btnInclSpecInRepYes.setLayoutData(gd);
		btnInclSpecInRepNo.setLayoutData(gd);

		Label lblLogicalReaders = toolkit.createLabel(form.getBody(),
				"Logical Readers");

		List lrList = new List(form.getBody(), SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		lrList.add("LogicalReader1");
		lrList.add("LogicalReader2");
		lrList.add("LogicalReader3");
		lrList.add("LogicalReader4");
		lrList.pack();

		Label lblBoundary = toolkit.createLabel(form.getBody(), "Boundary");
		gd = new GridData();
		gd.horizontalSpan = 2;
		lblBoundary.setLayoutData(gd);
		Label lblStableSet = toolkit.createLabel(form.getBody(),
				"Stable Set (ms):");
		Text txtStableSet = toolkit.createText(form.getBody(), "0", SWT.BORDER);
		txtStableSet.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label lblRepeatAfter = toolkit.createLabel(form.getBody(),
				"Repeat After (ms):");
		Text txtRepeatAfter = toolkit.createText(form.getBody(), "0",
				SWT.BORDER);
		txtRepeatAfter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label lblDuration = toolkit.createLabel(form.getBody(),
				"Duration (ms):");
		Text txtDuration = toolkit.createText(form.getBody(), "0", SWT.BORDER);
		txtDuration.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Section secRepSpec = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);
		secRepSpec.setLayout(new GridLayout());
		gd = new GridData(GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = 2;
		secRepSpec.setLayoutData(gd);
		TableWrapData td = new TableWrapData(TableWrapData.FILL);
		td.colspan = 2;
		secRepSpec.setLayoutData(td);
		secRepSpec.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		secRepSpec.setText("Section title");
		secRepSpec.setDescription("This is the description that goes "
				+ "below the title");
		Composite sectionClient = toolkit.createComposite(secRepSpec);
		sectionClient.setLayout(new GridLayout());
		secRepSpec.setClient(sectionClient);

		// ExpandableComposite ec = toolkit.createExpandableComposite(form
		// .getBody(), ExpandableComposite.TREE_NODE
		// | ExpandableComposite.CLIENT_INDENT);
		// ec.setText("Expandable Composite title");
		// String ctext = "We will now create a somewhat long text so that "
		// + "we can use it as content for the expandable composite. "
		// + "Expandable composite is used to hide or show the text using the "
		// + "toggle control";
		// Label client = toolkit.createLabel(ec, ctext, SWT.WRAP);
		// ec.setClient(client);
		// TableWrapData td = new TableWrapData();
		// td.colspan = 2;
		// ec.setLayoutData(td);
		// ec.addExpansionListener(new ExpansionAdapter() {
		// public void expansionStateChanged(ExpansionEvent e) {
		// form.reflow(true);
		// }
		// });

		Label lblReportSpec = toolkit
				.createLabel(form.getBody(), "Report Spec");
		Button btnAddSpec = new Button(form.getBody(), SWT.PUSH);
		btnAddSpec.setText("Add Report Spec");
		// if !lastRspec.isEmpty->AddSpecLine

		CTabItem ctiReport = new CTabItem(folder, SWT.NONE, 1);
		ctiReport.setText("Report");
		CTabItem ctiSubscribers = new CTabItem(folder, SWT.NONE, 2);
		ctiSubscribers.setText("Subscribers");

	}

	private void addReportSpec() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		form.setFocus();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

}

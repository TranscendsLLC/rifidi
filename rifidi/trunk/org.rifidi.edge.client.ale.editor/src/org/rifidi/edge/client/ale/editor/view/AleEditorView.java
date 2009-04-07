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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECBoundarySpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec;
import org.rifidi.edge.client.ale.connection.service.ConnectionService;
import org.rifidi.edge.client.ale.treeview.util.ConnectionWrapper.ConnectionWrapper;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AleEditorView extends ViewPart {

	private FormToolkit toolkit = null;
	private ScrolledForm form = null;
	private CTabFolder folder = null;
	private ArrayList<Composite> reportSpecComposites = null;
	private ECSpec ecSpec = new ECSpec();
	private Text txtStableSet = null;
	private Text txtRepeatAfter = null;
	private Text txtDuration = null;
	private ConnectionService conSvc=null;
	private List lrList;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		ConnectionWrapper cw = new ConnectionWrapper();
		conSvc=cw.getConnectionService();
		
		toolkit = new FormToolkit(parent.getDisplay());
		folder = new CTabFolder(parent, SWT.BOTTOM);
		CTabItem ctiEdit = new CTabItem(folder, SWT.NONE, 0);
		form = toolkit.createScrolledForm(folder);
		ctiEdit.setControl(form);
		ctiEdit.setText("Edit");
		folder.setSelection(ctiEdit);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		form.getBody().setLayout(layout);
		form.setText("NewEcSpec");

		Label lblSpecName = toolkit.createLabel(form.getBody(), "EcSpec Name:");
		Text txtSpecName = toolkit.createText(form.getBody(), "", SWT.BORDER);

		txtSpecName.setLayoutData(new TableWrapData(TableWrapData.FILL));

		Label lblInclSpecInRep = toolkit.createLabel(form.getBody(),
				"Include Spec in Reports?");
		TableWrapData td = new TableWrapData();
		td.colspan = 2;
		lblInclSpecInRep.setLayoutData(td);
		Button btnInclSpecInRepYes = toolkit.createButton(form.getBody(),
				"Yes", SWT.RADIO);
		Button btnInclSpecInRepNo = toolkit.createButton(form.getBody(), "No",
				SWT.RADIO);
		btnInclSpecInRepNo.setSelection(true);

		Section lrSection = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);
		td = new TableWrapData();
		td.colspan = 2;
		lrSection.setLayoutData(td);

		lrSection.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		lrSection.setText("Logical Readers");
		lrSection
				.setDescription("Mark the logical readers that should be used in the query.");
		Composite lrSectionClient = toolkit.createComposite(lrSection);
		lrSectionClient.setLayout(new GridLayout());

		lrList = new List(form.getBody(), SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		ArrayList<String> strings= new ArrayList<String>();
		try {
			strings = (ArrayList<String>) conSvc
			.getAleLrServicePortType()
			.getLogicalReaderNames(
					new EmptyParms())
			.getString();
		} catch (SecurityExceptionResponse e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ImplementationExceptionResponse e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String[] items = new String[strings.size()];
		for (int i = 0; i < strings.size(); i++) {
			items[i] = strings.get(i);
		}
		lrList.setItems(items);

		lrSection.setClient(lrSectionClient);

//		Section boundarySection = toolkit.createSection(form.getBody(),
//				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
//						| Section.EXPANDED);
//		td = new TableWrapData();
//		td.colspan = 2;
//		boundarySection.setLayoutData(td);
//		boundarySection.addExpansionListener(new ExpansionAdapter() {
//			public void expansionStateChanged(ExpansionEvent e) {
//				form.reflow(true);
//			}
//		});
//		boundarySection.setText("Boundary");
//		boundarySection.setDescription("Set the properties for the boundary.");
//		Composite boundarySectionClient = toolkit
//				.createComposite(boundarySection);
//		boundarySectionClient.setLayout(new GridLayout());
//
//		Label lblStableSet = toolkit.createLabel(form.getBody(),
//				"Stable Set (ms):");
//		txtStableSet = toolkit.createText(form.getBody(), "0", SWT.BORDER);
//		td = new TableWrapData(TableWrapData.FILL);
//		txtStableSet.setLayoutData(td);
//		Label lblRepeatAfter = toolkit.createLabel(form.getBody(),
//				"Repeat After (ms):");
//		txtRepeatAfter = toolkit.createText(form.getBody(), "0", SWT.BORDER);
//		td = new TableWrapData(TableWrapData.FILL);
//		txtRepeatAfter.setLayoutData(td);
//		Label lblDuration = toolkit.createLabel(form.getBody(),
//				"Duration (ms):");
//		txtDuration = toolkit.createText(form.getBody(), "0", SWT.BORDER);
//		td = new TableWrapData(TableWrapData.FILL);
//		txtDuration.setLayoutData(td);
//		boundarySection.setClient(boundarySectionClient);
		// Section secRepSpec = toolkit.createSection(form.getBody(),
		// Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
		// | Section.EXPANDED);
		// secRepSpec.setLayout(new GridLayout());
		// gd = new GridData(GridData.GRAB_HORIZONTAL);
		// gd.horizontalSpan = 2;
		// secRepSpec.setLayoutData(gd);
		// TableWrapData td = new TableWrapData(TableWrapData.FILL);
		// td.colspan = 2;
		// secRepSpec.setLayoutData(td);
		// secRepSpec.addExpansionListener(new ExpansionAdapter() {
		// public void expansionStateChanged(ExpansionEvent e) {
		// form.reflow(true);
		// }
		// });
		// secRepSpec.setText("Section title");
		// secRepSpec.setDescription("This is the description that goes "
		// + "below the title");
		// Composite sectionClient = toolkit.createComposite(secRepSpec);
		// sectionClient.setLayout(new GridLayout());
		// secRepSpec.setClient(sectionClient);
		//
		// // ExpandableComposite ec = toolkit.createExpandableComposite(form
		// // .getBody(), ExpandableComposite.TREE_NODE
		// // | ExpandableComposite.CLIENT_INDENT);
		// // ec.setText("Expandable Composite title");
		// // String ctext = "We will now create a somewhat long text so that "
		// // + "we can use it as content for the expandable composite. "
		// // +
		// "Expandable composite is used to hide or show the text using the "
		// // + "toggle control";
		// // Label client = toolkit.createLabel(ec, ctext, SWT.WRAP);
		// // ec.setClient(client);
		// // TableWrapData td = new TableWrapData();
		// // td.colspan = 2;
		// // ec.setLayoutData(td);
		// // ec.addExpansionListener(new ExpansionAdapter() {
		// // public void expansionStateChanged(ExpansionEvent e) {
		// // form.reflow(true);
		// // }
		// // });
		//
		// Label lblReportSpec = toolkit
		// .createLabel(form.getBody(), "Report Spec");
		// Button btnAddSpec = new Button(form.getBody(), SWT.PUSH);
		// btnAddSpec.setText("Add Report Spec");
		// if !lastRspec.isEmpty->AddSpecLine

		CTabItem ctiReport = new CTabItem(folder, SWT.NONE, 1);
		ctiReport.setText("Reports (5)");
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

	public void init(String name, ECSpec ecSpec){
		this.ecSpec=ecSpec;
		form.setText(name);
		ECBoundarySpec bspec = this.ecSpec.getBoundarySpec();
		txtDuration.setText(Long.toString(bspec.getDuration().getValue()));
		txtRepeatAfter.setText(Long.toString(bspec.getRepeatPeriod().getValue()));
		txtStableSet.setText(Long.toString(bspec.getStableSetInterval().getValue()));
		ArrayList<String> logiRead = (ArrayList<String>) this.ecSpec.getLogicalReaders().getLogicalReader();
		String[] loRe=new String[logiRead.size()];
		for(int i=0;i<logiRead.size();i++){
			loRe[i]=logiRead.get(i);
		}
		lrList.setSelection(loRe);
		
	}
}

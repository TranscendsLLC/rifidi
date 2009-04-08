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

import javax.swing.JToolBar.Separator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
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
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportOutputSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportSetSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportSpec;
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
	private ConnectionService conSvc = null;
	private List lrList;
	private Text txtSpecName;
	private String name;

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
		conSvc = cw.getConnectionService();

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
		this.init("NewEcSpec", new ECSpec());

		createReportCtab();
		createSubscriberCtab();

		
		

	}

	/**
	 * 
	 */
	private void createSubscriberCtab() {
		CTabItem ctiReport = new CTabItem(folder, SWT.NONE, 1);
		ctiReport.setText("Reports (5)");
	}

	/**
	 * 
	 */
	private void createReportCtab() {
		CTabItem ctiSubscribers = new CTabItem(folder, SWT.NONE, 2);
		ctiSubscribers.setText("Subscribers");
		
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

	public void init(String name, ECSpec ecSpec) {
		this.ecSpec = ecSpec;
		this.name = name;
		createHeader();
		createSecLogRds();
		createSecBoundary();
		createSecReport();

	}

	/**
	 * 
	 */
	private void createSecReport() {
		// addbutton
//		toolkit.createSeparator(form.getBody(), Separator.HORIZONTAL);
		Button btnAddSpecSec = toolkit.createButton(form.getBody(),
				"Add Report Spec", SWT.PUSH);
		btnAddSpecSec.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				System.out.println("adding section");
				ECReportSpec spec = new ECReportSpec();
				spec.setReportName("NewReport");
				ECReportSetSpec setSpec = new ECReportSetSpec();
				setSpec.setSet("CURRENT");
				spec.setReportSet(setSpec);
				spec.setReportIfEmpty(false);
				spec.setReportOnlyOnChange(false);
				ECReportOutputSpec ecros= new ECReportOutputSpec();
				ecros.setIncludeTag(false);
				ecros.setIncludeEPC(false);
				ecros.setIncludeRawHex(false);
				ecros.setIncludeRawDecimal(false);
				ecros.setIncludeCount(false);
				spec.setOutput(ecros);
				addReportSpecSection(spec);
			}
		});

		if (this.ecSpec.getReportSpecs() != null) {
			ArrayList<ECReportSpec> repSpecs = (ArrayList<ECReportSpec>) this.ecSpec
					.getReportSpecs().getReportSpec();
			for (ECReportSpec reportSpec : repSpecs) {
				addReportSpecSection(reportSpec);
			}
		}

		

	}

	/**
	 * 
	 */
	private void createSecBoundary() {
		Section boundarySection = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);
		TableWrapData tdSbs = new TableWrapData(TableWrapData.FILL_GRAB);
		tdSbs.colspan = 2;
		boundarySection.setLayoutData(tdSbs);
		boundarySection.addExpansionListener(createExpansionAdapter());
		boundarySection.setText("Boundary");
		boundarySection.setDescription("Set the properties for the boundary.");
		Composite boundarySectionClient = toolkit
				.createComposite(boundarySection);
		boundarySectionClient.setLayout(new GridLayout());
		// TODO: get live data from spec
		Label lblStableSet = toolkit.createLabel(form.getBody(),
				"Stable Set (ms):");
		txtStableSet = toolkit.createText(form.getBody(), "0", SWT.BORDER);

		Label lblRepeatAfter = toolkit.createLabel(form.getBody(),
				"Repeat After (ms):");
		txtRepeatAfter = toolkit.createText(form.getBody(), "0", SWT.BORDER);

		Label lblDuration = toolkit.createLabel(form.getBody(),
				"Duration (ms):");
		txtDuration = toolkit.createText(form.getBody(), "0", SWT.BORDER);
		ECBoundarySpec bspec = null;

		if (this.ecSpec.getBoundarySpec() != null) {
			bspec = this.ecSpec.getBoundarySpec();
			txtDuration.setText(Long.toString(bspec.getDuration().getValue()));
			txtRepeatAfter.setText(Long.toString(bspec.getRepeatPeriod()
					.getValue()));
			txtStableSet.setText(Long.toString(bspec.getStableSetInterval()
					.getValue()));
		}

		if (this.ecSpec.getLogicalReaders() != null) {
			ArrayList<String> logiRead = (ArrayList<String>) this.ecSpec
					.getLogicalReaders().getLogicalReader();
			lrList.setSelection(logiRead.toArray(new String[logiRead.size()]));
		}

		boundarySection.setClient(boundarySectionClient);

	}

	/**
	 * 
	 */
	private void createSecLogRds() {
		Section lrSection = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);
		TableWrapData tdLrs = new TableWrapData(TableWrapData.FILL_GRAB);
		tdLrs.colspan = 2;
		lrSection.setLayoutData(tdLrs);

		lrSection.addExpansionListener(createExpansionAdapter());
		lrSection.setText("Logical Readers");
		lrSection
				.setDescription("Mark the logical readers that should be used in the query.");
		Composite lrSectionClient = toolkit.createComposite(lrSection);
		lrSectionClient.setLayout(new GridLayout());

		lrList = new List(form.getBody(), SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		TableWrapData lrListTd = new TableWrapData(TableWrapData.FILL_GRAB);
		lrListTd.colspan = 2;
		lrList.setLayoutData(lrListTd);
		ArrayList<String> strings = new ArrayList<String>();
		try {
			strings = (ArrayList<String>) conSvc.getAleLrServicePortType()
					.getLogicalReaderNames(new EmptyParms()).getString();
		} catch (SecurityExceptionResponse e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ImplementationExceptionResponse e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		lrList.setItems((strings.toArray(new String[strings.size()])));

		lrSection.setClient(lrSectionClient);

	}

	/**
	 * 
	 */
	private void createHeader() {
		form.setText(this.name);

		Label lblSpecName = toolkit.createLabel(form.getBody(), "EcSpec Name:");
		txtSpecName = toolkit.createText(form.getBody(), this.name, SWT.BORDER);

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
		// TODO:read from ecspec
		btnInclSpecInRepNo.setSelection(true);

	}

	private Section addReportSpecSection(ECReportSpec spec) {
		// spec. FIXME
		Section secRepSpec = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);

		TableWrapData repTd = new TableWrapData(TableWrapData.FILL_GRAB);
		repTd.colspan = 2;
		secRepSpec.setLayoutData(repTd);
		secRepSpec.addExpansionListener(createExpansionAdapter());
		secRepSpec.setText("Reporting");
		secRepSpec.setDescription("Set the properties for the reports.");
		Composite reportSectionClient = toolkit.createComposite(secRepSpec);
		reportSectionClient.setLayout(new GridLayout());
		Label lblRepSpecName = toolkit.createLabel(form.getBody(), "Name:");
		Text txtRepSpecName = toolkit.createText(form.getBody(), spec
				.getReportName());
		Label lblRepSet = toolkit.createLabel(form.getBody(), "Report Set:");
		Combo cboRepSet = new Combo(form.getBody(), SWT.READ_ONLY);
		String[] arRepSetData = { "CURRENT", "ADDITIONS", "DELETIONS" };
		cboRepSet.setItems(arRepSetData);
		for (int i = 0; i < cboRepSet.getItemCount(); i++) {
			if (spec.getReportSet().getSet().equalsIgnoreCase(
					cboRepSet.getItem(i))) {
				cboRepSet.select(i);
			}
		}
//		form.layout();
		form.reflow(true);
		return secRepSpec;
		

	}

	private void removeReportSpecSection(Section sec) {

		sec.dispose();
		// sec.get

	}

	private IExpansionListener createExpansionAdapter() {
		IExpansionListener listener = new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		};
		return listener;

	}
}

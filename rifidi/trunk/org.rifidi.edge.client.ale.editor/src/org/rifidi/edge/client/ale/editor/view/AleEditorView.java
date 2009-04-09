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

import javax.swing.JPopupMenu.Separator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
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
		CTabItem ctiEdit = new CTabItem(folder, SWT.NONE);
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
		CTabItem ctiReport = new CTabItem(folder, SWT.NONE);
		ctiReport.setText("Reports (5)");
	}

	/**
	 * 
	 */
	private void createReportCtab() {
		CTabItem ctiSubscribers = new CTabItem(folder, SWT.NONE);
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
		createFooter();

	}

	/**
	 * 
	 */
	private void createFooter() {

	}

	/**
	 * 
	 */
	private void createHeader() {
		form.setText(this.name);
		Button btnSave = toolkit.createButton(form.getBody(), "Save", SWT.PUSH);
		toolkit.createSeparator(form.getBody(), Separator.HORIZONTAL);
		btnSave.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// TODO: assemble spec and save/define
				System.out.println("SAVE");
			}

		});
		toolkit.createLabel(form.getBody(), "EcSpec Name:");
		txtSpecName = toolkit.createText(form.getBody(), this.name, SWT.BORDER);
		txtSpecName.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				form.setText(((Text)e.widget).getText());
				
				

			}

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do here

			}

		});

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

		lrList = new List(lrSectionClient, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL | SWT.FILL);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		// data.minimumHeight=50;
		// data.grabExcessHorizontalSpace=true;
		lrList.setLayoutData(data);

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
		// if(lrList.getItemCount()==0){
		// lrSection.setDescription(lrSection.getDescription()+"\nCURRENTLY NO READERS AVAILABLE.");
		// lrList.setVisible(false);
		// }

		lrSection.setClient(lrSectionClient);

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
		GridLayout grid = new GridLayout();
		grid.numColumns = 2;
		boundarySectionClient.setLayout(grid);
		// TODO: get live data from spec
		toolkit.createLabel(boundarySectionClient, "Stable Set (ms):");
		txtStableSet = toolkit.createText(boundarySectionClient, "0",
				SWT.BORDER);

		toolkit.createLabel(boundarySectionClient, "Repeat After (ms):");
		txtRepeatAfter = toolkit.createText(boundarySectionClient, "0",
				SWT.BORDER);

		toolkit.createLabel(boundarySectionClient, "Duration (ms):");
		txtDuration = toolkit
				.createText(boundarySectionClient, "0", SWT.BORDER);
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
	private void createSecReport() {
		// addbutton
		toolkit.createSeparator(form.getBody(), Separator.HORIZONTAL);
		Button btnAddSpecSec = toolkit.createButton(form.getBody(),
				"Add Report Spec", SWT.PUSH);

		btnAddSpecSec.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				addReportSpecSection(createEmptyRepSpec());
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

	private void addReportSpecSection(ECReportSpec spec) {
		// spec. FIXME
		ArrayList<Section> sections = new ArrayList<Section>();

		// REPORT
		Section reportSection = createStandardSection();
		sections.add(reportSection);
		reportSection.setText("Reporting");
		reportSection.setDescription("Set the properties for the reports.");
		Composite reportSectionClient = toolkit.createComposite(reportSection);
		GridLayout glReport = new GridLayout();
		glReport.numColumns = 2;
		reportSectionClient.setLayout(glReport);

		toolkit.createLabel(reportSectionClient, "Name:");
		Text txtRepSpecName = toolkit.createText(reportSectionClient, spec
				.getReportName(), SWT.BORDER);

		toolkit.createLabel(reportSectionClient, "Report Set:");
		Combo cboRepSet = new Combo(reportSectionClient, SWT.READ_ONLY);
		String[] arRepSetData = new String[3];
		arRepSetData[0] = "CURRENT";
		arRepSetData[1] = "ADDITIONS";
		arRepSetData[2] = "ADDITIONS";

		cboRepSet.setItems(arRepSetData);

		for (int i = 0; i < cboRepSet.getItemCount(); i++) {
			if (spec.getReportSet().getSet().equalsIgnoreCase(
					cboRepSet.getItem(i))) {
				cboRepSet.select(i);
			}
		}

		// TODO:getlivedata
		Label lblRiE = toolkit.createLabel(reportSectionClient,
				"Report if Empty:");

		lblRiE.setLayoutData(createStandardGridData());

		Button btnRepIfEmptyYes = toolkit.createButton(reportSectionClient,
				"Yes", SWT.RADIO);
		Button btnRepIfEmptyNo = toolkit.createButton(reportSectionClient,
				"No", SWT.RADIO);

		Label lblRooC = toolkit.createLabel(reportSectionClient,
				"Report only on Change:");
		lblRooC.setLayoutData(createStandardGridData());
		Button btnRepOnChangeYes = toolkit.createButton(reportSectionClient,
				"Yes", SWT.RADIO);
		Button btnRepOnChangeNo = toolkit.createButton(reportSectionClient,
				"No", SWT.RADIO);

		reportSection.setClient(reportSectionClient);
		form.reflow(true);

		// OUTPUT
		Section outputSection = createStandardSection();
		sections.add(outputSection);
		outputSection.setText("Output Options");
		outputSection.setDescription("Set the properties for the reports.");

		Composite outputSectionClient = toolkit.createComposite(outputSection);
		GridLayout glOutput = new GridLayout();
		// glOutput.numColumns=2;
		outputSectionClient.setLayout(glOutput);

		Label lblInclTags = toolkit.createLabel(outputSectionClient,
				"Include Tags:");
		lblInclTags.setData(createStandardGridData());
		Button btnInclTagsYes = toolkit.createButton(outputSectionClient,
				"Yes", SWT.RADIO);
		Button btnInclTagsNo = toolkit.createButton(outputSectionClient, "No",
				SWT.RADIO);
		// TODO: getlivedata
		Label lblInclEpc = toolkit.createLabel(outputSectionClient,
				"Include EPC:");
		lblInclEpc.setData(createStandardGridData());
		Button btnInclEpcYes = toolkit.createButton(outputSectionClient, "Yes",
				SWT.RADIO);
		Button btnInclEpcNo = toolkit.createButton(outputSectionClient, "No",
				SWT.RADIO);
		Label lblInclHex = toolkit.createLabel(outputSectionClient,
				"Include Raw Hex:");
		lblInclHex.setData(createStandardGridData());
		Button btnInclHexYes = toolkit.createButton(outputSectionClient, "Yes",
				SWT.RADIO);
		Button btnInclHexNo = toolkit.createButton(outputSectionClient, "No",
				SWT.RADIO);
		Label lblInclDec = toolkit.createLabel(outputSectionClient,
				"Include Raw Decimal (deprecated):");
		lblInclDec.setData(createStandardGridData());
		Button btnInclDecYes = toolkit.createButton(outputSectionClient, "Yes",
				SWT.RADIO);
		Button btnInclDecNo = toolkit.createButton(outputSectionClient, "No",
				SWT.RADIO);
		Label lblInclCount = toolkit.createLabel(outputSectionClient,
				"Include Tag Count:");
		lblInclCount.setData(createStandardGridData());
		Button btnInclCountYes = toolkit.createButton(outputSectionClient,
				"Yes", SWT.RADIO);
		Button btnInclCountNo = toolkit.createButton(outputSectionClient, "No",
				SWT.RADIO);

		outputSection.setClient(outputSectionClient);
		form.reflow(true);

		// FILTER
		Section filterSection = createStandardSection();
		sections.add(filterSection);
		filterSection.setText("Filters");
		filterSection
				.setDescription("Set the filterin criteria. Enter one pattern per line.");
		Composite filterSectionClient = toolkit.createComposite(filterSection);
		GridLayout glFilter = new GridLayout();
		glFilter.numColumns = 2;
		filterSectionClient.setLayout(glFilter);

		toolkit.createLabel(filterSectionClient, "Exclude Patterns:");
		// TODO: minsize=...
		Text txtExclPatterns = toolkit.createText(filterSectionClient, "",
				SWT.MULTI | SWT.BORDER);

		toolkit.createLabel(filterSectionClient, "Include Patterns:");

		Text txtInclPatterns = toolkit.createText(filterSectionClient, "",
				SWT.MULTI | SWT.BORDER);

		if (spec.getFilterSpec() != null) {
			if (spec.getFilterSpec().getExcludePatterns() != null) {

				for (String string : spec.getFilterSpec().getExcludePatterns()
						.getExcludePattern()) {
					txtExclPatterns.setText(txtExclPatterns.getText() + "\n"
							+ string);
				}
			}
			if (spec.getFilterSpec().getIncludePatterns() != null) {
				for (String string : spec.getFilterSpec().getIncludePatterns()
						.getIncludePattern()) {
					txtInclPatterns.setText(txtInclPatterns.getText() + "\n"
							+ string);
				}
			}

		}

		filterSection.setClient(filterSectionClient);

		// End of Report (FOOTER)

		toolkit.createSeparator(filterSectionClient, Separator.HORIZONTAL);
		Button btnRemoveThisSection = toolkit.createButton(filterSectionClient,
				"Remove this Report Spec", SWT.PUSH);

		btnRemoveThisSection.addListener(SWT.Selection,
				new AleEditorSelectionListener(form, sections));

		form.reflow(true);

	}

	private IExpansionListener createExpansionAdapter() {
		IExpansionListener listener = new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		};
		return listener;

	}

	private ECReportSpec createEmptyRepSpec() {
		ECReportSpec spec = new ECReportSpec();
		spec.setReportName("NewReport");
		ECReportSetSpec setSpec = new ECReportSetSpec();
		setSpec.setSet("CURRENT");
		spec.setReportSet(setSpec);
		spec.setReportIfEmpty(false);
		spec.setReportOnlyOnChange(false);
		ECReportOutputSpec ecros = new ECReportOutputSpec();
		ecros.setIncludeTag(false);
		ecros.setIncludeEPC(false);
		ecros.setIncludeRawHex(false);
		ecros.setIncludeRawDecimal(false);
		ecros.setIncludeCount(false);
		spec.setOutput(ecros);
		return spec;
	}

	private Section createStandardSection() {
		Section section = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL);
		tableWrapData.colspan = 2;
		section.setLayoutData(tableWrapData);
		section.addExpansionListener(createExpansionAdapter());
		return section;
	}

	private GridData createStandardGridData() {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		return data;
	}
}

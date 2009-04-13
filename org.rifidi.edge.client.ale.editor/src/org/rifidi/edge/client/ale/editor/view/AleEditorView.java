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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Define;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.DuplicateNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ECSpecValidationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECBoundarySpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECFilterSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportOutputSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportSetSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECTime;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECFilterSpec.ExcludePatterns;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECFilterSpec.IncludePatterns;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec.LogicalReaders;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec.ReportSpecs;
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
	private List lrList = null;
	private Text txtSpecName = null;
	private String name = null;
	private Define define = null;
	private ECBoundarySpec bspec = new ECBoundarySpec();
	private ECReportSpec ecrSpec = null;
	private ECReportOutputSpec ros = null;
	private ECFilterSpec fisp = null;
	private ArrayList<ECReportSpec> repSpecs = null;

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

		// static for now:
		Define def = new Define();
		def.setSpec(new ECSpec());
		def.setSpecName("NewEcSpec");
		this.init(def);

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

	public void init(Define define) {
		this.define = define;
		this.ecSpec = define.getSpec();
		this.name = define.getSpecName();
		setPartName(this.name);

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
		// form.setText(this.name);
		Button btnSave = toolkit.createButton(form.getBody(), "Save", SWT.PUSH);
		toolkit.createSeparator(form.getBody(), Separator.HORIZONTAL);
		btnSave.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// TODO:
				// Always save back info to model
				// save just submits/saves.
				ReportSpecs rs = new ReportSpecs();
				for (int i = 0; i < repSpecs.size(); i++) {
					rs.getReportSpec().set(i, repSpecs.get(i));
				}
				try {
					conSvc.getAleServicePortType().define(define);
				} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ECSpecValidationExceptionResponse e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DuplicateNameExceptionResponse e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("SAVE");
			}

		});
		toolkit.createLabel(form.getBody(), "EcSpec Name:");
		txtSpecName = toolkit.createText(form.getBody(), this.name, SWT.BORDER);
		txtSpecName.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// 
				setPartName(((Text) e.widget).getText());
				define.setSpecName(((Text) e.widget).getText());

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
		 btnInclSpecInRepYes.setSelection(this.ecSpec.isIncludeSpecInReports());
		 btnInclSpecInRepYes.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ecSpec.setIncludeSpecInReports(true);
				
			}
			 
		 });
		 Button btnInclSpecInRepNo = toolkit.createButton(form.getBody(),
		 "No",
		 SWT.RADIO);
		 btnInclSpecInRepNo.setSelection(!this.ecSpec.isIncludeSpecInReports());
		 btnInclSpecInRepNo.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do 
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ecSpec.setIncludeSpecInReports(false);
				
			}
			 
		 });

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
		lrList.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do here

			}

			@Override
			public void focusLost(FocusEvent e) {
				List list = ((List) e.widget);

				int[] selection = list.getSelectionIndices();
				for (int i = 0; i < selection.length; i++) {
					LogicalReaders lr = new LogicalReaders();
					lr.getLogicalReader().add(i, list.getItem(i));
					ecSpec.setLogicalReaders(lr);
				}

			}

		});

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.verticalSpan = 2;
		data.heightHint = 50;
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

		// select readers
		if (this.ecSpec.getLogicalReaders() != null) {
			ArrayList<String> logiRead = (ArrayList<String>) this.ecSpec
					.getLogicalReaders().getLogicalReader();
			lrList.setSelection(logiRead.toArray(new String[logiRead.size()]));
		}
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

		if (this.ecSpec.getBoundarySpec() != null) {
			bspec = this.ecSpec.getBoundarySpec();
		} else {
			bspec = new ECBoundarySpec();
			ECTime ectDuration = new ECTime();
			ectDuration.setUnit("MS");
			ectDuration.setValue(0);
			bspec.setDuration(ectDuration);
			ECTime ectRepeat = new ECTime();
			ectRepeat.setUnit("MS");
			ectRepeat.setValue(0);
			bspec.setRepeatPeriod(ectRepeat);
			ECTime ectStable = new ECTime();
			ectStable.setUnit("MS");
			ectStable.setValue(0);
			bspec.setStableSetInterval(ectStable);
		}
		toolkit.createLabel(boundarySectionClient, "Stable Set (ms):");
		txtStableSet = toolkit.createText(boundarySectionClient, Long
				.toString(bspec.getStableSetInterval().getValue()), SWT.BORDER);
		txtStableSet.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT, true,
				false));
		txtStableSet.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do here

			}

			@Override
			public void focusLost(FocusEvent e) {
				ECTime ectStable = new ECTime();
				ectStable.setUnit("MS");
				ectStable.setValue(Long.parseLong(txtStableSet.getText()));
				bspec.setStableSetInterval(ectStable);
			}

		});

		toolkit.createLabel(boundarySectionClient, "Repeat After (ms):");
		txtRepeatAfter = toolkit.createText(boundarySectionClient, Long
				.toString(bspec.getRepeatPeriod().getValue()), SWT.BORDER);
		txtRepeatAfter.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true,
				false));
		txtRepeatAfter.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do here

			}

			@Override
			public void focusLost(FocusEvent e) {
				ECTime ectRepeat = new ECTime();
				ectRepeat.setUnit("MS");
				ectRepeat.setValue(Long.parseLong(txtRepeatAfter.getText()));
				bspec.setRepeatPeriod(ectRepeat);
			}

		});

		toolkit.createLabel(boundarySectionClient, "Duration (ms):");
		txtDuration = toolkit.createText(boundarySectionClient, Long
				.toString(bspec.getDuration().getValue()), SWT.BORDER);
		txtDuration
				.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
		txtDuration.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do here

			}

			@Override
			public void focusLost(FocusEvent e) {
				ECTime ectDuration = new ECTime();
				ectDuration.setUnit("MS");
				ectDuration.setValue(Long.parseLong(txtDuration.getText()));
				bspec.setDuration(ectDuration);
			}

		});
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
		repSpecs = new ArrayList<ECReportSpec>();
		this.ecrSpec = spec;

		if (ecrSpec.getOutput() != null)
			this.ros = ecrSpec.getOutput();
		else
			this.ros = new ECReportOutputSpec();
		if (ecrSpec.getFilterSpec() != null)
			this.fisp = ecrSpec.getFilterSpec();
		else
			this.fisp = createEmptyFilterSpec();

		repSpecs.add(ecrSpec);
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
		txtRepSpecName.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT, true,
				false));
		txtRepSpecName.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do here

			}

			@Override
			public void focusLost(FocusEvent e) {
				Text text = (Text) e.widget;
				ecrSpec.setReportName(text.getText());

			}

		});

		toolkit.createLabel(reportSectionClient, "Report Set:");
		Combo cboRepSet = new Combo(reportSectionClient, SWT.READ_ONLY);
		String[] arRepSetData = new String[3];
		arRepSetData[0] = "CURRENT";
		arRepSetData[1] = "ADDITIONS";
		arRepSetData[2] = "DELETIONS";

		cboRepSet.setItems(arRepSetData);
		cboRepSet.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ECReportSetSpec rss = new ECReportSetSpec();
				Combo combo = (Combo) e.widget;
				rss.setSet(combo.getText());
				ecrSpec.setReportSet(rss);

			}

		});

		for (int i = 0; i < cboRepSet.getItemCount(); i++) {
			if (spec.getReportSet().getSet().equalsIgnoreCase(
					cboRepSet.getItem(i))) {
				cboRepSet.select(i);
			}
		}

		Label lblRiE = toolkit.createLabel(reportSectionClient,
				"Report if Empty:");

		lblRiE.setLayoutData(createStandardGridData());

		Button btnRepIfEmptyYes = toolkit.createButton(reportSectionClient,
				"Yes", SWT.RADIO);
		btnRepIfEmptyYes.setSelection(spec.isReportIfEmpty());
		btnRepIfEmptyYes.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ecrSpec.setReportIfEmpty(true);

			}

		});
		Button btnRepIfEmptyNo = toolkit.createButton(reportSectionClient,
				"No", SWT.RADIO);
		btnRepIfEmptyNo.setSelection(!spec.isReportIfEmpty());
		btnRepIfEmptyNo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ecrSpec.setReportIfEmpty(false);

			}

		});

		Label lblRooC = toolkit.createLabel(reportSectionClient,
				"Report only on Change:");
		lblRooC.setLayoutData(createStandardGridData());
		Button btnRepOnChangeYes = toolkit.createButton(reportSectionClient,
				"Yes", SWT.RADIO);
		btnRepOnChangeYes.setSelection(spec.isReportOnlyOnChange());
		btnRepOnChangeYes.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ecrSpec.setReportOnlyOnChange(true);

			}

		});

		Button btnRepOnChangeNo = toolkit.createButton(reportSectionClient,
				"No", SWT.RADIO);
		btnRepOnChangeNo.setSelection(!spec.isReportOnlyOnChange());
		btnRepOnChangeNo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ecrSpec.setReportOnlyOnChange(false);

			}

		});

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
		btnInclTagsYes.setSelection(ros.isIncludeTag());
		btnInclTagsYes.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ros.setIncludeTag(true);

			}

		});
		Button btnInclTagsNo = toolkit.createButton(outputSectionClient, "No",
				SWT.RADIO);
		btnInclTagsNo.setSelection(!ros.isIncludeTag());
		btnInclTagsNo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ros.setIncludeTag(false);

			}

		});

		Label lblInclEpc = toolkit.createLabel(outputSectionClient,
				"Include EPC:");
		lblInclEpc.setData(createStandardGridData());
		Button btnInclEpcYes = toolkit.createButton(outputSectionClient, "Yes",
				SWT.RADIO);
		btnInclEpcYes.setSelection(ros.isIncludeEPC());
		btnInclEpcYes.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ros.setIncludeEPC(true);

			}

		});
		Button btnInclEpcNo = toolkit.createButton(outputSectionClient, "No",
				SWT.RADIO);
		btnInclEpcNo.setSelection(!ros.isIncludeEPC());
		btnInclEpcNo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ros.setIncludeEPC(false);

			}

		});
		Label lblInclHex = toolkit.createLabel(outputSectionClient,
				"Include Raw Hex:");
		lblInclHex.setData(createStandardGridData());
		Button btnInclHexYes = toolkit.createButton(outputSectionClient, "Yes",
				SWT.RADIO);
		btnInclHexYes.setSelection(ros.isIncludeRawHex());
		btnInclHexYes.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ros.setIncludeRawHex(true);

			}

		});
		Button btnInclHexNo = toolkit.createButton(outputSectionClient, "No",
				SWT.RADIO);
		btnInclHexNo.setSelection(!ros.isIncludeRawHex());
		btnInclHexNo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ros.setIncludeRawHex(false);

			}

		});

		Label lblInclDec = toolkit.createLabel(outputSectionClient,
				"Include Raw Decimal (deprecated):");
		lblInclDec.setData(createStandardGridData());
		Button btnInclDecYes = toolkit.createButton(outputSectionClient, "Yes",
				SWT.RADIO);
		btnInclDecYes.setSelection(ros.isIncludeRawDecimal());
		btnInclDecYes.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ros.setIncludeRawDecimal(true);

			}

		});

		Button btnInclDecNo = toolkit.createButton(outputSectionClient, "No",
				SWT.RADIO);
		btnInclDecNo.setSelection(!ros.isIncludeRawDecimal());
		btnInclDecNo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ros.setIncludeRawDecimal(false);

			}

		});
		Label lblInclCount = toolkit.createLabel(outputSectionClient,
				"Include Tag Count:");
		lblInclCount.setData(createStandardGridData());
		Button btnInclCountYes = toolkit.createButton(outputSectionClient,
				"Yes", SWT.RADIO);
		btnInclCountYes.setSelection(ros.isIncludeCount());
		btnInclCountYes.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ros.setIncludeCount(true);

			}

		});
		Button btnInclCountNo = toolkit.createButton(outputSectionClient, "No",
				SWT.RADIO);
		btnInclCountNo.setSelection(!ros.isIncludeCount());
		btnInclCountNo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ros.setIncludeCount(false);

			}

		});

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

		Text txtExclPatterns = toolkit.createText(filterSectionClient, "",
				SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData txtExclPatData = new GridData(SWT.FILL, SWT.LEFT, true, false);
		txtExclPatData.heightHint = 100;
		txtExclPatterns.setLayoutData(txtExclPatData);
		ArrayList<String> alExclPatterns = (ArrayList<String>) fisp
				.getExcludePatterns().getExcludePattern();
		for (String string : alExclPatterns) {
			txtExclPatterns.setText(txtExclPatterns.getText() + "\n" + string);
		}
		txtExclPatterns.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do here

			}

			@Override
			public void focusLost(FocusEvent e) {
				ExcludePatterns ep = new ExcludePatterns();
				Text text = (Text) e.widget;
				String[] alPatterns = text.getText().split("\n");
				ep.getExcludePattern().clear();
				for (int i = 0; i < alPatterns.length; i++) {
					ep.getExcludePattern().set(i, alPatterns[i]);
				}

			}

		});

		toolkit.createLabel(filterSectionClient, "Include Patterns:");

		Text txtInclPatterns = toolkit.createText(filterSectionClient, "",
				SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData txtInclPatData = new GridData(SWT.FILL, SWT.LEFT, true, false);
		txtInclPatData.heightHint = 100;
		txtInclPatterns.setLayoutData(txtInclPatData);
		ArrayList<String> alInclPatterns = (ArrayList<String>) fisp
				.getIncludePatterns().getIncludePattern();
		for (String string : alInclPatterns) {
			txtInclPatterns.setText(txtInclPatterns.getText() + "\n" + string);
		}
		txtInclPatterns.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do here
				
			}

			@Override
			public void focusLost(FocusEvent e) {
				IncludePatterns ep = new IncludePatterns();
				Text text = (Text) e.widget;
				String[] alPatterns = text.getText().split("\n");
				ep.getIncludePattern().clear();
				for (int i = 0; i < alPatterns.length; i++) {
					ep.getIncludePattern().set(i, alPatterns[i]);
				}
				
			}
			
		});

		filterSection.setClient(filterSectionClient);

		// End of Report (FOOTER)

		toolkit.createSeparator(filterSectionClient, Separator.HORIZONTAL);
		Button btnRemoveThisSection = toolkit.createButton(filterSectionClient,
				"Remove this Report Spec", SWT.PUSH);

		btnRemoveThisSection.addListener(SWT.Selection,
				new AleEditorSelectionListener(form, sections, spec, repSpecs));

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
	
	private ECFilterSpec createEmptyFilterSpec(){
	ECFilterSpec spec =new ECFilterSpec();
	ExcludePatterns ePatterns = new ExcludePatterns();
	ePatterns.getExcludePattern().clear();
	spec.setExcludePatterns(ePatterns);
	IncludePatterns iPatterns = new IncludePatterns();
	spec.setIncludePatterns(iPatterns);
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

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
package org.rifidi.edge.client.ale.ecspecview.views;

import java.util.ArrayList;

import javax.swing.JPopupMenu.Separator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.DuplicateNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ECSpecValidationExceptionResponse;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECBoundarySpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECFieldSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECFilterSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECFilterSpecExtension;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECGroupSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECGroupSpecExtension;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportOutputSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportSetSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECTime;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECFilterSpec.ExcludePatterns;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECFilterSpec.IncludePatterns;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECFilterSpecExtension.FilterList;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec.LogicalReaders;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec.ReportSpecs;
import org.rifidi.edge.client.ale.ecspecview.Activator;
import org.rifidi.edge.client.alelr.ALELRService;
import org.rifidi.edge.client.alelr.ALEService;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ECSpecEditorView extends ViewPart {

	public final static String ID = "org.rifidi.edge.client.ale.ecspecview.views.ECSpecEditorView";

	private FormToolkit toolkit = null;
	private ScrolledForm form = null;
	private CTabFolder folder = null;
	private ECSpec ecSpec = new ECSpec();
	private Text txtStableSet = null;
	private Text txtRepeatAfter = null;
	private Text txtDuration = null;
	private List lrList = null;
	private Text txtSpecName = null;
	private String name = null;
	// private ECBoundarySpec bspec = new ECBoundarySpec();
	// private ECReportSpec ecrSpec = null;
	// private ECReportOutputSpec ros = null;
	// private ECFilterSpec fisp = null;
	// private ECGroupSpec grsp = null;
	private ArrayList<ECReportSpec> repSpecs = new ArrayList<ECReportSpec>();
	private CTabItem ctiEdit;
	private Log logger = LogFactory.getLog(ECSpecEditorView.class);

	private ALEService service;
	private ALELRService lrService;

	// private ConnectionService connectionService = null;

	public ECSpecEditorView() {
		super();
		service = Activator.getDefault().getAleService();
		lrService = Activator.getDefault().getAleLrService();
	}

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

		ctiEdit = new CTabItem(folder, SWT.NONE);
		form = toolkit.createScrolledForm(folder);
		ctiEdit.setControl(form);
		ctiEdit.setText("Edit");
		folder.setSelection(ctiEdit);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		form.getBody().setLayout(layout);

		createReportCtab();
		createSubscriberCtab();

	}

	/**
	 * creates the subscriber tab
	 */
	private void createSubscriberCtab() {
		CTabItem ctiReport = new CTabItem(folder, SWT.NONE);
		ctiReport.setText("Reports (5)");
	}

	/**
	 * creates the report tab
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

	/**
	 * Initializes the editor. All fields in the editor get set respective to
	 * the values from the ECSpec. In an edit action a full spec gets passed in,
	 * in a new action a new ECSpec should be passed in. If init is not called,
	 * the View displays absolutely nothing.
	 * 
	 * @param name
	 *            - name of the ECSpec
	 * @param ecSpec
	 *            - the actual ECSpec
	 */
	public void initSpecView(String name, ECSpec ecSpec) {

		this.ecSpec = ecSpec;
		this.name = name;
		// Changes the name of the view (tab).
		setPartName(this.name);

		// Here all the sections of the form get created.
		createHeader();
		createSecLogRds();
		createSecBoundary();
		createSecReport();
		createFooter();

	}

	/**
	 * Creates all elements that are supposed to be below the dynamic form.
	 */
	private void createFooter() {

	}

	/**
	 * Creates the uppermost part of the form that is always present.
	 */
	private void createHeader() {

		/*
		 * TODO: here is the button that magically initiates the transfer of the
		 * ECSpec Object to the server.
		 */

		Button btnSave = toolkit.createButton(form.getBody(), "Save", SWT.PUSH);
		toolkit.createSeparator(form.getBody(), Separator.HORIZONTAL);
		btnSave.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// set report specs
				ReportSpecs reportSpecs = new ReportSpecs();
				for (ECReportSpec ecrs : repSpecs) {
					reportSpecs.getReportSpec().add(ecrs);
				}
				ecSpec.setReportSpecs(reportSpecs);
				try {
					service.createECSpec(name, ecSpec);
				} catch (ECSpecValidationExceptionResponse e1) {
					MessageBox messageBox = new MessageBox(PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), SWT.ICON_ERROR | SWT.OK);
					messageBox.setMessage(e1.toString());
					messageBox.open();
				} catch (DuplicateNameExceptionResponse e1) {
					MessageBox messageBox = new MessageBox(PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), SWT.ICON_ERROR | SWT.OK);
					messageBox.setMessage(e1.toString());
					messageBox.open();
				}

			}

		});
		toolkit.createLabel(form.getBody(), "EcSpec Name:");
		/*
		 * Text widget for changing the the name of the spec. Initial value is
		 * the name (String) passed in in the init method.
		 */
		txtSpecName = toolkit.createText(form.getBody(), this.name, SWT.BORDER);
		/*
		 * Set name when focus is lost.
		 */
		txtSpecName.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// 
				setPartName(((Text) e.widget).getText());
				name = ((Text) e.widget).getText();

			}

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do here

			}

		});

		txtSpecName.setLayoutData(new TableWrapData(TableWrapData.FILL));

		TableWrapData td = new TableWrapData();
		td.colspan = 2;
		// Check button: include spec in reports.
		Button btnInclSpecInRep = toolkit.createButton(form.getBody(),
				"Include Spec in Reports?", SWT.CHECK);
		btnInclSpecInRep.setLayoutData(td);
		// set selection according to the value from the ecspec.
		btnInclSpecInRep.setSelection(this.ecSpec.isIncludeSpecInReports());
		// set value when selected (clicked).
		btnInclSpecInRep.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ecSpec.setIncludeSpecInReports(button.getSelection());
				// System.out.println("Selection is: " + button.getSelection());
			}

		});

	}

	/**
	 * Creates the logical reader section. Here all the logical readers get
	 * displayed in a list and selected due to the values from the spec. It
	 * fills the logical readers part of the spec.
	 */
	private void createSecLogRds() {
		Section lrSection = createStandardSection();
		lrSection.setText("Logical Readers");
		lrSection
				.setDescription("Mark the logical readers that should be used in the query.");
		Composite lrSectionClient = toolkit.createComposite(lrSection);
		lrSectionClient.setLayout(new GridLayout());

		// List widget that contains the logical readers and allows for their
		// de-/selection
		lrList = new List(lrSectionClient, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);

		// TODO: Former code to retrieve readers from server - probably will
		// change with models.
		/*
		 * ConnectionWrapper wrapper = new ConnectionWrapper();
		 * ArrayList<String> lstReaders = new ArrayList<String>(); try {
		 * lstReaders = (ArrayList<String>) wrapper.getConnectionService()
		 * .getAleLrServicePortType().getLogicalReaderNames( new
		 * EmptyParms()).getString(); } catch (SecurityExceptionResponse e1) {
		 * logger.debug(e1.getMessage()); } catch
		 * (ImplementationExceptionResponse e1) { logger.debug(e1.getMessage());
		 * }
		 */

		// list gets passed to the widget.
		lrList.setItems(lrService.getAvailableReaderNames().toArray(
				new String[] {}));
		// when focus is lost, data gets written back to the spec.
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
		data.verticalSpan = 4;
		data.heightHint = 200;
		data.widthHint = 300;

		lrList.setLayoutData(data);

		// select readers
		if (this.ecSpec.getLogicalReaders() != null) {
			ArrayList<String> logiRead = (ArrayList<String>) this.ecSpec
					.getLogicalReaders().getLogicalReader();
			for (int i = 0; i < lrList.getItemCount(); i++) {
				if (logiRead.contains(lrList.getItem(i)))
					lrList.setSelection(i);
			}

		}
		lrSection.setClient(lrSectionClient);

	}

	/**
	 * Creates the boundary spec section. Values are set according to the passed
	 * in spec. Values set here form the boundary spec part of the ECSpec.
	 */
	private void createSecBoundary() {
		Section boundarySection = createStandardSection();
		boundarySection.setText("Boundary");
		boundarySection.setDescription("Set the properties for the boundary.");

		Composite boundarySectionClient = toolkit
				.createComposite(boundarySection);
		GridLayout grid = new GridLayout();
		grid.numColumns = 2;
		boundarySectionClient.setLayout(grid);

		// if we passed in an empty ecspec we create a boundary spec.

		if (this.ecSpec.getBoundarySpec() == null) {
			ECBoundarySpec bspec = new ECBoundarySpec();
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
			this.ecSpec.setBoundarySpec(bspec);
		}

		// Text element for the Stable set
		toolkit.createLabel(boundarySectionClient, "Stable Set (ms):");
		txtStableSet = toolkit.createText(boundarySectionClient, Long
				.toString(this.ecSpec.getBoundarySpec().getStableSetInterval()
						.getValue()), SWT.BORDER);
		txtStableSet.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT, true,
				false));
		// write back data on focus lost
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
				ECBoundarySpec bspec = ecSpec.getBoundarySpec();
				bspec.setStableSetInterval(ectStable);
				ecSpec.setBoundarySpec(bspec);
			}

		});

		toolkit.createLabel(boundarySectionClient, "Repeat After (ms):");

		// Text element for repeat after
		txtRepeatAfter = toolkit.createText(boundarySectionClient, Long
				.toString(this.ecSpec.getBoundarySpec().getRepeatPeriod()
						.getValue()), SWT.BORDER);
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
				ECBoundarySpec bspec = ecSpec.getBoundarySpec();
				bspec.setRepeatPeriod(ectRepeat);
				ecSpec.setBoundarySpec(bspec);
			}

		});

		toolkit.createLabel(boundarySectionClient, "Duration (ms):");
		// Text for Duration
		txtDuration = toolkit.createText(boundarySectionClient, Long
				.toString(this.ecSpec.getBoundarySpec().getDuration()
						.getValue()), SWT.BORDER);
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
				ECBoundarySpec bspec = ecSpec.getBoundarySpec();
				bspec.setDuration(ectDuration);
				ecSpec.setBoundarySpec(bspec);
			}

		});

		// Start trigger
		toolkit.createLabel(boundarySectionClient, "Start Trigger:");
		Text txtStartTr = toolkit.createText(boundarySectionClient, this.ecSpec
				.getBoundarySpec().getStartTrigger(), SWT.BORDER);
		txtStartTr.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
		txtStartTr.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// nothing

			}

			@Override
			public void focusLost(FocusEvent e) {
				Text text = (Text) e.widget;
				ECBoundarySpec bspec = ecSpec.getBoundarySpec();
				bspec.setStartTrigger(text.getText());
				ecSpec.setBoundarySpec(bspec);

			}

		});

		// Stop Trigger
		toolkit.createLabel(boundarySectionClient, "Stop Trigger:");
		Text txtStopTr = toolkit.createText(boundarySectionClient, this.ecSpec
				.getBoundarySpec().getStopTrigger(), SWT.BORDER);
		txtStopTr.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
		txtStopTr.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// nothing

			}

			@Override
			public void focusLost(FocusEvent e) {
				Text text = (Text) e.widget;
				ECBoundarySpec bspec = ecSpec.getBoundarySpec();
				bspec.setStopTrigger(text.getText());
				ecSpec.setBoundarySpec(bspec);

			}

		});

		boundarySection.setClient(boundarySectionClient);

	}

	/**
	 * Creates the report part of the form. All values set according to the
	 * passed in spec. Assembles the report specs.
	 */
	private void createSecReport() {
		// Separators are only cosmetical, removing however can mess up the
		// layout.
		toolkit.createSeparator(form.getBody(), Separator.HORIZONTAL);
		/*
		 * This button adds one report section per push.
		 */
		Button btnAddSpecSec = toolkit.createButton(form.getBody(),
				"Add Report Spec", SWT.PUSH);

		btnAddSpecSec.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				addReportSpecSection(createEmptyRepSpec());
			}
		});

		/*
		 * check if we have report specs in our ecspec if so, add a report
		 * section per spec.
		 */
		if (this.ecSpec.getReportSpecs() != null) {
			ArrayList<ECReportSpec> repSpecs = (ArrayList<ECReportSpec>) this.ecSpec
					.getReportSpecs().getReportSpec();
			for (ECReportSpec reportSpec : repSpecs) {
				addReportSpecSection(reportSpec);
			}
		}

	}

	/**
	 * Adds a report section to the dynamic form. Each report section
	 * corresponds to one ECReportSpec. A report section is not a toolkit
	 * section, but consists of 4 real sections: report, filter, output,
	 * grouping Report -> ECReportSpec Filter -> ECFilterSpec Output ->
	 * ECReportOutputSpec Grouping -> ECGroupSpec
	 * 
	 * @param spec
	 */
	private void addReportSpecSection(ECReportSpec spec) {

		// to keep track of all the sections in this ReportSpec'section'
		// when the remove report spec button is clicked, these get disposed.
		ArrayList<Section> sections = new ArrayList<Section>();

		// REPORT SECTION
		Section reportSection = createStandardSection();
		// add this section to the list of the ones to be removed when this
		// reportspec is removed.
		sections.add(reportSection);
		reportSection.setText("Reporting");
		reportSection.setDescription("Set the properties for the reports.");
		Composite reportSectionClient = toolkit.createComposite(reportSection);
		GridLayout glReport = new GridLayout();
		glReport.numColumns = 2;
		reportSectionClient.setLayout(glReport);

		toolkit.createLabel(reportSectionClient, "Name:");
		// name for the report
		Text txtRepSpecName = toolkit.createText(reportSectionClient, spec
				.getReportName(), SWT.BORDER);
		txtRepSpecName.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT, true,
				false));
		// hand the spec to the widget to being able to access it in the
		// listener
		txtRepSpecName.setData(spec);
		// set name for the report on focus lost
		txtRepSpecName.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do here

			}

			@Override
			public void focusLost(FocusEvent e) {
				Text text = (Text) e.widget;
				ECReportSpec spec = (ECReportSpec) text.getData();
				spec.setReportName(text.getText());

			}

		});

		toolkit.createLabel(reportSectionClient, "Report Set:");
		// Combo to set the report set
		Combo cboRepSet = new Combo(reportSectionClient, SWT.READ_ONLY);
		// Create the data for the combo
		String[] arRepSetData = new String[3];
		arRepSetData[0] = "CURRENT";
		arRepSetData[1] = "ADDITIONS";
		arRepSetData[2] = "DELETIONS";
		// set data
		cboRepSet.setItems(arRepSetData);
		// pass the spec to the widget
		cboRepSet.setData(spec);
		cboRepSet.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ECReportSetSpec rss = new ECReportSetSpec();
				Combo combo = (Combo) e.widget;
				ECReportSpec ecrSpec = (ECReportSpec) combo.getData();
				rss.setSet(combo.getText());
				ecrSpec.setReportSet(rss);

			}

		});

		// Select the value from the spec.
		for (int i = 0; i < cboRepSet.getItemCount(); i++) {
			if (spec.getReportSet().getSet().equalsIgnoreCase(
					cboRepSet.getItem(i))) {
				cboRepSet.select(i);
			}
		}

		Button btnRepIfEmpty = toolkit.createButton(reportSectionClient,
				"Report if Empty", SWT.CHECK);
		btnRepIfEmpty.setLayoutData(createStandardGridData());
		// set value from spec
		btnRepIfEmpty.setSelection(spec.isReportIfEmpty());
		// hand in spec
		btnRepIfEmpty.setData(spec);
		btnRepIfEmpty.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ECReportSpec ecrSpec = (ECReportSpec) button.getData();
				ecrSpec.setReportIfEmpty(button.getSelection());

			}

		});

		Button btnRepOnChange = toolkit.createButton(reportSectionClient,
				"Report only on Change", SWT.CHECK);
		btnRepOnChange.setLayoutData(createStandardGridData());
		// set selection from spec value
		btnRepOnChange.setSelection(spec.isReportOnlyOnChange());
		// pass in spec
		btnRepOnChange.setData(spec);
		btnRepOnChange.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ECReportSpec ecrSpec = (ECReportSpec) button.getData();
				ecrSpec.setReportOnlyOnChange(button.getSelection());

			}

		});
		// sets the created composite as display
		reportSection.setClient(reportSectionClient);
		/*
		 * has to be done after each section in this method or else the section
		 * wont show up form.layout does not help here.
		 */
		form.reflow(true);

		// Start of OUTPUT section
		Section outputSection = createStandardSection();
		sections.add(outputSection);
		outputSection.setText("Output Options");
		outputSection.setDescription("Set the properties for the reports.");
		// the composite that will host the widgets.
		Composite outputSectionClient = toolkit.createComposite(outputSection);
		GridLayout glOutput = new GridLayout();

		outputSectionClient.setLayout(glOutput);

		// check if there's an output spec - if not, add one
		if (spec.getOutput() == null) {
			ECReportOutputSpec eros = new ECReportOutputSpec();
			eros.setIncludeCount(false);
			eros.setIncludeEPC(false);
			eros.setIncludeRawDecimal(false);
			eros.setIncludeRawHex(false);
			eros.setIncludeTag(false);
			spec.setOutput(eros);
		}

		Button btnInclTags = toolkit.createButton(outputSectionClient,
				"Include Tags", SWT.CHECK);
		btnInclTags.setLayoutData(createStandardGridData());
		// set selection according to spec
		btnInclTags.setSelection(spec.getOutput().isIncludeTag());
		// pass in spec
		btnInclTags.setData(spec);
		btnInclTags.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ECReportSpec spec = (ECReportSpec) button.getData();
				ECReportOutputSpec eros = spec.getOutput();
				eros.setIncludeTag(button.getSelection());
				spec.setOutput(eros);

			}

		});

		Button btnInclEpc = toolkit.createButton(outputSectionClient,
				"Include EPC", SWT.CHECK);
		btnInclEpc.setLayoutData(createStandardGridData());
		// set selection according to spec
		btnInclEpc.setSelection(spec.getOutput().isIncludeEPC());
		// pass in spec
		btnInclEpc.setData(spec);
		btnInclEpc.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ECReportSpec spec = (ECReportSpec) button.getData();
				ECReportOutputSpec eros = spec.getOutput();
				eros.setIncludeEPC(button.getSelection());
				spec.setOutput(eros);

			}

		});

		Button btnInclHex = toolkit.createButton(outputSectionClient,
				"Include Raw Hex", SWT.CHECK);
		btnInclHex.setLayoutData(createStandardGridData());
		btnInclHex.setSelection(spec.getOutput().isIncludeRawHex());
		btnInclHex.setData(spec);
		btnInclHex.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ECReportSpec spec = (ECReportSpec) button.getData();
				ECReportOutputSpec eros = spec.getOutput();
				eros.setIncludeRawHex(button.getSelection());
				spec.setOutput(eros);
			}

		});

		Button btnInclDec = toolkit.createButton(outputSectionClient,
				"Include Raw Decimal (deprecated)", SWT.CHECK);
		btnInclDec.setLayoutData(createStandardGridData());
		btnInclDec.setSelection(spec.getOutput().isIncludeRawDecimal());
		btnInclDec.setData(spec);
		btnInclDec.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ECReportSpec spec = (ECReportSpec) button.getData();
				ECReportOutputSpec eros = spec.getOutput();
				eros.setIncludeRawDecimal(button.getSelection());
				spec.setOutput(eros);

			}

		});

		Button btnInclCount = toolkit.createButton(outputSectionClient,
				"Include Tag Count", SWT.CHECK);
		btnInclCount.setLayoutData(createStandardGridData());
		btnInclCount.setSelection(spec.getOutput().isIncludeCount());
		btnInclCount.setData(spec);
		btnInclCount.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ECReportSpec spec = (ECReportSpec) button.getData();
				ECReportOutputSpec eros = spec.getOutput();
				eros.setIncludeCount(button.getSelection());
				spec.setOutput(eros);

			}

		});
		outputSection.setClient(outputSectionClient);
		form.reflow(true);

		// FILTER
		Section filterSection = createStandardSection();
		// add section to the list of the ones to dispose
		sections.add(filterSection);
		filterSection.setText("Filters");
		filterSection
				.setDescription("Set the filtering criteria. Enter one pattern per line.");
		Composite filterSectionClient = toolkit.createComposite(filterSection);
		GridLayout glFilter = new GridLayout();
		glFilter.numColumns = 2;
		filterSectionClient.setLayout(glFilter);
		// check for filterspec / create one
		if (spec.getFilterSpec() == null) {
			ECFilterSpec filter = new ECFilterSpec();
			ExcludePatterns expat = new ExcludePatterns();
			filter.setExcludePatterns(expat);
			IncludePatterns inpat = new IncludePatterns();
			filter.setIncludePatterns(inpat);
			// TODO: adding a null filter list
			ECFilterSpecExtension ext = new ECFilterSpecExtension();
			ext.setFilterList(new FilterList());
			filter.setExtension(ext);

			spec.setFilterSpec(filter);

		}

		toolkit.createLabel(filterSectionClient, "Exclude Patterns:");

		// Text widget for exclude patterns
		Text txtExclPatterns = toolkit.createText(filterSectionClient, "",
				SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData txtExclPatData = new GridData(SWT.FILL, SWT.LEFT, true, false);
		// make a reasonable size for the text.
		txtExclPatData.heightHint = 100;
		txtExclPatterns.setLayoutData(txtExclPatData);
		ArrayList<String> alExclPatterns = (ArrayList<String>) spec
				.getFilterSpec().getExcludePatterns().getExcludePattern();
		for (String string : alExclPatterns) {
			txtExclPatterns.setText(txtExclPatterns.getText() + "\n" + string);
		}
		txtExclPatterns.setData(spec);
		txtExclPatterns.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do here

			}

			@Override
			public void focusLost(FocusEvent e) {

				ExcludePatterns ep = new ExcludePatterns();
				Text text = (Text) e.widget;
				ECReportSpec spec = (ECReportSpec) text.getData();
				ECFilterSpec filter = spec.getFilterSpec();
				String[] alPatterns = text.getText().split("\n");

				for (int i = 0; i < alPatterns.length; i++) {
					ep.getExcludePattern().add(alPatterns[i]);
				}
				filter.setExcludePatterns(ep);
				spec.setFilterSpec(filter);

			}

		});

		toolkit.createLabel(filterSectionClient, "Include Patterns:");

		Text txtInclPatterns = toolkit.createText(filterSectionClient, "",
				SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData txtInclPatData = new GridData(SWT.FILL, SWT.LEFT, true, false);
		txtInclPatData.heightHint = 100;
		txtInclPatterns.setLayoutData(txtInclPatData);
		ArrayList<String> alInclPatterns = (ArrayList<String>) spec
				.getFilterSpec().getIncludePatterns().getIncludePattern();
		for (String string : alInclPatterns) {
			txtInclPatterns.setText(txtInclPatterns.getText() + "\n" + string);
		}
		txtInclPatterns.setData(spec);
		txtInclPatterns.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do here

			}

			@Override
			public void focusLost(FocusEvent e) {
				IncludePatterns ep = new IncludePatterns();
				Text text = (Text) e.widget;
				ECReportSpec spec = (ECReportSpec) text.getData();
				ECFilterSpec filter = spec.getFilterSpec();
				String[] alPatterns = text.getText().split("\n");
				for (int i = 0; i < alPatterns.length; i++) {
					ep.getIncludePattern().add(alPatterns[i]);
				}
				filter.setIncludePatterns(ep);
				spec.setFilterSpec(filter);

			}

		});

		filterSection.setClient(filterSectionClient);
		form.reflow(true);

		// GROUPING

		Section groupingSection = createStandardSection();
		sections.add(groupingSection);
		groupingSection.setText("Grouping");
		groupingSection
				.setDescription("Set the grouping criteria. Enter one pattern per line.");
		Composite groupingSectionClient = toolkit
				.createComposite(groupingSection);
		GridLayout glGrouping = new GridLayout();
		glGrouping.numColumns = 2;
		groupingSectionClient.setLayout(glGrouping);

		toolkit.createLabel(groupingSectionClient, "Grouping Patterns:");
		// Text for grouping
		Text txtGrpPatterns = toolkit.createText(groupingSectionClient, "",
				SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData txtGrpPatData = new GridData(SWT.FILL, SWT.LEFT, true, false);
		txtGrpPatData.heightHint = 100;
		txtGrpPatterns.setLayoutData(txtGrpPatData);

		// set data from spec
		if (spec.getGroupSpec() == null) {
			ECGroupSpec group = new ECGroupSpec();
			spec.setGroupSpec(group);
		}
		ArrayList<String> alGrpPatterns = (ArrayList<String>) spec
				.getGroupSpec().getPattern();
		for (String string : alGrpPatterns) {
			txtGrpPatterns.setText(txtGrpPatterns.getText() + "\n" + string);
		}
		// pass in spec
		txtGrpPatterns.setData(spec);
		txtGrpPatterns.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do

			}

			@Override
			public void focusLost(FocusEvent e) {
				Text text = (Text) e.widget;
				ECReportSpec spec = (ECReportSpec) text.getData();
				ECGroupSpec group = spec.getGroupSpec();
				String[] alPatterns = text.getText().split("\n");

				for (int i = 0; i < alPatterns.length; i++) {
					group.getPattern().add(alPatterns[i]);
				}
				// TODO: Supply a null GroupSpec
				ECGroupSpecExtension groupExt = new ECGroupSpecExtension();
				ECFieldSpec fieldSpec = new ECFieldSpec();
				groupExt.setFieldspec(fieldSpec);
				group.setExtension(groupExt);

				spec.setGroupSpec(group);
			}

		});
		groupingSection.setClient(groupingSectionClient);
		// add spec to list of report specs
		this.repSpecs.add(spec);

		// End of Report (FOOTER)

		toolkit.createSeparator(groupingSectionClient, Separator.HORIZONTAL);

		// button to remove report specs
		Button btnRemoveThisSection = toolkit.createButton(
				groupingSectionClient, "Remove this Report Spec", SWT.PUSH);

		btnRemoveThisSection.addListener(SWT.Selection,
				new ALEEditorSelectionListener(form, sections, spec, repSpecs));

		form.reflow(true);

	}

	/**
	 * Creates an IExpansionListener that every section needs. It allows for
	 * minimizing the section.
	 * 
	 * @return IExpansionListener
	 */
	private IExpansionListener createExpansionAdapter() {
		IExpansionListener listener = new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		};
		return listener;

	}

	/**
	 * Creates an 'empty' report spec. Sets standard values that are needed to
	 * init the view.
	 * 
	 * @return ECReportSpec
	 */
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

	/**
	 * Creates an 'empty' ECFilterSpec. Sets standard values that are needed to
	 * init the view.
	 * 
	 * @return ECFilterSpec
	 */
	private ECFilterSpec createEmptyFilterSpec() {
		ECFilterSpec spec = new ECFilterSpec();
		ExcludePatterns ePatterns = new ExcludePatterns();
		ePatterns.getExcludePattern().clear();
		spec.setExcludePatterns(ePatterns);
		IncludePatterns iPatterns = new IncludePatterns();
		spec.setIncludePatterns(iPatterns);
		return spec;
	}

	/**
	 * Creates a section with the standard values that are always the same.
	 * 
	 * @return Section
	 */
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

	/**
	 * Creates the GridData that is needed for the SectionClient Composite that
	 * hosts all the widgets of a section.
	 * 
	 * @return GridData
	 */
	private GridData createStandardGridData() {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		return data;
	}

	/**
	 * Shows a dialog box that contains the String parameter.
	 * 
	 * @param message
	 *            to show
	 */
	private void showMessage(String message) {
		MessageDialog.openInformation(folder.getShell(), this.name, message);
	}

	/**
	 * Closes the view. (After successfully defining the spec)
	 */
	private void closeThisView() {
		this.dispose();
	}

	// private IViewPart getView(String id) {
	// IViewReference viewReferences[] = PlatformUI.getWorkbench()
	// .getActiveWorkbenchWindow().getActivePage().getViewReferences();
	// for (int i = 0; i < viewReferences.length; i++) {
	// if (id.equals(viewReferences[i].getId())) {
	// return viewReferences[i].getView(false);
	// }
	// }
	// return null;
	// }

}

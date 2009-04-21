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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
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
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECBoundarySpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECFilterSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECGroupSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportOutputSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportSetSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECTime;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECFilterSpec.ExcludePatterns;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECFilterSpec.IncludePatterns;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec.LogicalReaders;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec.ReportSpecs;
import org.rifidi.edge.client.ale.models.aleserviceporttype.AleServicePortTypeWrapper;
import org.rifidi.edge.client.ale.models.ecspec.RemoteSpecModelWrapper;
import org.rifidi.edge.client.ale.treeview.views.LrTreeView;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AleEditorView extends ViewPart {
	
	public final static String ID = "org.rifidi.edge.client.ale.editor.view.aleeditorview";

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
	private ECBoundarySpec bspec = new ECBoundarySpec();
	private ECReportSpec ecrSpec = null;
	private ECReportOutputSpec ros = null;
	private ECFilterSpec fisp = null;
	private ECGroupSpec grsp = null;
	private ArrayList<ECReportSpec> repSpecs = null;
	private RemoteSpecModelWrapper specWrapper = null;
	private CTabItem ctiEdit;

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

		// static for now:
		// Define def = new Define();
		// def.setSpec(new ECSpec());
		// def.setSpecName("NewEcSpec");
		// this.init(def);

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

	public void init(RemoteSpecModelWrapper spec) {
		this.specWrapper = spec;
		this.ecSpec = spec.getEcSpec();
		this.name = spec.getName();
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

				// Always save back info to model
				// save just submits/saves.
				ReportSpecs rs = new ReportSpecs();
				for (int i = 0; i < repSpecs.size(); i++) {
					rs.getReportSpec().set(i, repSpecs.get(i));
				}
				if (specWrapper.getParent() instanceof AleServicePortTypeWrapper) {
					String result = specWrapper.define();
					if (!result.isEmpty()) {
						showMessage(result);
					}
				}

			}

		});
		toolkit.createLabel(form.getBody(), "EcSpec Name:");
		txtSpecName = toolkit.createText(form.getBody(), this.name, SWT.BORDER);
		txtSpecName.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// 
				setPartName(((Text) e.widget).getText());
				specWrapper.setName(((Text) e.widget).getText());

			}

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do here

			}

		});

		txtSpecName.setLayoutData(new TableWrapData(TableWrapData.FILL));

		TableWrapData td = new TableWrapData();
		td.colspan = 2;

		Button btnInclSpecInRep = toolkit.createButton(form.getBody(),
				"Include Spec in Reports?", SWT.CHECK);
		btnInclSpecInRep.setLayoutData(td);
		btnInclSpecInRep.setSelection(this.ecSpec.isIncludeSpecInReports());
		btnInclSpecInRep.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ecSpec.setIncludeSpecInReports(button.getSelection());
				System.out.println("Selection is: " + button.getSelection());
			}

		});

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
		LrTreeView view = (LrTreeView)getView(LrTreeView.ID);
		

		lrList = new List(lrSectionClient, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL | SWT.FILL);
		
		Control[] controls=view.getViewer().getTree().getChildren();
		lrList.removeAll();
		for(int i=0;i<controls.length;i++){
			lrList.add(controls[i].toString());
		}
		
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

		lrList.setLayoutData(data);

		ArrayList<String> strings = new ArrayList<String>();
		// try {
		// strings = (ArrayList<String>) conSvc.getAleLrServicePortType()
		// .getLogicalReaderNames(new EmptyParms()).getString();
		// } catch (SecurityExceptionResponse e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// } catch (ImplementationExceptionResponse e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

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
		if (ecrSpec.getGroupSpec() != null)
			this.grsp = ecrSpec.getGroupSpec();
		else
			this.grsp = new ECGroupSpec();

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

		Button btnRepIfEmpty = toolkit.createButton(reportSectionClient,
				"Report if Empty", SWT.CHECK);
		btnRepIfEmpty.setLayoutData(createStandardGridData());
		btnRepIfEmpty.setSelection(spec.isReportIfEmpty());
		btnRepIfEmpty.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ecrSpec.setReportIfEmpty(button.getSelection());

			}

		});

		Button btnRepOnChange = toolkit.createButton(reportSectionClient,
				"Report only on Change", SWT.CHECK);
		btnRepOnChange.setLayoutData(createStandardGridData());
		btnRepOnChange.setSelection(spec.isReportOnlyOnChange());
		btnRepOnChange.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ecrSpec.setReportOnlyOnChange(button.getSelection());

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

		Button btnInclTags = toolkit.createButton(outputSectionClient,
				"Include Tags", SWT.CHECK);
		btnInclTags.setLayoutData(createStandardGridData());
		btnInclTags.setSelection(ros.isIncludeTag());
		btnInclTags.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ros.setIncludeTag(button.getSelection());

			}

		});

		Button btnInclEpc = toolkit.createButton(outputSectionClient,
				"Include EPC", SWT.CHECK);
		btnInclEpc.setData(createStandardGridData());
		btnInclEpc.setSelection(ros.isIncludeEPC());
		btnInclEpc.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ros.setIncludeEPC(button.getSelection());

			}

		});

		Button btnInclHex = toolkit.createButton(outputSectionClient,
				"Include Raw Hex", SWT.CHECK);
		btnInclHex.setLayoutData(createStandardGridData());
		btnInclHex.setSelection(ros.isIncludeRawHex());
		btnInclHex.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ros.setIncludeRawHex(button.getSelection());

			}

		});

		Button btnInclDec = toolkit.createButton(outputSectionClient,
				"Include Raw Decimal (deprecated)", SWT.CHECK);
		btnInclDec.setLayoutData(createStandardGridData());
		btnInclDec.setSelection(ros.isIncludeRawDecimal());
		btnInclDec.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ros.setIncludeRawDecimal(button.getSelection());

			}

		});

		Button btnInclCount = toolkit.createButton(outputSectionClient,
				"Include Tag Count", SWT.CHECK);
		btnInclCount.setLayoutData(createStandardGridData());
		btnInclCount.setSelection(ros.isIncludeCount());
		btnInclCount.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				ros.setIncludeCount(button.getSelection());

			}

		});
		outputSection.setClient(outputSectionClient);
		form.reflow(true);

		// FILTER
		Section filterSection = createStandardSection();
		sections.add(filterSection);
		filterSection.setText("Filters");
		filterSection
				.setDescription("Set the filtering criteria. Enter one pattern per line.");
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
		txtInclPatterns.addFocusListener(new FocusListener() {

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
		Text txtGrpPatterns = toolkit.createText(groupingSectionClient, "",
				SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData txtGrpPatData = new GridData(SWT.FILL, SWT.LEFT, true, false);
		txtGrpPatData.heightHint = 100;
		txtGrpPatterns.setLayoutData(txtGrpPatData);
		ArrayList<String> alGrpPatterns;
		if (this.grsp.getPattern() == null)
			alGrpPatterns = new ArrayList<String>();
		else
			alGrpPatterns = (ArrayList<String>) this.grsp.getPattern();
		for (String string : alGrpPatterns) {
			txtGrpPatterns.setText(txtGrpPatterns.getText() + "\n" + string);
		}
		txtGrpPatterns.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do

			}

			@Override
			public void focusLost(FocusEvent e) {
				Text text = (Text) e.widget;
				String[] alPatterns = text.getText().split("\n");
				grsp.getPattern().clear();

				for (int i = 0; i < alPatterns.length; i++) {
					grsp.getPattern().set(i, alPatterns[i]);
				}

			}

		});
		groupingSection.setClient(groupingSectionClient);

		// End of Report (FOOTER)

		toolkit.createSeparator(groupingSectionClient, Separator.HORIZONTAL);
		Button btnRemoveThisSection = toolkit.createButton(
				groupingSectionClient, "Remove this Report Spec", SWT.PUSH);

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

	private ECFilterSpec createEmptyFilterSpec() {
		ECFilterSpec spec = new ECFilterSpec();
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

	private void showMessage(String message) {
		MessageDialog.openInformation(folder.getShell(), this.name, message);
	}
	
	private IViewPart getView(String id) {
		IViewReference viewReferences[] = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (int i = 0; i < viewReferences.length; i++) {
			if (id.equals(viewReferences[i].getId())) {
				return viewReferences[i].getView(false);
			}
		}
		return null;
	}

}

/**
 * 
 */
package org.rifidi.edge.client.sal.wizards.submitjob;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.sal.SALPluginActivator;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SubmitJobWizardPage extends WizardPage {

	private Set<RemoteCommandConfiguration> commandConfigurations;
	private RemoteCommandConfiguration selection;
	private Composite timingComposite;
	private Spinner spinner;
	private boolean recurring = true;
	private Label spinnerLabel;

	protected SubmitJobWizardPage(String pageName,
			Set<RemoteCommandConfiguration> commandConfigurations) {
		super(pageName);
		this.commandConfigurations = commandConfigurations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		try {
			setTitle("Job Submission Wizard");
			setDescription("Submit a New Job to a Session");
			Composite composite = new Composite(parent, SWT.None);
			GridLayout gridLayout = new GridLayout(1, true);
			composite.setLayout(gridLayout);

			Group topGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			topGroup.setLayoutData(data);
			topGroup.setText("Available Command Templates");
			topGroup.setLayout(new GridLayout(1, true));

			Tree tree = new Tree(topGroup, SWT.None);
			GridData treeGridData = new GridData(GridData.FILL_HORIZONTAL);
			treeGridData.heightHint = 200;
			treeGridData.grabExcessHorizontalSpace = true;
			tree.setLayoutData(treeGridData);
			fillTree(tree);
			tree.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (e.item.getData() instanceof RemoteCommandConfiguration) {
						selection = (RemoteCommandConfiguration) e.item
								.getData();
						validate();
					} else {
						setPageComplete(false);
					}
				}

			});

			Group bottomGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
			bottomGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			bottomGroup.setText("Scheduling Options");
			bottomGroup.setLayout(new GridLayout(1, true));

			Button button1 = new Button(bottomGroup, SWT.RADIO);
			button1.setText("One Time Execution");
			button1.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					recurring = false;
					spinner.setEnabled(false);
					spinnerLabel.setEnabled(false);
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					recurring = false;
					spinner.setEnabled(false);
					spinnerLabel.setEnabled(false);
				}

			});
			Button button2 = new Button(bottomGroup, SWT.RADIO);
			button2.setText("Recurring Execution");
			button2.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					recurring = true;
					spinnerLabel.setEnabled(true);
					spinner.setEnabled(true);

				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					recurring = true;
					spinnerLabel.setEnabled(true);
					spinner.setEnabled(true);
				}
			});

			timingComposite = new Composite(bottomGroup, SWT.NONE);
			timingComposite.setLayout(new GridLayout(2, false));
			GridData timingCompositeGridData = new GridData(
					GridData.FILL_HORIZONTAL);
			timingCompositeGridData.horizontalIndent = 20;
			timingCompositeGridData.verticalIndent = 0;
			timingComposite.setLayoutData(timingCompositeGridData);
			spinnerLabel = new Label(timingComposite, SWT.None);
			spinnerLabel.setText("Interval (ms): ");
			spinner = new Spinner(timingComposite, SWT.BORDER);
			GridData spinnerGridData = new GridData();
			spinnerGridData.grabExcessHorizontalSpace = true;
			spinnerGridData.widthHint = 100;
			spinner.setLayoutData(spinnerGridData);
			spinner.setMinimum(0);
			spinner.setMaximum(Integer.MAX_VALUE);
			spinner.setSelection(1000);

			setControl(composite);
			setPageComplete(false);
			button2.setSelection(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void fillTree(Tree tree) {
		Map<String, TreeItem> commandTypesToTreeItem = new HashMap<String, TreeItem>();
		for (RemoteCommandConfiguration config : this.commandConfigurations) {
			TreeItem item = commandTypesToTreeItem.get(config.getCommandType());
			if (item == null) {
				item = new TreeItem(tree, SWT.None);
				item.setData(config.getCommandType());
				item.setText(config.getCommandType());
				item.setImage(SALPluginActivator.getDefault()
						.getImageRegistry()
						.get(SALPluginActivator.IMAGE_FOLDER));
				commandTypesToTreeItem.put(config.getCommandType(), item);
			}
			TreeItem configItem = new TreeItem(item, SWT.None);
			configItem.setText(config.getID());
			configItem.setData(config);
			configItem.setImage(SALPluginActivator.getDefault()
					.getImageRegistry().get(SALPluginActivator.IMAGE_COG));
		}

	}

	private void validate() {
		setPageComplete(true);
	}

	protected RemoteCommandConfiguration getSelectedConfiguration() {
		return this.selection;
	}

	protected Long getInterval() {
		if (recurring) {
			return new Long(spinner.getSelection());
		} else
			return 0l;
	}
}

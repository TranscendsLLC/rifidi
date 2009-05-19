
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.sal.SALPluginActivator;

/**
 * A Wizard Page used to submit commands to the session. Allows the user to
 * choose the commad to submit
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class SubmitJobWizardPage extends WizardPage {

	/** The set of possible commands */
	private Set<RemoteCommandConfiguration> commandConfigurations;
	/** The chosen command */
	private RemoteCommandConfiguration selection;
	/** A composite to allow users to choose the execution interval */
	private IntervalChooserComposite intervalChooserComposite;

	/***
	 * Constructor
	 * 
	 * @param pageName
	 * @param commandConfigurations
	 *            Set of possible commands to choose from
	 */
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
			this.intervalChooserComposite = new IntervalChooserComposite(
					bottomGroup, SWT.None);
			setControl(composite);
			setPageComplete(false);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Private helper method to fill the tree with possible command choices
	 * 
	 * @param tree
	 */
	private void fillTree(Tree tree) {
		Map<String, TreeItem> commandTypesToTreeItem = new HashMap<String, TreeItem>();
		// step through each command.
		for (RemoteCommandConfiguration config : this.commandConfigurations) {
			TreeItem item = commandTypesToTreeItem.get(config.getCommandType());
			// create a command category if one does not exist
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

	/**
	 * Private method called once a command has been chosen
	 */
	private void validate() {
		setPageComplete(true);
	}

	/**
	 * Get the selected command
	 * 
	 * @return
	 */
	protected RemoteCommandConfiguration getSelectedConfiguration() {
		return this.selection;
	}

	/**
	 * Get the selected execution interval
	 * 
	 * @return
	 */
	protected Long getInterval() {
		return this.intervalChooserComposite.getInterval();
	}

}

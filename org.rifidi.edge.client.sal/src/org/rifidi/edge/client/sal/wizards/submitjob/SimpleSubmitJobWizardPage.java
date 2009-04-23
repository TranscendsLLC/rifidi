/**
 * 
 */
package org.rifidi.edge.client.sal.wizards.submitjob;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * This is a wizard page that allows a user to pick only the execution interval
 * of a command to be submitted (i.e. the user cannot pick the
 * CommandConfiguraiton)
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SimpleSubmitJobWizardPage extends WizardPage {

	/** The widget that allows the user to choose the interval */
	private IntervalChooserComposite intervalChooserComposite;

	/**
	 * Constructor
	 * 
	 * @param pageName
	 */
	protected SimpleSubmitJobWizardPage(String pageName) {
		super(pageName);
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
		setTitle("Job Submission Wizard");
		setDescription("Submit a New Job to a Session");
		Group bottomGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		bottomGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bottomGroup.setText("Scheduling Options");
		bottomGroup.setLayout(new GridLayout(1, true));
		this.intervalChooserComposite = new IntervalChooserComposite(
				bottomGroup, SWT.None);
		setControl(bottomGroup);
		setPageComplete(true);

	}

	/**
	 * 
	 * @return The interval that the user chose
	 */
	public Long getInterval() {
		return intervalChooserComposite.getInterval();
	}

}


package org.rifidi.edge.client.sal.wizards.submitjob;

import java.util.Set;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.model.sal.RemoteSession;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerTreeContentProvider;

/**
 * A wizard used to submit jobs to a reader when the ComandConfiguration is not
 * known
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class SubmitJobWizard extends Wizard implements IWizard {

	/** The set of possible command configurations */
	private Set<RemoteCommandConfiguration> commandConfigs;
	/** The wizard page to use */
	private SubmitJobWizardPage page;
	/** The remote session to submit the command to */
	private RemoteSession session;

	/**
	 * Constructor
	 * 
	 * @param session
	 *            The session to submit the command to
	 * @param commandConfigs
	 *            The set of possible command configurations
	 */
	public SubmitJobWizard(RemoteSession session,
			Set<RemoteCommandConfiguration> commandConfigs) {
		this.commandConfigs = commandConfigs;
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		if (page != null) {
			RemoteCommandConfiguration configuration = page
					.getSelectedConfiguration();
			Long interval = page.getInterval();
			if (configuration != null && interval != null) {
				EdgeServerTreeContentProvider.getEdgeServerController()
						.submitCommand(session, configuration, interval);
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		page = new SubmitJobWizardPage("Submit a Job", commandConfigs);
		addPage(page);

	}

}

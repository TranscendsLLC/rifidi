/**
 * 
 */
package org.rifidi.edge.client.sal.wizards.submitjob;

import java.util.Set;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.model.sal.RemoteSession;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerTreeContentProvider;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SubmitJobWizard extends Wizard implements IWizard {

	private Set<RemoteCommandConfiguration> commandConfigs;
	private SubmitJobWizardPage page;
	private RemoteSession session;

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
						.scheduleJob(session, configuration, interval);
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

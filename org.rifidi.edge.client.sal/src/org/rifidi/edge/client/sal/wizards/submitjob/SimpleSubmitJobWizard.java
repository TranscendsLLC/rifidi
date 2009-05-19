
package org.rifidi.edge.client.sal.wizards.submitjob;

import org.eclipse.jface.wizard.Wizard;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.model.sal.RemoteSession;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerTreeContentProvider;

/**
 * This is a wizard used for submitting a command to the server if the Session
 * and RemoteCommandConfiguration are already known
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class SimpleSubmitJobWizard extends Wizard {

	/** The wizard page to use */
	private SimpleSubmitJobWizardPage page;
	/** The CommandConfiguration to submit */
	private RemoteCommandConfiguration configuration;
	/** The session to submit the command to */
	private RemoteSession session;

	/***
	 * Constructor
	 * 
	 * @param configuration
	 *            The Configuration to submit
	 * @param session
	 *            The session to submit to
	 */
	public SimpleSubmitJobWizard(RemoteCommandConfiguration configuration,
			RemoteSession session) {
		this.configuration = configuration;
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
		page = new SimpleSubmitJobWizardPage("Submit a Job");
		addPage(page);

	}

}

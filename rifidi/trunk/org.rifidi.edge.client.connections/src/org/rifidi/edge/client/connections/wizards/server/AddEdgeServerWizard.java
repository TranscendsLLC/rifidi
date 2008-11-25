package org.rifidi.edge.client.connections.wizards.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.rifidi.edge.client.connections.registryservice.EdgeServerConnectionRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class AddEdgeServerWizard extends Wizard {
	static private Log logger = LogFactory.getLog(AddEdgeServerWizard.class);

	private AddEdgeServerWizardPage connectEdgeServerWizardPage;
	
	private EdgeServerConnectionRegistryService edgeServerConnectionRegistryService;

	public AddEdgeServerWizard() {
		this.setWindowTitle("EdgeServer Configuration Wizard");
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public void addPages() {
		connectEdgeServerWizardPage = new AddEdgeServerWizardPage();
		addPage(connectEdgeServerWizardPage);
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		logger.debug("Finishing");
		String url = connectEdgeServerWizardPage.getURL();
		int port = connectEdgeServerWizardPage.getPort();
		int jmsPort = connectEdgeServerWizardPage.getJmsPort();

		try {
			this.edgeServerConnectionRegistryService.createConnection(url, port, jmsPort);		
		} catch (Exception e) {
			logger.debug(e.getMessage());
			Object o = getContainer().getCurrentPage();
			if (o instanceof WizardPage) {
				logger.debug("Setting error Message");
				e.printStackTrace();
				((WizardPage) o)
						.setErrorMessage("Can't create connection to EdgeServer");
				return false;
			}
		}

		logger.debug("Finish successful.");
		return true;
	}

	@Inject
	public void setEdgeServerConnectionRegistryService(
			EdgeServerConnectionRegistryService edgeServerConnectionRegistryService) {
		this.edgeServerConnectionRegistryService = edgeServerConnectionRegistryService;
	}

}

package org.rifidi.edge.client.connections.wizards.reader;

import org.eclipse.jface.wizard.Wizard;
import org.rifidi.edge.client.connections.exceptions.RifidiInvalidReaderInfoException;
import org.rifidi.edge.client.connections.registryservice.EdgeServerConnectionRegistryService;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.wizards.reader.pages.AddReaderWizardData;
import org.rifidi.edge.client.connections.wizards.reader.pages.EditReaderInfoWizardPage;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class EditReaderInfoWizard extends Wizard {

	private RemoteReader remoteReader;
	private EdgeServerConnectionRegistryService edgeServerConnectionRegistryService;
	private AddReaderWizardData data;

	// private Log logger = LogFactory.getLog(EditReaderInfoWizard.class);

	public EditReaderInfoWizard(RemoteReader remoteReader) {
		super();
		this.remoteReader = remoteReader;
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public void addPages() {
		super.addPages();
		data = new AddReaderWizardData();
		// create new WizardPage

		data.readerInfoClass = remoteReader.getReaderInfoClassName();
		EditReaderInfoWizardPage page = new EditReaderInfoWizardPage(
				"EditReaderInfoWizardPage", data,
				edgeServerConnectionRegistryService.getConnection(remoteReader
						.getServerID()));
		page.setRemoteReader(remoteReader);
		addPage(page);

	}

	// private Log logger = LogFactory.getLog(EditReaderInfoWizard.class);

	@Override
	public boolean performFinish() {
		try {
			remoteReader.setReaderInfo(data.widget.getXMLAsString(true));
		} catch (RifidiInvalidReaderInfoException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Inject
	public void setEdgeServerConnectionRegistryService(
			EdgeServerConnectionRegistryService edgeServerConnectionRegistryService) {
		this.edgeServerConnectionRegistryService = edgeServerConnectionRegistryService;
	}

}

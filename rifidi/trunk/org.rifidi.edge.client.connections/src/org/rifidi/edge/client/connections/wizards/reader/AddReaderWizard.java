package org.rifidi.edge.client.connections.wizards.reader;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.exceptions.CannotCreateRemoteReaderException;
import org.rifidi.edge.client.connections.wizards.reader.pages.AddReaderWizardData;
import org.rifidi.edge.client.connections.wizards.reader.pages.EditReaderInfoWizardPage;
import org.rifidi.edge.client.connections.wizards.reader.pages.ReaderInfoChooserWizardPage;
import org.rifidi.edge.core.rmi.client.sessionstub.valueobjects.ReaderInfoWrapper;
import org.rifidi.services.registry.ServiceRegistry;

public class AddReaderWizard extends Wizard {
	// private Log logger = LogFactory.getLog(AddReaderWizard.class);
	private EdgeServerConnection connection;
	private ReaderInfoChooserWizardPage page1;
	private AddReaderWizardData data;

	public AddReaderWizard(EdgeServerConnection connection) {
		super();
		this.connection = connection;
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public void addPages() {
		data = new AddReaderWizardData();

		page1 = new ReaderInfoChooserWizardPage("ReaderInfoChooser", data,
				connection);
		addPage(page1);

		EditReaderInfoWizardPage page2 = new EditReaderInfoWizardPage(
				"EditReaderInfoWizardPage", data, connection);
		addPage(page2);

	}

	@Override
	public boolean performFinish() {
		try {
			connection.createRemoteReader(new ReaderInfoWrapper(data.widget
					.getXMLAsString(true)));
		} catch (CannotCreateRemoteReaderException e) {
			// TODO: display dialog box that says there was an error
		}
		return true;
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		page1.createControl(pageContainer);
	}

}

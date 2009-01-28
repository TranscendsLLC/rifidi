package org.rifidi.edge.client.connections.wizards.reader.pages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.rifidi.dynamicswtforms.ui.exceptions.DynamicSWTFormInvalidXMLException;
import org.rifidi.dynamicswtforms.ui.widgets.listeners.DynamicSWTWidgetListener;
import org.rifidi.dynamicswtforms.ui.widgets.standard.StandardDynamicSWTForm;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.ReaderPluginWrapper;
import org.rifidi.edge.core.rmi.client.sessionstub.valueobjects.ReaderInfoWrapper;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

public class EditReaderInfoWizardPage extends WizardPage implements
		DynamicSWTWidgetListener {

	private AddReaderWizardData wizardData = null;
	private RemoteReader remoteReader = null;
	private EdgeServerConnection connection = null;
	private Log logger = LogFactory.getLog(EditReaderInfoWizardPage.class);
	private Composite parent;

	public EditReaderInfoWizardPage(String pageName,
			EdgeServerConnection connection, AddReaderWizardData wizardData) {
		super(pageName);
		this.connection = connection;

	}

	public EditReaderInfoWizardPage(String pageName,
			AddReaderWizardData wizardData, EdgeServerConnection connection) {
		super(pageName);
		this.wizardData = wizardData;
		this.connection = connection;
	}

	public void setRemoteReader(RemoteReader remoteReader) {
		this.remoteReader = remoteReader;
	}

	public void setWizardData(AddReaderWizardData wizardData) {
		logger.debug("setting data");
		this.wizardData = wizardData;
		if (parent != null) {
			drawPage();
			parent.layout();
		}

	}

	private void drawPage() {
		setTitle("Reader Connection Wizard");

		ReaderPluginWrapper plugin;
		try {
			plugin = connection
					.getReaderPluginWrapper(wizardData.readerInfoClass);
			setTitle(plugin.getReaderName() + " Connection Wizard");
			setDescription(plugin.getDescription());
		} catch (RifidiReaderPluginXMLNotFoundException e1) {
			logger.error("Could not find reader plugin "
					+ wizardData.readerInfoClass);
			// TODO: draw an error pane and tell users to go back
		} catch (ServerUnavailable e1) {
			logger.error("Server Unavailable", e1);
			// TODO: display a box
		}

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);

		try {
			String annotation = connection
					.getReaderInfoAnnotation(wizardData.readerInfoClass);
			StandardDynamicSWTForm form = new StandardDynamicSWTForm(
					annotation, false);
			
			form.createControls(composite);

			if (remoteReader != null) {

				// fill widget
				ReaderInfoWrapper readerInfo;

				readerInfo = this.remoteReader.getReaderInfo();
				for (String name : readerInfo.getElementNames()) {
					form.setValue(name, readerInfo.getValue(name));
				}

			}

			
			form.addListner(this);
			this.wizardData.widget = form;

			setControl(composite);
			setPageComplete(false);
			validate();
		} catch (DynamicSWTFormInvalidXMLException e) {
			logger.error(e);
		} catch (RifidiReaderInfoNotFoundException e) {
			logger.error(e);
		}
	}

	@Override
	public void createControl(Composite parent) {
		logger.debug("creating control");
		this.parent = parent;
		drawPage();
	}

	@Override
	public void dataChanged(String newData) {
		validate();
	}

	private void validate() {
		String error = this.wizardData.widget.validate();
		if (error != null) {
			this.setErrorMessage(error);
			this.setPageComplete(false);
		} else {
			setErrorMessage(null);
			setPageComplete(true);
		}
	}

	@Override
	public void keyReleased() {
		validate();
	}

}

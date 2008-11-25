package org.rifidi.edge.client.connections.wizards.reader.pages;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.core.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

public class ReaderInfoChooserWizardPage extends WizardPage {

	private EdgeServerConnection connection;
	private Combo combo;
	private AddReaderWizardData wizardData;
	private Log logger = LogFactory.getLog(ReaderInfoChooserWizardPage.class);
	private HashMap<String, String> displayNameMap;

	public ReaderInfoChooserWizardPage(String pageName,
			AddReaderWizardData wizardData, EdgeServerConnection connection) {
		super(pageName);
		setPageInfo();
		this.connection = connection;
		this.wizardData = wizardData;
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		List<String> readerInfos = connection.getAvailableReaderPlugins();
		displayNameMap = new HashMap<String, String>();
		combo = new Combo(composite, SWT.NONE);
		combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();

			}

		});
		for (String s : readerInfos) {
			try {
				String displayName = connection.getReaderPluginWrapper(s)
						.getReaderName();
				displayNameMap.put(displayName, s);
				combo.add(displayName);
			} catch (RifidiReaderPluginXMLNotFoundException e) {
				logger.error(e);
			} catch (ServerUnavailable e) {
				logger.error(e);
			}

		}
		setControl(composite);
		setPageComplete(false);

	}

	private void setPageInfo() {
		setTitle("Reader Connection Wizard");
		setDescription("Please choose a reader type");
	}

	private void update() {
		wizardData.readerInfoClass = displayNameMap.get(combo.getItem(combo
				.getSelectionIndex()));
		logger.debug("choice is " + wizardData.readerInfoClass);
		((EditReaderInfoWizardPage) this.getNextPage())
				.setWizardData(wizardData);
		setPageComplete(true);
	}

}

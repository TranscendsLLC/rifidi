
package org.rifidi.edge.client.sal.wizards.newreader;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.rifidi.edge.client.model.sal.RemoteReaderFactory;

/**
 * A wizard page that allows a user to chose a new reader type to create
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReaderFactoryChooserPage extends WizardPage {

	/** The set of available factories */
	private Set<RemoteReaderFactory> readerFactories;
	/** The combo box to display */
	private Combo combo;
	/** A hasmap that maps display names to ReaderFactories */
	private HashMap<String, RemoteReaderFactory> displayNameMap;
	/** The data that is collected from the user */
	private NewReaderWizardData wizardData;
	/** A logger for this class */
	private static final Log logger = LogFactory
			.getLog(ReaderFactoryChooserPage.class);

	/**
	 * Constructor
	 * 
	 * @param pageName
	 *            The name of the page to display
	 * @param factories
	 *            The available factories
	 * @param data
	 *            The data that is collected from the user
	 */
	protected ReaderFactoryChooserPage(String pageName,
			Set<RemoteReaderFactory> factories, NewReaderWizardData data) {
		super(pageName);
		setTitle("New Reader Wizard");
		setDescription("Please choose a reader type");
		this.readerFactories = factories;
		this.displayNameMap = new HashMap<String, RemoteReaderFactory>();
		this.wizardData = data;
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
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
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
		for (RemoteReaderFactory factory : readerFactories) {

			String displayName = factory.getDisplayName();
			displayNameMap.put(displayName, factory);
			combo.add(displayName);

		}
		setControl(composite);
		setPageComplete(false);
	}

	/**
	 * Method called when a new type is selected.
	 */
	private void update() {
		wizardData.factory = displayNameMap.get(combo.getItem(combo
				.getSelectionIndex()));
		logger.debug("choice is " + wizardData.factory);
		((EditConnectionInfoPage) this.getNextPage()).setWizardData(wizardData);
		setPageComplete(true);
	}

}

/**
 * 
 */
package org.rifidi.edge.client.sal.wizards.newreader;

import java.util.HashSet;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.rifidi.edge.client.mbean.ui.MBeanInfoWidgetListener;
import org.rifidi.edge.client.mbean.ui.widgets.standard.StandardMBeanInfoComposite;

/**
 * This is a wizard page that allows the user to edit the connection information
 * about a new reader.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EditConnectionInfoPage extends WizardPage implements
		MBeanInfoWidgetListener {

	/** The composite to draw on */
	private Composite parent;
	/** The composite that is dynamically generated from the MBeanInfo */
	private StandardMBeanInfoComposite mbeanWidgetComposite;
	/** The data that is passed around between the wizard pages */
	private NewReaderWizardData wizardData;

	/**
	 * Constructor
	 * 
	 * @param pageName
	 *            The page name for this wizard
	 * @param wizardData
	 *            the data that is passed between the wizard pages to collect
	 *            information
	 */
	protected EditConnectionInfoPage(final String pageName,
			NewReaderWizardData wizardData) {
		super(pageName);
		this.wizardData = wizardData;
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
		this.parent = parent;
		if (wizardData != null) {
			draw();
		}
	}

	/**
	 * Set the wizard data. This method is necessary to tell the page to redraw
	 * when the wizarddata changes
	 * 
	 * @param wizardData
	 *            The object that collects information as the wizard collects it
	 */
	public void setWizardData(final NewReaderWizardData wizardData) {
		this.wizardData = wizardData;
		if (parent != null) {
			draw();
		}
	}

	/**
	 * Draw the page
	 */
	private void draw() {

		setTitle("New Reader Connection: "
				+ wizardData.factory.getDisplayName());
		setDescription(wizardData.factory.getDescription());
		assert (parent != null);
		assert (wizardData != null);

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);

		HashSet<String> includeStrings = new HashSet<String>();
		includeStrings.add("connection");
		mbeanWidgetComposite = new StandardMBeanInfoComposite(
				wizardData.factory.getMbeanInfo(), includeStrings, true, true);

		mbeanWidgetComposite.createControls(composite);

		mbeanWidgetComposite.addListner(this);

		setControl(composite);

		setPageComplete(false);
		validate();
	}

	/**
	 * Called from the MBeanWidgetComposite when a data value changes
	 * 
	 * @param dataChanged
	 *            - The data that was changed
	 */
	@Override
	public void dataChanged(String widgetName, String newData) {
		validate();

	}

	/**
	 * Called from the MBeanWidetCompsoite when a key is released
	 */
	@Override
	public void keyReleased(String widgetName) {
		validate();
	}

	/**
	 * Validate all controls and decide whether or not this page is complete
	 */
	private void validate() {

		String error = mbeanWidgetComposite.validate();
		if (error != null) {
			this.setErrorMessage(error);
			this.setPageComplete(false);
		} else {
			this.wizardData.attributes = mbeanWidgetComposite.getAttributes();
			setErrorMessage(null);
			setPageComplete(true);
		}
	}

}

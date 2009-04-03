/**
 * 
 */
package org.rifidi.edge.client.sal.wizards.newreader;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.rifidi.edge.client.model.sal.RemoteReaderFactory;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerTreeContentProvider;

/**
 * The wizard creating a new Reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class NewReaderWizard extends Wizard implements INewWizard {

	/** The first wizard page */
	private ReaderFactoryChooserPage page1;
	/** The second wizard pate */
	private EditConnectionInfoPage page2;
	/** The set of available factories */
	private Set<RemoteReaderFactory> readerFactories;
	/** The data that is passed between the pages */
	private NewReaderWizardData wizardData;
	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(NewReaderWizard.class);

	/**
	 * Constructor
	 * @param readerFactories The available factories
	 */
	public NewReaderWizard(Set<RemoteReaderFactory> readerFactories) {
		this.readerFactories = readerFactories;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		EdgeServerController controller = EdgeServerTreeContentProvider
				.getEdgeServerController();
		controller.createReader(wizardData.factory, wizardData.attributes);
		return true;
	}

	/**
	 * Override the can finish so that wizard cannot finish on first page
	 */
	@Override
	public boolean canFinish() {
		if (getContainer().getCurrentPage() == page1)
			return false;
		else
			return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		wizardData = new NewReaderWizardData();
		page1 = new ReaderFactoryChooserPage("ReaderFactoryChooser",
				readerFactories, wizardData);
		addPage(page1);
		page2 = new EditConnectionInfoPage("EditConnectionInfo", wizardData);
		addPage(page2);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Figure out how to get the readerFactories from the selection?

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPageControls(Composite pageContainer) {
		page1.createControl(pageContainer);
		
	}

}

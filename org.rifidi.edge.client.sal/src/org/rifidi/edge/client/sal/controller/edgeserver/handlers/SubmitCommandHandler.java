
package org.rifidi.edge.client.sal.controller.edgeserver.handlers;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.model.sal.RemoteSession;
import org.rifidi.edge.client.sal.controller.commands.CommandTreeContentProvider;
import org.rifidi.edge.client.sal.wizards.submitjob.SubmitJobWizard;

/**
 * This is a handler for submitting a command to the session. The selection must
 * be a RemoteSession
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class SubmitCommandHandler extends AbstractHandler implements IHandler2 {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IWorkbenchWindow window = HandlerUtil
					.getActiveWorkbenchWindow(event);
			ISelection sel = HandlerUtil.getCurrentSelection(event);
			Object obj = ((TreeSelection) sel).getFirstElement();
			RemoteSession session = (RemoteSession) obj;
			Set<RemoteCommandConfiguration> configurations = new HashSet<RemoteCommandConfiguration>();
			for (RemoteCommandConfiguration config : CommandTreeContentProvider
					.getController().getCommandConfigurations()) {
				if (config.getReaderFactoryID().equals(
						session.getReaderFactoryID())) {
					configurations.add(config);
				}
			}
			SubmitJobWizard wizard = new SubmitJobWizard(session,
					configurations);
			WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
			dialog.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

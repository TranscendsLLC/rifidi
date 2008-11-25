package org.rifidi.edge.client.connections.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.wizards.reader.AddReaderWizard;

public class AddNewReaderHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelectionChecked(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Object connection = selection.getFirstElement();
		if (connection instanceof EdgeServerConnection) {
			WizardDialog dialog = new WizardDialog(window.getShell(),
					new AddReaderWizard((EdgeServerConnection) connection));
			dialog.open();
			// sEdgeServerConnection)
			// connection).createRemoteReader(readerInfo);
		}
		return null;
	}

}

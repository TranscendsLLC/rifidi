package org.rifidi.edge.client.connections.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.views.EdgeServerConnectionView;
import org.rifidi.edge.client.connections.wizards.reader.EditReaderInfoWizard;

public class EditReaderHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelectionChecked(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Object remoteReader = selection.getFirstElement();
		if (remoteReader instanceof RemoteReader) {
			WizardDialog dialog = new WizardDialog(window.getShell(),
					new EditReaderInfoWizard((RemoteReader) remoteReader));
			dialog.open();
			// sEdgeServerConnection)
			// connection).createRemoteReader(readerInfo);
		}
		
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if(part instanceof EdgeServerConnectionView){
			((EdgeServerConnectionView)part).refresh();
		}
		
		return null;
	}

}

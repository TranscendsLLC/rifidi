package org.rifidi.edge.client.connections.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.views.EdgeServerConnectionView;

public class EnableReaderHandler extends AbstractHandler implements IHandler {

	//private Log logger = LogFactory.getLog(EnableReaderHandler.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelectionChecked(event);
		Object sel = selection.getFirstElement();
		if (sel instanceof RemoteReader) {
			((RemoteReader)sel).enable();
		}
		
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if(part instanceof EdgeServerConnectionView){
			((EdgeServerConnectionView)part).refresh();
		}
		
		return null;
	}

}

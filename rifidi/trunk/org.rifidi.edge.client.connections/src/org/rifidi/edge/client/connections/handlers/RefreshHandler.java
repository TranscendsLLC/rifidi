package org.rifidi.edge.client.connections.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.connections.views.EdgeServerConnectionView;

public class RefreshHandler extends AbstractHandler implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if(part instanceof EdgeServerConnectionView){
			((EdgeServerConnectionView)part).refresh();
		}
		return null;
	}

}

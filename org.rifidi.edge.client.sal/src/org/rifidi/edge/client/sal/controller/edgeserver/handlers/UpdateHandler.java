/**
 * 
 */
package org.rifidi.edge.client.sal.controller.edgeserver.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerTreeContentProvider;

/**
 * The handler method for updating the edge server. Should only be able to be
 * executed if the RemoteEdgeServer is in the connected state
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class UpdateHandler extends AbstractHandler implements IHandler2 {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		EdgeServerTreeContentProvider.getEdgeServerController().update();
		return null;
	}
}

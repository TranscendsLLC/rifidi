/**
 * 
 */
package org.rifidi.edge.client.model.sal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;

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
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelectionChecked(event);
		Object sel = selection.getFirstElement();
		if (sel instanceof RemoteEdgeServer) {

			((RemoteEdgeServer) sel).update();

		}
		return null;
	}

}

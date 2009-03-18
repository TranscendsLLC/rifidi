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
 * The Handler Method for the Disconnect Command. Should only be executed if the
 * RemoteEdgeServer is in the connected state
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class DisconnectHandler extends AbstractHandler implements IHandler2 {

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

			((RemoteEdgeServer) sel).disconnect();

		}
		return null;
	}
}

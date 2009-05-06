/**
 * 
 */
package org.rifidi.edge.client.sal.controller.edgeserver.handlers;
//TODO: Comments
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerTreeContentProvider;

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
		EdgeServerTreeContentProvider.getEdgeServerController().disconnect();
		return null;
	}
}

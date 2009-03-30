/**
 * 
 */
package org.rifidi.edge.client.sal.controller.edgeserver.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerTreeContentProvider;

/**
 * A handler for persisting the configuration information on the server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SaveConfigurationHandler extends AbstractHandler implements
		IHandler2 {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		EdgeServerTreeContentProvider.getEdgeServerController()
				.saveConfiguration();
		return null;
	}

}

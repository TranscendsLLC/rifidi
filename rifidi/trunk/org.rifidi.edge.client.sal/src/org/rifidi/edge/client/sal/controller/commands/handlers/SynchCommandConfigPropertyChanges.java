
package org.rifidi.edge.client.sal.controller.commands.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.sal.controller.commands.CommandController;
import org.rifidi.edge.client.sal.controller.commands.CommandTreeContentProvider;

/**
 * @author kyle
 * 
 */
public class SynchCommandConfigPropertyChanges extends AbstractHandler
		implements IHandler2 {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getCurrentSelection(event);
		CommandController controller = CommandTreeContentProvider
				.getController();
		Object obj = ((TreeSelection) sel).getFirstElement();
		controller.synchPropertyChanges(((RemoteCommandConfiguration) obj)
				.getID());
		return null;
	}

}

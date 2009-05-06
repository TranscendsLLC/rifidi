/**
 * 
 */
package org.rifidi.edge.client.sal.controller.edgeserver.handlers;
//TODO: Comments
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerTreeContentProvider;

/**
 * Create a new session on a reader. Should only be called when edge server is
 * connected and the selected object is a RemoteReader
 * 
 * @author Kyle Neumeier -kyle@pramari.com
 * 
 */
public class CreateSessionHandler extends AbstractHandler implements IHandler2 {

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
		EdgeServerController controller = EdgeServerTreeContentProvider
				.getEdgeServerController();

		Object obj = ((TreeSelection) sel).getFirstElement();
		controller.createSession(((RemoteReader) obj).getID());

		return null;
	}

}

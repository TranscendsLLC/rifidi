/**
 * 
 */
package org.rifidi.edge.client.sal.controller.edgeserver.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerTreeContentProvider;
import org.rifidi.edge.client.sal.views.tags.TagView;

/**
 * @author kyle
 * 
 */
public class OpenTagViewHandler extends AbstractHandler implements IHandler2 {

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
		RemoteReader reader = (RemoteReader) obj;

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);

		try {
			IViewPart part = window.getActivePage().showView(
					TagView.ID, reader.getID(),
					IWorkbenchPage.VIEW_VISIBLE);
			TagView view = (TagView) part;
			view.setReader(reader);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

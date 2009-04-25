/**
 * 
 */
package org.rifidi.edge.client.ale.ecspecview.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec;
import org.rifidi.edge.client.ale.ecspecview.views.ECSpecEditorView;

/**
 * @author kyle
 * 
 */
public class NewECSpec extends AbstractHandler implements IHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		String generatedName = "ECSpec";
		ECSpecEditorView view;
		try {
			view = (ECSpecEditorView) window.getActivePage().showView(
					ECSpecEditorView.ID, generatedName,
					IWorkbenchPage.VIEW_VISIBLE);
			view.initSpecView(generatedName, new ECSpec());
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

}

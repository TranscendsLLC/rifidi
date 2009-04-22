/**
 * 
 */
package org.rifidi.edge.client.ale.ecspecview.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec;
import org.rifidi.edge.client.ale.ecspecview.model.ECSpecDecorator;
import org.rifidi.edge.client.ale.ecspecview.views.ALEEditorView;

/**
 * @author kyle
 * 
 */
public class DisplayECSpecHandler extends AbstractHandler implements IHandler {

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
		ISelection sel = window.getSelectionService().getSelection();
		Object element = ((IStructuredSelection) sel).getFirstElement();
		ECSpecDecorator ecspec = (ECSpecDecorator) element;

		ALEEditorView view;
		try {
			view = (ALEEditorView) window.getActivePage().showView(
					ALEEditorView.ID, ecspec.getName(),
					IWorkbenchPage.VIEW_VISIBLE);
			view.initSpecView(ecspec.getName(), ecspec.getSpec());
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}


package org.rifidi.edge.client.ale.ecspecview.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.rifidi.edge.client.ale.ecspecview.model.ECSpecDecorator;
import org.rifidi.edge.client.ale.ecspecview.views.ECSpecEditorView;

/**
 * TODO: Class level comment.  
 * 
 * @author kyle
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
		if (event.getApplicationContext() instanceof IEvaluationContext) {
			for (Object selected : (List<?>) ((IEvaluationContext) event
					.getApplicationContext()).getDefaultVariable()) {
				if (selected instanceof ECSpecDecorator) {
					ECSpecDecorator ecspec = (ECSpecDecorator) selected;

					ECSpecEditorView view;
					try {
						view = (ECSpecEditorView) PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage()
								.showView(ECSpecEditorView.ID,
										ecspec.getName(),
										IWorkbenchPage.VIEW_VISIBLE);
						view.initSpecView(ecspec.getName(), ecspec.getSpec());
					} catch (PartInitException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return null;
	}

}

/**
 * 
 */
package org.rifidi.edge.client.ale.ecspecview.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.rifidi.edge.client.reports.ReportReceiverViewPart;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ClearReportViewHandler extends AbstractHandler implements IHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		for (IViewReference ref : PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getViewReferences()) {
			if (ref.getId().equals(
					"org.rifidi.edge.client.ale.ReportReceiverView")) {
				ReportReceiverViewPart vi = (ReportReceiverViewPart) ref
						.getView(true);
				vi.clear();
			}
		}

		return null;
	}

}

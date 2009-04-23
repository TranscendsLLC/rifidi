/**
 * 
 */
package org.rifidi.edge.client.alelr.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.InUseExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.ecspecview.Activator;
import org.rifidi.edge.client.alelr.ALELRService;
import org.rifidi.edge.client.alelr.decorators.LRSpecDecorator;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class DeleteHandler extends AbstractHandler {
	
	private ALELRService service;
	
	public DeleteHandler() {
		super();
		service = Activator.getDefault().getAleLrService();
	}

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
			// collect the selected tags and pair them with their parents
			for (Object selected : (List<?>) ((IEvaluationContext) event
					.getApplicationContext()).getDefaultVariable()) {
				if (selected instanceof LRSpecDecorator) {
					try {
						service.deleteReader((LRSpecDecorator) selected);
					} catch (InUseExceptionResponse e) {
						MessageBox messageBox = new MessageBox(PlatformUI
								.getWorkbench().getActiveWorkbenchWindow().getShell(),
								SWT.ICON_ERROR | SWT.OK);
						messageBox.setMessage(e.toString());
						messageBox.open();
					} catch (NoSuchNameExceptionResponse e) {
						MessageBox messageBox = new MessageBox(PlatformUI
								.getWorkbench().getActiveWorkbenchWindow().getShell(),
								SWT.ICON_ERROR | SWT.OK);
						messageBox.setMessage(e.toString());
						messageBox.open();
					}
				}
			}
		}
		return null;
	}

}

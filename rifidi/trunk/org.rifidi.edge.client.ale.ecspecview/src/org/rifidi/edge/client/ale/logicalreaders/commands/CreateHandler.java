/**
 * 
 */
package org.rifidi.edge.client.ale.logicalreaders.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.DuplicateNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ValidationExceptionResponse;
import org.rifidi.edge.client.ale.ecspecview.Activator;
import org.rifidi.edge.client.ale.logicalreaders.ALELRService;
import org.rifidi.edge.client.ale.logicalreaders.decorators.LRSpecDecorator;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class CreateHandler extends AbstractHandler {

	private ALELRService service;

	public CreateHandler() {
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
		// holds all readers that were selected
		List<String> readers = new ArrayList<String>();
		// holds a name suggestion <reader1>_<reader2>...
		StringBuilder suggestedName = new StringBuilder();
		if (event.getApplicationContext() instanceof IEvaluationContext) {
			// collect the selected readers
			for (Object selected : (List<?>) ((IEvaluationContext) event
					.getApplicationContext()).getDefaultVariable()) {
				readers.add(((LRSpecDecorator) selected).getName());
				// create a name suggestion
				suggestedName.append(((LRSpecDecorator) selected).getName()
						+ "_");
			}
		}
		// get rid of the trailing _
		suggestedName.deleteCharAt(suggestedName.length() - 1);
		InputDialog dialog = new InputDialog(Display.getDefault()
				.getActiveShell(), "Reader name",
				"Please give a name for the new composite reader",
				suggestedName.toString(), null);
		// wait for the result of the window input
		dialog.setBlockOnOpen(true);
		if (dialog.open() == InputDialog.OK) {
			try {
				service.createReader(dialog.getValue(), readers);
			} catch (DuplicateNameExceptionResponse e) {
				MessageBox messageBox = new MessageBox(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						SWT.ICON_ERROR | SWT.OK);
				messageBox.setMessage(e.toString());
				messageBox.open();
			} catch (ValidationExceptionResponse e) {
				MessageBox messageBox = new MessageBox(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						SWT.ICON_ERROR | SWT.OK);
				messageBox.setMessage(e.toString());
				messageBox.open();
			}

		}
		return null;
	}

}

/**
 * 
 */
package org.rifidi.edge.ale.lr.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;
import org.rifidi.edge.ale.lr.LRTreeContentProvider;
import org.rifidi.edge.ale.lr.decorators.LRSpecDecorator;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class CreateHandler extends AbstractHandler {

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
		LRTreeContentProvider provider = null;
		if (event.getApplicationContext() instanceof IEvaluationContext) {
			// collect the selected readers
			for (Object selected : (List<?>) ((IEvaluationContext) event
					.getApplicationContext()).getDefaultVariable()) {
				readers.add(((LRSpecDecorator) selected).getName());
				// create a name suggestion
				suggestedName.append(((LRSpecDecorator) selected).getName()
						+ "_");
				provider = ((LRSpecDecorator) selected)
						.getLrTreeContentProvider();
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
			provider.createReader(dialog.getValue(), readers);
		}
		return null;
	}

}

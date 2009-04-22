/**
 * 
 */
package org.rifidi.edge.client.alelr.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.rifidi.edge.client.alelr.decorators.LRSpecDecorator;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class DeleteHandler extends AbstractHandler {

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
					((LRSpecDecorator) selected).getLrTreeContentProvider()
							.deleteReader((LRSpecDecorator) selected);
				}
			}
		}
		return null;
	}

}

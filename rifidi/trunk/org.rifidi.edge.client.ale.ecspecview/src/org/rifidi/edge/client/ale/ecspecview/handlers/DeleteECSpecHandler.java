/**
 * 
 */
package org.rifidi.edge.client.ale.ecspecview.handlers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.ecspecview.Activator;
import org.rifidi.edge.client.ale.ecspecview.model.ECSpecDecorator;
import org.rifidi.edge.client.ale.logicalreaders.ALEService;

/**
 * This class represents a command to delete an EC spec.  
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class DeleteECSpecHandler extends AbstractHandler {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(DeleteECSpecHandler.class);
	/** ALE management service. */
	private ALEService service;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	public DeleteECSpecHandler() {
		super();
		service = Activator.getDefault().getAleService();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (event.getApplicationContext() instanceof IEvaluationContext) {
			for (Object selected : (List<?>) ((IEvaluationContext) event
					.getApplicationContext()).getDefaultVariable()) {
				if (selected instanceof ECSpecDecorator) {
					try {
						service.deleteECSpec(((ECSpecDecorator) selected).getName());
					} catch (NoSuchNameExceptionResponse e) {
						logger.warn(e);
					}
				}
			}
		}
		return null;
	}
}

/**
 * 
 */
package org.rifidi.edge.client.monitoring.tagview.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.rifidi.edge.client.monitoring.tagview.model.TagModelProviderSingleton;

/**
 * This class is the handler used to clear the tag model
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ClearTagHandlers extends AbstractHandler implements IHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		TagModelProviderSingleton.getInstance().getTags().clear();
		return null;
	}

}

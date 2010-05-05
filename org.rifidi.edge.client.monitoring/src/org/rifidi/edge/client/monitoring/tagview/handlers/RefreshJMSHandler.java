/**
 * 
 */
package org.rifidi.edge.client.monitoring.tagview.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.rifidi.edge.client.monitoring.Activator;

/**
 * Handler method to refresh JMS connection for tag destination.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RefreshJMSHandler extends AbstractHandler implements IHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Activator.getDefault().getTagMonitor().refresh();

			}
		});
		t.start();
		return null;
	}

}

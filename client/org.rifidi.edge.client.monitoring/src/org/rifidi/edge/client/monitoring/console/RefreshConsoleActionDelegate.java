package org.rifidi.edge.client.monitoring.console;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.rifidi.edge.client.monitoring.Activator;

/**
 * This is the action delegate for plugging into the eclipse console. It
 * provides the functionality to refresh the JMS connection.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RefreshConsoleActionDelegate implements IViewActionDelegate {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	@Override
	public void init(IViewPart view) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				Activator.getDefault().getConsoleMonitor().refresh();

			}
		});
		t.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

}

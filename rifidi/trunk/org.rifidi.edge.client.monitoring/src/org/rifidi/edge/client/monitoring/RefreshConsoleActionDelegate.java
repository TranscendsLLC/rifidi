package org.rifidi.edge.client.monitoring;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class RefreshConsoleActionDelegate implements IViewActionDelegate {

	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(IAction action) {
		Activator.getDefault().getMonitor().refresh();

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}

package org.rifidi.edge.client.jmsconsole.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.connections.edgeserver.RemoteReader;
import org.rifidi.edge.client.jmsconsole.jmslistener.JMSListener;

public class OpenJMSConsoleHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelectionChecked(event);
		Object sel = selection.getFirstElement();
		if (sel instanceof RemoteReader) {
			RemoteReader reader = (RemoteReader) sel;
			// TODO use profiling tool to check if this instance get destroyed
			new JMSListener(reader);
		}
		return null;
	}

}

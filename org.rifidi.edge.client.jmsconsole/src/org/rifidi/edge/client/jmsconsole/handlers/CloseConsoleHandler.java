package org.rifidi.edge.client.jmsconsole.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.console.ConsoleView;

public class CloseConsoleHandler extends AbstractHandler implements IHandler {

	private Log logger = LogFactory.getLog(CloseConsoleHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof ConsoleView) {
			ConsoleView consoleView = (ConsoleView) part;
			IConsole iconsole = consoleView.getConsole();
			//ConsolePlugin.getDefault().
			ConsolePlugin.getDefault().getConsoleManager().removeConsoles(
					new IConsole[] { iconsole });
		}
		return null;
	}
}

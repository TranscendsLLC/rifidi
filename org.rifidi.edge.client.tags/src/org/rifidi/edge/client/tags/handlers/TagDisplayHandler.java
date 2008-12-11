package org.rifidi.edge.client.tags.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.tags.views.reader.TagView;
import org.rifidi.edge.client.tags.views.server.TagServerView;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class TagDisplayHandler extends AbstractHandler {
	
	static private Log logger = LogFactory.getLog(TagDisplayHandler.class);
	/**
	 * The constructor.
	 */
	public TagDisplayHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		logger.debug("Executing...");
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
		.getCurrentSelectionChecked(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		if (selection.getFirstElement() instanceof RemoteReader) {
			try {
				RemoteReader connection = (RemoteReader) selection.getFirstElement();
				
				TagView view = (TagView) window.getActivePage().showView(TagView.class.getName(), connection.getDescription().replace(":","&colon;"),
						IWorkbenchPage.VIEW_ACTIVATE);
				view.initTagView(connection);
			} catch (PartInitException e1) {
				logger.debug("Error. " , e1);
				throw new ExecutionException("Could not create view.", e1);
			} 
		} else if (selection.getFirstElement() instanceof EdgeServerConnection) {
			try {
				EdgeServerConnection connection = (EdgeServerConnection) selection.getFirstElement();
				
				TagServerView view = (TagServerView) window.getActivePage().showView(TagServerView.class.getName(), connection.toString().replace(":","&colon;"),
						IWorkbenchPage.VIEW_ACTIVATE);
				
				view.initTagView( connection);
			} catch (PartInitException e1) {
				logger.debug("Error. " , e1);
				throw new ExecutionException("Could not create view.", e1);
			} 
		} else {
				throw new ExecutionException("Selected Element not instanceof RemoteReader or EdgeServerConnection.");
		}
		

		
		return null;
	}
}

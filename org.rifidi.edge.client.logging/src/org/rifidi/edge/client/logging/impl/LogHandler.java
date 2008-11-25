package org.rifidi.edge.client.logging.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;
import org.rifidi.edge.client.connections.edgeserver.RemoteReader;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class LogHandler extends AbstractHandler implements IHandler,
		IElementUpdater {
	private static Log logger = LogFactory.getLog(LogHandler.class);

	private static final String TOGGLE_ID = "org.rifidi.edge.client.logging.commands.Loging.toggle";

	private boolean checked = false;

	private TagLogger tagLogger;
	
	public LogHandler(){
		ServiceRegistry.getInstance().service(this);
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Figure out why this is not working....
		
			logger.debug("Executing....");
			IStructuredSelection selection = (IStructuredSelection) HandlerUtil
					.getCurrentSelectionChecked(event);
			IWorkbenchWindow window = HandlerUtil
					.getActiveWorkbenchWindow(event);
			if (!(selection.getFirstElement() instanceof RemoteReader))
				throw new ExecutionException(
						"Selected Element not instanceof RemoteReader.");

			RemoteReader reader = (RemoteReader) selection.getFirstElement();
			
			
			if (checked) {
				reader.addListener(tagLogger);
				logger.debug("Addeed reader " + reader.getDescription() + " to logging.");
			} else {
				reader.removeListener(tagLogger);
				logger.debug("Removed reader " + reader.getDescription() + " from logging.");
			}
			
			
//			ICommandService service = (ICommandService) HandlerUtil
//					.getActiveWorkbenchWindowChecked(event).getService(
//							ICommandService.class);
//			
//
//			service.refreshElements(event.getCommand().getId(), null);	
		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
//		// TODO Auto-generated method stub
//		String contextId = (String) parameters.get(TOGGLE_ID);
//		if(element != null && contextId != null)
//			element.setChecked(contextId.equals("true"));
//		else
//			logger.error("I think contextId = null");
		if(checked)
		{
			checked = false;
		}else
		{
			checked = true;
		}
		element.setChecked(!checked);
		
		//logger.debug(element.getClass().getName());
	}

	@Inject
	public void setTagLogger(TagLogger tagLogger){
		this.tagLogger = tagLogger;
	}
}

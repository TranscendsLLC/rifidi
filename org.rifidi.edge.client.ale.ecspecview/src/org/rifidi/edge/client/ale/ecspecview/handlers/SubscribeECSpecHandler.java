/**
 * 
 */
package org.rifidi.edge.client.ale.ecspecview.handlers;
//TODO: Comments
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.InvalidURIExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.ecspecview.Activator;
import org.rifidi.edge.client.ale.ecspecview.model.ECSpecDecorator;
import org.rifidi.edge.client.ale.logicalreaders.ALEService;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class SubscribeECSpecHandler extends AbstractHandler {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(DeleteECSpecHandler.class);
	/** ALE management service. */
	private ALEService service;

	public SubscribeECSpecHandler() {
		super();
		service = Activator.getDefault().getAleService();
	}

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
			for (Object selected : (List<?>) ((IEvaluationContext) event
					.getApplicationContext()).getDefaultVariable()) {
				if (selected instanceof ECSpecDecorator) {
					try {
						service.subscribeECSpec(((ECSpecDecorator) selected)
								.getName(), Activator.getDefault().getPreferenceStore()
								.getString(Activator.REPORT_RECEIVER_ADR));
					} catch (NoSuchNameExceptionResponse e) {
						logger.warn(e);
					} catch (InvalidURIExceptionResponse e) {
						MessageBox messageBox = new MessageBox(PlatformUI
								.getWorkbench().getActiveWorkbenchWindow()
								.getShell(), SWT.ICON_ERROR | SWT.OK);
						messageBox.setMessage(e.toString());
						messageBox.open();
					} catch (DuplicateSubscriptionExceptionResponse e) {
						MessageBox messageBox = new MessageBox(PlatformUI
								.getWorkbench().getActiveWorkbenchWindow()
								.getShell(), SWT.ICON_ERROR | SWT.OK);
						messageBox.setMessage(e.toString());
						messageBox.open();
					}
				}
			}
		}
		return null;
	}

}

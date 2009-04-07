/* 
 *  DeleteRemoteEcSpecHandler.java
 *  Created:	Apr 1, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.treeview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.treeview.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Undefine;
import org.rifidi.edge.client.ale.connection.service.ConnectionService;
import org.rifidi.edge.client.ale.treeview.util.ConnectionWrapper.ConnectionWrapper;
import org.rifidi.edge.client.ale.treeview.views.AleTreeView;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class DeleteRemoteEcSpecHandler extends AbstractHandler implements
		IHandler {
	private ConnectionService conSvc = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
//		AleTreeView view = null;
//		view = (AleTreeView) PlatformUI
//				.getWorkbench()
//				.getActiveWorkbenchWindow()
//				.getActivePage()
//				.findView(
//						"org.rifidi.edge.client.ale.treeview.views.AleTreeView");
		ISelection sel = HandlerUtil.getCurrentSelectionChecked(event);
	
//		if (view != null) {
//			sel = view.getViewer().getSelection();
//
//		}
		TreeViewer viewer =(TreeViewer)HandlerUtil.getActivePartChecked(event).getSite().getSelectionProvider();
		

//		if (sel != null) {
		Object spec = ((IStructuredSelection) sel).getFirstElement();
//		}

		if (spec instanceof String) {
			ConnectionWrapper conWrap = new ConnectionWrapper();
			conSvc = conWrap.getConnectionService();
			Undefine parms = new Undefine();
			parms.setSpecName(spec.toString());
			try {
				conSvc.getAleServicePortType().undefine(parms);
				viewer.refresh();
			} catch (ImplementationExceptionResponse e) {
				throw new ExecutionException("Implementation Exception\n"
						+ e.getMessage());
			} catch (NoSuchNameExceptionResponse e) {
				throw new ExecutionException("No Such Name Exception\n"
						+ e.getMessage());
			} catch (SecurityExceptionResponse e) {
				throw new ExecutionException("Security Exception\n"
						+ e.getMessage());
			}

		}
		return null;
	}

}

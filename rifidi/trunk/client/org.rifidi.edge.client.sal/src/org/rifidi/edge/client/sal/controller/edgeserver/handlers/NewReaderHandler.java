/*
 * NewReaderHandler.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */

package org.rifidi.edge.client.sal.controller.edgeserver.handlers;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.model.sal.RemoteReaderFactory;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerTreeContentProvider;
import org.rifidi.edge.client.sal.wizards.newreader.NewReaderWizard;

/**
 * Create a new reader. Should only be called when the edge server is in
 * connected state and selection is RemoteEdgeServer
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class NewReaderHandler extends AbstractHandler implements IHandler2 {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Set<RemoteReaderFactory> factories = EdgeServerTreeContentProvider
				.getEdgeServerController().getReaderfactories();
		NewReaderWizard wizard = new NewReaderWizard(factories);
		WizardDialog wizardDialog = new WizardDialog(window.getShell(), wizard);
		wizardDialog.open();
		return null;
	}

}

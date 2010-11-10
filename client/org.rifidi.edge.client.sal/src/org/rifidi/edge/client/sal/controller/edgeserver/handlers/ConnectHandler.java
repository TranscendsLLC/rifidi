/*
 * ConnectHandler.java
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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerTreeContentProvider;

/**
 * The Handler method for connecting to an EdgeServer. It should only be able to
 * happen if the Edge Server is in the disconnected state
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ConnectHandler extends AbstractHandler implements IHandler2 {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		EdgeServerTreeContentProvider.getEdgeServerController().connect();
		return null;
	}

}

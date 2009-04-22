/* 
 *  SubscribeToSpec.java
 *  Created:	Apr 22, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.editor
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.ale.models.ecspec.RemoteSpecModelWrapper;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 *
 */
public class SubscribeToSpec extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getCurrentSelectionChecked(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Object spec = ((TreeSelection) sel).getFirstElement();

		RemoteSpecModelWrapper wrapper = (RemoteSpecModelWrapper) spec;
		wrapper.subscribe("http://localhost:10001");
		return null;
	}

}

/* 
 *  DefineRemoteEcSpec.java
 *  Created:	Apr 16, 2009
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.ale.models.ecspec.EcSpecModelWrapper;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 *
 */
public class DefineRemoteEcSpec extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getCurrentSelectionChecked(event);
		
		Object spec = ((IStructuredSelection) sel).getFirstElement();

		if (spec instanceof EcSpecModelWrapper) {
			((EcSpecModelWrapper) spec).define();
		}
		return null;
	}

}

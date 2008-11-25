package org.rifidi.edge.client.tags.views.server;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TagTableServerContentProvider implements IStructuredContentProvider {
	static private Log logger = LogFactory.getLog(TagTableServerLabelProvider.class);
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		
		// TODO Auto-generated method stub
		return ((Collection<?>) inputElement).toArray() ;
	}

}

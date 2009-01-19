/* 
 *  TagTableContentProvider.java
 *  Created:	Jan 16, 2009
 *  Project:	RiFidi org.rifidi.edge.client.tagview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.tagview.utils;

import java.util.Collection;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class TagTableContentProvider implements IStructuredContentProvider, IListChangeListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		System.out.println(inputElement);
		return ((Collection<?>) inputElement).toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		System.out.println(viewer.toString() + oldInput.toString()
				+ newInput.toString());

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.observable.list.IListChangeListener#handleListChange(org.eclipse.core.databinding.observable.list.ListChangeEvent)
	 */
	@Override
	public void handleListChange(ListChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}

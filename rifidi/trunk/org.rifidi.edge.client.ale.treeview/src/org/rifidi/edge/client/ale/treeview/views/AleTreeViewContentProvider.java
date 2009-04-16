/* 
 *  AleTreeViewContentProvider.java
 *  Created:	Mar 12, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.treeview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.treeview.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.ale.models.alelrserviceporttype.AleLrServicePortTypeWrapper;
import org.rifidi.edge.client.ale.models.aleserviceporttype.AleServicePortTypeWrapper;
import org.rifidi.edge.client.ale.models.ecspec.EcSpecModelWrapper;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AleTreeViewContentProvider implements ITreeContentProvider {

	@SuppressWarnings("unused")
	private Log logger = null;
	private AleTreeView view = null;
	private AleServicePortTypeWrapper aWrapper = null;
	private AleLrServicePortTypeWrapper lWrapper = null;

	/**
	 * @param aleTreeView
	 * 
	 */
	public AleTreeViewContentProvider(AleTreeView aleTreeView) {
		logger = LogFactory.getLog(AleTreeViewContentProvider.class);
		this.view = aleTreeView;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof AleServicePortTypeWrapper) {
			return ((AleServicePortTypeWrapper) parentElement).getEcSpecs();
		}

		// if(parentElement instanceof EcSpecModelWrapper){
		// return new Object[0]; //return info
		// }

		return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof EcSpecModelWrapper) {
			return ((EcSpecModelWrapper) element).getParent();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof TreeNode) {
			return true;
		}
		if (element instanceof AleServicePortTypeWrapper) {
			if (((AleServicePortTypeWrapper) element).isConnected())
				return true;
			else
				return false;
		}

		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TreeNode) {
			Object[] obj = new Object[2];
			if(aWrapper==null){
			aWrapper = new AleServicePortTypeWrapper();
			aWrapper.addConnectionChangeListener(this.view);
			aWrapper.addSetChangeListener(this.view);
			}
			if(lWrapper==null){
//				TODO: same as above for logical readers
			}
			obj[0] = aWrapper;

			return obj;
		} else if (hasChildren(inputElement))
			return getChildren(inputElement);
		else {
			return new Object[0];
		}
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
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

}

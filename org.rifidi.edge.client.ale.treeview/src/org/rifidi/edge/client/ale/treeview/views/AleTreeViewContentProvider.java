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

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.connection.service.ConnectionService;
import org.rifidi.edge.client.ale.treeview.util.ConnectionWrapper.ConnectionWrapper;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AleTreeViewContentProvider implements ITreeContentProvider {

	private Log logger = null;
	// private BundleContext context = null;
	// private ServiceReference svcRef = null;
	private ConnectionService conSvc = null;

	/**
	 * 
	 */
	public AleTreeViewContentProvider() {
		logger = LogFactory.getLog(AleTreeViewContentProvider.class);
		ConnectionWrapper conWrap = new ConnectionWrapper();
		conSvc = conWrap.getConnectionService();
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

		if (parentElement instanceof TreeNode) {
			TreeNode node = (TreeNode) parentElement;
			if (node.getValue().toString().startsWith("Remote EcSpecs (")) {
				// conSvc.getAleServicePortType();
				try {
					ArrayList<String> strings = (ArrayList<String>) conSvc
							.getAleServicePortType().getECSpecNames(
									new EmptyParms()).getString();
					return strings.toArray();

				} catch (Exception e) {
					logger.error(e.getCause() + ":\n" + e.getMessage());
					return new Object[0];
				}
			}
			if (node.getValue().toString().startsWith("Logical Readers (")) {
				try {
					ArrayList<String> strings = (ArrayList<String>) conSvc
							.getAleLrServicePortType()
							.getLogicalReaderNames(
									new org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.EmptyParms())
							.getString();
					return strings.toArray();

				} catch (Exception e) {
					logger.error(e.getCause() + ":\n" + e.getMessage());
					return new Object[0];
				}
			}
			return new Object[0];

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	@Override
	public Object getParent(Object arg0) {

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
		if (inputElement instanceof TreeNode
				&& ((TreeNode) inputElement).getValue().toString().isEmpty()) {
			Object[] obj = new Object[3];
			try {
				obj[0] = new TreeNode("Remote EcSpecs ("
						+ conSvc.getAleServicePortType().getECSpecNames(
								new EmptyParms()).getString().size() + ")");
				obj[1] = new TreeNode(
						"Logical Readers ("
								+ conSvc
										.getAleLrServicePortType()
										.getLogicalReaderNames(
												new org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.EmptyParms())
										.getString().size()+")");
				obj[2] = new TreeNode("Local EcSpecs (0)");
			} catch (ImplementationExceptionResponse e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityExceptionResponse e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.SecurityExceptionResponse e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ImplementationExceptionResponse e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

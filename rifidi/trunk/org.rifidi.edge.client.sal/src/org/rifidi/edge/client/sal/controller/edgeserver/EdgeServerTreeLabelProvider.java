/**
 * 
 */
package org.rifidi.edge.client.sal.controller.edgeserver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.sal.SALPluginActivator;

/**
 * @author kyle
 * 
 */
public class EdgeServerTreeLabelProvider implements ILabelProvider {

	private static final Log logger = LogFactory
			.getLog(EdgeServerTreeLabelProvider.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof RemoteEdgeServer) {
			RemoteEdgeServer server = (RemoteEdgeServer) element;
			switch (server.getState()) {
			case CONNECTED:
				return SALPluginActivator.getImageDescriptor(
						"icons/connect.png").createImage();
			case DISCONNECTED:
				return SALPluginActivator.getImageDescriptor(
						"icons/disconnect.png").createImage();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof RemoteEdgeServer) {
			return "Edge Server";
		}
		if (element instanceof RemoteReader) {
			return "Reader: " + ((RemoteReader) element).getID();
		} else
			return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.
	 * jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang
	 * .Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse
	 * .jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}

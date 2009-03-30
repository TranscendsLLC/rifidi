/**
 * 
 */
package org.rifidi.edge.client.sal.controller.edgeserver;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.model.sal.RemoteSession;
import org.rifidi.edge.client.sal.SALPluginActivator;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EdgeServerTreeLabelProvider implements ILabelProvider {

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
						"icons/server_add.png").createImage();
			case DISCONNECTED:
				return SALPluginActivator.getImageDescriptor(
						"icons/server_delete.png").createImage();
			}
		} else if (element instanceof RemoteReader) {
			return SALPluginActivator.getImageDescriptor(
					"icons/reader-16x16.png").createImage();
		} else if (element instanceof RemoteSession) {
			RemoteSession session = (RemoteSession) element;
			switch (session.getStateOfSession()) {
			case CLOSED:
				return SALPluginActivator.getImageDescriptor(
						"icons/flag_red.png").createImage();
			case CONNECTING:
				return SALPluginActivator.getImageDescriptor(
						"icons/flag_yellow.png").createImage();
			case CREATED:
				return SALPluginActivator.getImageDescriptor(
						"icons/flag_red.png").createImage();
			case FAIL:
				return SALPluginActivator.getImageDescriptor(
						"icons/flag_red.png").createImage();
			case LOGGINGIN:
				return SALPluginActivator.getImageDescriptor(
						"icons/flag_yellow.png").createImage();
			case PROCESSING:
				return SALPluginActivator.getImageDescriptor(
						"icons/flag_green.png").createImage();
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
			return ((RemoteReader) element).getID();
		}
		if (element instanceof RemoteSession) {
			return ((RemoteSession) element).getSessionID();
		}
		return element.toString();
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

/**
 * 
 */
package org.rifidi.edge.client.sal.controller.commands;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.rifidi.edge.client.model.sal.RemoteCommandConfigFactory;
import org.rifidi.edge.client.model.sal.RemoteCommandConfigType;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.sal.SALPluginActivator;

/**
 * @author kyle
 * 
 */
public class CommandTreeLabelProvider implements ILabelProvider {

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
						"icons/server-green.png").createImage();
			case DISCONNECTED:
				return SALPluginActivator.getImageDescriptor(
						"icons/server-red.png").createImage();
			}
		}
		if (element instanceof RemoteCommandConfigFactory) {
			return SALPluginActivator
					.getImageDescriptor("icons/reader-cog.png").createImage();
		}
		if (element instanceof RemoteCommandConfigType) {
			return SALPluginActivator
					.getImageDescriptor("icons/folder.png").createImage();
		}
		if (element instanceof RemoteCommandConfiguration) {
			return SALPluginActivator.getImageDescriptor("icons/cog.png")
					.createImage();
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
		} else if (element instanceof RemoteCommandConfigFactory) {
			return ((RemoteCommandConfigFactory) element).getReaderFactoryID()
					+ " Commands";
		} else if (element instanceof RemoteCommandConfigType) {
			return ((RemoteCommandConfigType) element).getCommandConfigType();
		} else if (element instanceof RemoteCommandConfiguration) {
			return ((RemoteCommandConfiguration) element).getID();
		} else
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

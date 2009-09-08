/*
 * CommandTreeLabelProvider.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.sal.controller.commands;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.rifidi.edge.client.model.sal.RemoteCommandConfigFactory;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteReaderFactory;
import org.rifidi.edge.client.sal.SALPluginActivator;

/**
 * Label Provider for the Command Tree Viewer
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CommandTreeLabelProvider implements ILabelProvider {

	private Image serverGreen = SALPluginActivator.getImageDescriptor(
			"icons/server-green.png").createImage();
	private Image serverRed = SALPluginActivator.getImageDescriptor(
			"icons/server-red.png").createImage();
	private Image readerCog = SALPluginActivator.getImageDescriptor(
			"icons/reader-cog.png").createImage();

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
				return serverGreen;
			case DISCONNECTED:
				return serverRed;
			}
		}
		if (element instanceof RemoteReaderFactory) {
			return readerCog;
		}
		if (element instanceof RemoteCommandConfigFactory) {
			return SALPluginActivator.getDefault().getImageRegistry().get(
					SALPluginActivator.IMAGE_FOLDER);
		}
		if (element instanceof RemoteCommandConfiguration) {
			return SALPluginActivator.getDefault().getImageRegistry().get(
					SALPluginActivator.IMAGE_COG);
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
		} else if (element instanceof RemoteReaderFactory) {
			return ((RemoteReaderFactory) element).getID() + " Commands";
		} else if (element instanceof RemoteCommandConfigFactory) {
			return ((RemoteCommandConfigFactory) element)
					.getCommandConfigFactoryID();
		} else if (element instanceof RemoteCommandConfiguration) {
			RemoteCommandConfiguration config = (RemoteCommandConfiguration) element;
			if (config.isDirty()) {
				return "*" + config.getID();
			} else {
				return config.getID();
			}
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		serverGreen.dispose();
		serverRed.dispose();
		readerCog.dispose();
		serverGreen = null;
		serverRed = null;
		readerCog = null;

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
	}

}

package org.rifidi.edge.client.sal.properties;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.sal.SALPluginActivator;

/**
 * This is the label provider for the tabbed properties view
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class SALTabbedPropertiesLabelProvider implements ILabelProvider {

	private Image server = SALPluginActivator.getImageDescriptor(
			"icons/server.png").createImage();
	private Image reader = SALPluginActivator.getImageDescriptor(
			"icons/reader-16x16.png").createImage();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection) element).getFirstElement();
			if (o instanceof RemoteEdgeServer) {
				return server;
			}
			if (o instanceof RemoteReader) {
				return reader;
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
		if (element instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection) element).getFirstElement();
			if (o instanceof RemoteEdgeServer) {
				return "Remote Edge Server";
			}
			if (o instanceof RemoteReader) {
				return "Reader: " + ((RemoteReader) o).getID();
			}

		}
		return null;
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
		server.dispose();
		server = null;
		reader.dispose();
		reader = null;

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

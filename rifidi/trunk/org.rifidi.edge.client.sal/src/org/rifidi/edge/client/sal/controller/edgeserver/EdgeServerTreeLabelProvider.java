package org.rifidi.edge.client.sal.controller.edgeserver;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteJob;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.model.sal.RemoteSession;
import org.rifidi.edge.client.sal.SALPluginActivator;

/**
 * Label Provider for the Edge Server Tree View
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class EdgeServerTreeLabelProvider implements ILabelProvider {

	private Image serverGreen = SALPluginActivator.getImageDescriptor(
			"icons/server-green.png").createImage();
	private Image serverRed = SALPluginActivator.getImageDescriptor(
			"icons/server-red.png").createImage();
	private Image reader = SALPluginActivator.getImageDescriptor(
			"icons/reader-16x16.png").createImage();
	private Image linkRed = SALPluginActivator.getImageDescriptor(
			"icons/link-red.png").createImage();
	private Image linkYellow = SALPluginActivator.getImageDescriptor(
			"icons/link-yellow.png").createImage();
	private Image linkGreen = SALPluginActivator.getImageDescriptor(
			"icons/link-green.png").createImage();
	private Image scriptGear = SALPluginActivator.getImageDescriptor(
			"icons/script_gear.png").createImage();

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
		} else if (element instanceof RemoteReader) {
			return reader;
		} else if (element instanceof RemoteSession) {
			RemoteSession session = (RemoteSession) element;
			switch (session.getStateOfSession()) {
			case CLOSED:
				return linkRed;
			case CONNECTING:
				return linkYellow;
			case CREATED:
				return linkRed;
			case FAIL:
				return linkRed;
			case LOGGINGIN:
				return linkYellow;
			case PROCESSING:
				return linkGreen;
			}

		} else if (element instanceof RemoteJob) {
			return scriptGear;
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
			RemoteReader reader = (RemoteReader) element;
			if (reader.isDirty()) {
				return "*" + ((RemoteReader) element).getID();
			} else {
				return ((RemoteReader) element).getID();
			}
		}
		if (element instanceof RemoteSession) {
			return "Session: " + ((RemoteSession) element).getSessionID();
		}
		if (element instanceof RemoteJob) {
			RemoteJob job = (RemoteJob) element;
			return "Command " + job.getJobID() + " : "
					+ job.getCommandConfigurationID();
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
		this.linkGreen.dispose();
		this.linkGreen = null;
		this.linkRed.dispose();
		this.linkRed = null;
		this.linkYellow.dispose();
		this.linkYellow = null;
		this.reader.dispose();
		this.reader = null;
		this.scriptGear.dispose();
		this.scriptGear = null;
		this.serverGreen.dispose();
		this.serverGreen = null;
		this.serverRed.dispose();
		this.serverRed = null;

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

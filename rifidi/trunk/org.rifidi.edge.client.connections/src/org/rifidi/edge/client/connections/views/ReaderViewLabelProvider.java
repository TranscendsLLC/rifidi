package org.rifidi.edge.client.connections.views;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.rifidi.edge.client.connections.Activator;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.registryservice.EdgeServerConnectionRegistryService;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class ReaderViewLabelProvider implements ILabelProvider {

//	private Log logger = LogFactory.getLog(ReaderViewLabelProvider.class);

	private EdgeServerConnectionRegistryService edgeServerConnectionService;

	public ReaderViewLabelProvider() {
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof EdgeServerConnection) {
			if (((EdgeServerConnection) element).isConnected()) {
				return Activator
						.getImageDescriptor("icons/icon_monitor_pc.gif")
						.createImage();
			} else {
				return Activator.getImageDescriptor("icons/action_stop.gif")
						.createImage();
			}
		}

		if (element instanceof RemoteReader) {
			String readerState;

			RemoteReader remoteReader = (RemoteReader) element;

			int edgeServerID = remoteReader.getServerID();
			EdgeServerConnection ESConnection = this.edgeServerConnectionService
					.getConnection(edgeServerID);
			if (!ESConnection.isConnected()) {
				return Activator.getImageDescriptor("icons/disconnect.png")
						.createImage();
			}

			readerState = remoteReader.getReaderState();
			if (readerState == null || readerState.equals("")) {
				return Activator.getImageDescriptor("icons/action_stop.gif")
						.createImage();
			}
			if (readerState.equals("OK")) {
				return Activator.getImageDescriptor("icons/icon_accept.gif")
						.createImage();
			}
			if (readerState.equals("ERROR")) {
				return Activator.getImageDescriptor("icons/action_stop.gif")
						.createImage();
			}
			if (readerState.equals("CONFIGURED")) {
				return Activator.getImageDescriptor("icons/icon_settings.gif")
						.createImage();
			}
			if (readerState.equals("UNAVAILABLE")) {
				return Activator.getImageDescriptor("icons/disconnect.png")
						.createImage();
			}

			else {
				return Activator.getImageDescriptor("icons/action_refresh.gif")
						.createImage();
			}

		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof RemoteReader) {
			return ((RemoteReader) element).getDescription();
		}
		return element.toString();
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Inject
	public void setEdgeServerConnectionRegistryService(
			EdgeServerConnectionRegistryService service) {
		this.edgeServerConnectionService = service;
	}

}

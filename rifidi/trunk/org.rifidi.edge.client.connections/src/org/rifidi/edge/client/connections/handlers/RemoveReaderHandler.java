package org.rifidi.edge.client.connections.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.registryservice.EdgeServerConnectionRegistryService;
import org.rifidi.edge.client.connections.registryservice.RemoteReaderConnectionRegistryService;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class RemoveReaderHandler extends AbstractHandler implements IHandler {
	public final static String ID = "org.rifidi.edge.client.connections.RemoveReader";

	RemoteReaderConnectionRegistryService readerConnectionRegistryService;
	EdgeServerConnectionRegistryService edgeServerConnectionRegistryService;

	public RemoveReaderHandler() {
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelectionChecked(event);
		Object sel = selection.getFirstElement();
		if (sel instanceof RemoteReader) {
			RemoteReader rr = ((RemoteReader) sel);
			edgeServerConnectionRegistryService.getConnection(rr.getServerID())
					.destroyRemoteReader(rr.getID());
			return null;
		}
		if (sel instanceof EdgeServerConnection) {
			edgeServerConnectionRegistryService
					.destroyConnection(((EdgeServerConnection) sel).getID());
		}

		return null;
	}

	@Inject
	public void setRemoteReaderConnectionRegistryService(
			RemoteReaderConnectionRegistryService readerConnectionRegistryService) {
		this.readerConnectionRegistryService = readerConnectionRegistryService;
	}

	@Inject
	public void setEdgeServerConnectionRegistryService(
			EdgeServerConnectionRegistryService edgeServerConnectionRegistryService) {
		this.edgeServerConnectionRegistryService = edgeServerConnectionRegistryService;
	}

}

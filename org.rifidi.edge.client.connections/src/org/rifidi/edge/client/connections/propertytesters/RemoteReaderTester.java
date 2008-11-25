package org.rifidi.edge.client.connections.propertytesters;

import org.eclipse.core.expressions.PropertyTester;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.registryservice.EdgeServerConnectionRegistryService;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * Used to test which state the RemoteReader is in
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RemoteReaderTester extends PropertyTester {

	private EdgeServerConnectionRegistryService edgeServerConnectionService;

	public RemoteReaderTester() {
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof RemoteReader) {
			RemoteReader reader = (RemoteReader) receiver;
			if (property.equalsIgnoreCase("state")) {

				int edgeServerID = reader.getServerID();
				EdgeServerConnection ESConnection = this.edgeServerConnectionService
						.getConnection(edgeServerID);
				if (!ESConnection.isConnected()) {
					return false;
				}

				String state;

				state = reader.getReaderState();
				for (Object o : args) {
					if (o instanceof String)
						if (state.equalsIgnoreCase((String) o))
							return true;
				}

			}
		}
		return false;
	}

	@Inject
	public void setEdgeServerConnectionRegistryService(
			EdgeServerConnectionRegistryService service) {
		this.edgeServerConnectionService = service;
	}

}

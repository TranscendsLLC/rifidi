package org.rifidi.edge.client.connections.propertytesters;

import org.eclipse.core.expressions.PropertyTester;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;

/**
 * Used to test to which state the EdgeServerConnection object is in
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EdgeServerConnectionTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (property.equals("edgeserverconnectionstate")) {
			if (receiver instanceof EdgeServerConnection) {
				Boolean state = ((EdgeServerConnection) receiver).isConnected();
				for (Object o : args) {
					if (o instanceof Boolean) {
						return (state.equals((Boolean) o));
					}
				}

			}
		}

		return false;
	}

}

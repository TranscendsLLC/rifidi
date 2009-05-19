
package org.rifidi.edge.client.sal.controller.edgeserver.handlers;

import org.eclipse.core.expressions.PropertyTester;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteEdgeServerState;

/**
 * A property tester to test the state of the edge server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RemoteEdgeServerPropertyTester extends PropertyTester {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 * java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof RemoteEdgeServer) {

			String val = (String) expectedValue;
			return ((RemoteEdgeServer) receiver).getState().equals(
					RemoteEdgeServerState.valueOf(val));
		}
		return false;
	}

}

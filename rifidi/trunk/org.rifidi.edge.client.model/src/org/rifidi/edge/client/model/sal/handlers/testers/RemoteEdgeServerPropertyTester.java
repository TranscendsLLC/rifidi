/**
 * 
 */
package org.rifidi.edge.client.model.sal.handlers.testers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.expressions.PropertyTester;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteEdgeServerState;

/**
 * @author kyle
 * 
 */
public class RemoteEdgeServerPropertyTester extends PropertyTester {

	private final static Log logger = LogFactory
			.getLog(RemoteEdgeServerPropertyTester.class);

	/**
	 * 
	 */
	public RemoteEdgeServerPropertyTester() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 * java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		try {
			if (receiver instanceof RemoteEdgeServer) {
				String val = (String) expectedValue;
				return ((RemoteEdgeServer) receiver).getState().equals(
						RemoteEdgeServerState.valueOf(val));
			}
		} catch (Exception e) {
			logger.warn(e);
		}

		return false;

	}

}

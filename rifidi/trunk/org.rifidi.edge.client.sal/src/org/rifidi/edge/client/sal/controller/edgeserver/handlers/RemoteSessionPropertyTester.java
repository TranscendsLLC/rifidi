
package org.rifidi.edge.client.sal.controller.edgeserver.handlers;

import org.eclipse.core.expressions.PropertyTester;
import org.rifidi.edge.client.model.sal.RemoteSession;
import org.rifidi.edge.api.SessionStatus;

/**
 * A property tester to test the state of a session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RemoteSessionPropertyTester extends PropertyTester {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 * java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		if (receiver instanceof RemoteSession) {
			String val = (String) expectedValue;
			return ((RemoteSession) receiver).getStateOfSession().equals(
					SessionStatus.valueOf(val));
		}

		return false;
	}

}

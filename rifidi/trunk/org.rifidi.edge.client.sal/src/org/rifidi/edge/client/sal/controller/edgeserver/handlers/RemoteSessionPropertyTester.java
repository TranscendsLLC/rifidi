/*
 * RemoteSessionPropertyTester.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */

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

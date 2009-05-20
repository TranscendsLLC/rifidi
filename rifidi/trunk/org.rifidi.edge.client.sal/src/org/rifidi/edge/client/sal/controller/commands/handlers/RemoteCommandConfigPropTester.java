package org.rifidi.edge.client.sal.controller.commands.handlers;

import org.eclipse.core.expressions.PropertyTester;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;

/**
 * A property tester that tests properties of CommandConfigurations model
 * objects. Currently the only property is "dirty" which is true if the
 * CommandConfiguration has properties that have been modified and not yet
 * committed to the server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RemoteCommandConfigPropTester extends PropertyTester {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 * java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof RemoteCommandConfiguration) {
			RemoteCommandConfiguration commandConfig = (RemoteCommandConfiguration) receiver;
			if (property.equals("dirty")) {
				Boolean expected = (Boolean) expectedValue;
				return expected.equals(commandConfig.isDirty());
			}
		}
		return false;
	}

}

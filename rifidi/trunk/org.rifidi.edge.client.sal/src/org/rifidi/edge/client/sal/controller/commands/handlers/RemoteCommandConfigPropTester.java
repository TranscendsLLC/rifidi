/**
 * 
 */
package org.rifidi.edge.client.sal.controller.commands.handlers;
//TODO: Comments
import org.eclipse.core.expressions.PropertyTester;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;

/**
 * @author kyle
 * 
 */
public class RemoteCommandConfigPropTester extends PropertyTester {

	/**
	 * 
	 */
	public RemoteCommandConfigPropTester() {
		// TODO Auto-generated constructor stub
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

package org.rifidi.edge.client.sal.controller.edgeserver.handlers;

import org.eclipse.core.expressions.PropertyTester;
import org.rifidi.edge.client.model.sal.RemoteReader;

/**
 * A PropertyTester for RemoteReaders. Receiver should be a RemoteReader.
 * Currently there is only one property called 'dirty' that is true if the
 * reader has uncommitted property changes
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReaderPropertyTester extends PropertyTester {

	/**
	 * Constructor.
	 */
	public ReaderPropertyTester() {
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
		if (receiver instanceof RemoteReader) {
			RemoteReader reader = (RemoteReader) receiver;
			if (property.equals("dirty")) {
				Boolean expected = (Boolean) expectedValue;
				return expected.equals(reader.isDirty());
			}
		}
		return false;
	}

}

package org.rifidi.edge.readerplugin.dummy.protocol;

import org.rifidi.edge.core.api.readerplugin.MessageProtocol;

public class DummyMessageProtocol implements MessageProtocol {

	@Override
	public String toXML(Object message) {
		return "Fake XML" + (String)message;
	}

}

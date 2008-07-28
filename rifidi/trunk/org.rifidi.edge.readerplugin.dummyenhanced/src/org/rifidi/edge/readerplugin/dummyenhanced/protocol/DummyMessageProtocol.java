package org.rifidi.edge.readerplugin.dummyenhanced.protocol;

import org.rifidi.edge.core.readerplugin.protocol.MessageProtocol;

public class DummyMessageProtocol implements MessageProtocol {

	@Override
	public String toXML(Object message) {
		return "Fake XML" + (String)message;
	}

}

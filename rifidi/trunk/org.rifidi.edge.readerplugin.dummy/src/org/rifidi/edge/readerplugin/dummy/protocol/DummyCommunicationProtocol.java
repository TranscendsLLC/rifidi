package org.rifidi.edge.readerplugin.dummy.protocol;

import org.rifidi.edge.core.api.readerplugin.communication.CommunicationProtocol;
import org.rifidi.edge.readerplugin.dummy.plugin.DummyReaderInfo;

public class DummyCommunicationProtocol implements CommunicationProtocol {

	private StringBuffer buf = new StringBuffer();

	public DummyCommunicationProtocol(DummyReaderInfo info) {
	}

	@Override
	public Object byteToMessage(byte b) {
		buf.append((char) b);
		if ((char) b == '\n') {
			String temp = buf.toString();
			buf = new StringBuffer();
			return temp;
		}
		return null;
	}

	@Override
	public byte[] messageToByte(Object message) {
		return ((String) message).getBytes();
	}

}

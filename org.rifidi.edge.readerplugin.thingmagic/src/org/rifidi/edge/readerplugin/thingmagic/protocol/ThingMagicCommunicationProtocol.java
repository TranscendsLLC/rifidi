package org.rifidi.edge.readerplugin.thingmagic.protocol;

import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public class ThingMagicCommunicationProtocol implements CommunicationProtocol {

	private StringBuffer buffer;
	
	public ThingMagicCommunicationProtocol() {
		buffer = new StringBuffer();
	}
	
	@Override
	public Object byteToMessage(byte b) {
		buffer.append(b);
		if(buffer.length() == 5)
		{
			String retVal = buffer.toString();
			buffer = new StringBuffer();
			return retVal;
		}
		return null;
	}

	@Override
	public byte[] messageToByte(Object message) {
		return ((String)message).getBytes();
	}

}

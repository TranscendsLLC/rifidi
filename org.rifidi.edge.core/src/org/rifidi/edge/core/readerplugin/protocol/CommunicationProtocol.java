package org.rifidi.edge.core.readerplugin.protocol;

public interface CommunicationProtocol {
	
	// TODO figure out if you could send in whole Arrays
	public Object byteToMessage(byte b);

	public byte[] messageToByte(Object message);
}

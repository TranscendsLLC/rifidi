package org.rifidi.edge.core.readerplugin.protocol;

import org.rifidi.edge.core.exceptions.RifidiInvalidMessageFormat;

public interface CommunicationProtocol {
	
	// TODO figure out if you could send in whole Arrays
	public Object byteToMessage(byte b) throws RifidiInvalidMessageFormat;

	public byte[] messageToByte(Object message) throws RifidiInvalidMessageFormat;
}

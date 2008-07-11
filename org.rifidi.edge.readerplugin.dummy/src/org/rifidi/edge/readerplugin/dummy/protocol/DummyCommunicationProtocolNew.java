/**
 * 
 */
package org.rifidi.edge.readerplugin.dummy.protocol;

import org.rifidi.edge.core.exceptions.RifidiInvalidMessageFormat;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

/**
 * @author kyle
 *
 */
public class DummyCommunicationProtocolNew implements CommunicationProtocol {

	StringBuffer buf = new StringBuffer();
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol#byteToMessage(byte)
	 */
	@Override
	public Object byteToMessage(byte b) throws RifidiInvalidMessageFormat {
		buf.append((char) b);
		if ((char) b == '\n') {
			String temp = buf.toString();
			buf = new StringBuffer();
			return temp;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol#messageToByte(java.lang.Object)
	 */
	@Override
	public byte[] messageToByte(Object message)
			throws RifidiInvalidMessageFormat {
		return ((String) message).getBytes();
	}

}

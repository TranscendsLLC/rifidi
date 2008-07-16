package org.rifidi.edge.readerplugin.thingmagic.protocol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public class ThingMagicCommunicationProtocol implements CommunicationProtocol {
	
	
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(ThingMagicCommunicationProtocol.class);
	
	private StringBuffer buffer;
	
	public ThingMagicCommunicationProtocol() {
		buffer = new StringBuffer();
	}
	
	@Override
	public Object byteToMessage(byte b) {
		//logger.debug((char) b );
		
		//TODO Test this on the real reader.
		buffer.append((char) b);
		if (buffer.length() >= 2 ){
			if(buffer.toString().endsWith("\n\n"))
			{
				String retVal = buffer.toString();
				buffer = new StringBuffer();
				return retVal.replace("\n\n", "");
			}
		} else {
			if (buffer.toString().equals("\n")) {
				buffer = new StringBuffer();
				return ""; // No tags message.
			}
			
		}
		return null;
	}

	@Override
	public byte[] messageToByte(Object message) {
		return ( ( (String ) message) + "\n" ).getBytes();
	}

}

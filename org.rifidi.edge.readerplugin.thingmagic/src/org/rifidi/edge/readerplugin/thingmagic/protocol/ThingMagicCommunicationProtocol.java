package org.rifidi.edge.readerplugin.thingmagic.protocol;

import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public class ThingMagicCommunicationProtocol implements CommunicationProtocol {
	//private static final Log logger = LogFactory.getLog(ThingMagicCommunicationProtocol.class);
	
	private StringBuffer buffer;
	
	public ThingMagicCommunicationProtocol() {
		buffer = new StringBuffer();
	}
	
	@Override
	public Object byteToMessage(byte b) {
		//logger.debug((char) b );
		buffer.append((char) b);
		if (buffer.length() >= 2 ){
			if(buffer.toString().endsWith("\n\n"))
			{
				String retVal = buffer.toString();
				buffer = new StringBuffer();
				return retVal.replace("\n\n", "");
			}
		}
		return null;
	}

	@Override
	public byte[] messageToByte(Object message) {
		return ( ( (String ) message) + "\n" ).getBytes();
	}

}

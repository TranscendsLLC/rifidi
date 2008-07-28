package org.rifidi.edge.readerplugin.dummyenhanced.protocol;

import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;
import org.rifidi.edge.readerplugin.dummyenhanced.plugin.DummyEnhancedReaderInfo;

public class DummyCommunicationProtocol implements CommunicationProtocol {

	private StringBuffer buf = new StringBuffer();

	
	@Override
	public Object byteToMessage(byte b) {
		//TODO This might need to be more refined.
		/* used for breakage testing purposes */
		
		buf.append((char) b);
		if (buf.length() >= 2 ){
			if(buf.toString().endsWith("\n\n"))
			{
				String retVal = buf.toString();
				buf = new StringBuffer();
				return retVal.replace("\n\n", "");
			}
		} else {
			if (buf.toString().equals("\n")) {
				buf = new StringBuffer();
				return ""; // No tags message.
			}
			
		}
		return null;
	}

	@Override
	public byte[] messageToByte(Object message) {
		//TODO This might need to be more refined.
		return ( ( (String ) message) + "\n" ).getBytes();
	}

}

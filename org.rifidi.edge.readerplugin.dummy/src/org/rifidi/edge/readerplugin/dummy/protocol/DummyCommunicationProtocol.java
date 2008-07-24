package org.rifidi.edge.readerplugin.dummy.protocol;

import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;
import org.rifidi.edge.readerplugin.dummy.plugin.DummyReaderInfo;

public class DummyCommunicationProtocol implements CommunicationProtocol {

	private StringBuffer buf = new StringBuffer();
	
	/* used for breakage test purposes */
	DummyReaderInfo info;
	
	public DummyCommunicationProtocol(DummyReaderInfo info) {
		this.info = info;
	}
	
	@Override
	public Object byteToMessage(byte b) {
		//TODO This might need to be more refined.
		/* used for breakage testing purposes */
		switch (info.getErrorToSet()) {
			case PROTOCOL_FROM_BYTE:
				//throw new RifidiConnectionIllegalStateException();
			case PROTOCOL_FROM_BYTE_RUNTIME:
				throw new RuntimeException();
			case RANDOM:
				if (info.getRandom().nextDouble() <= info.getRandomErrorProbibility()){
					if(info.getRandom().nextDouble() <= info.getProbiblityOfErrorsBeingRuntimeExceptions()){
						throw new RuntimeException();
					} else {
						//throw new RifidiConnectionIllegalStateException();
					}
				}
		}
		
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
		//TODO This might need to be more refined.
		/* used for breakage testing purposes */
		switch (info.getErrorToSet()) {
			case PROTOCOL_TO_BYTE:
				//throw new RifidiConnectionIllegalStateException();
			case PROTOCOL_TO_BYTE_RUNTIME:
				throw new RuntimeException();
			case RANDOM:
				if (info.getRandom().nextDouble() <= info.getRandomErrorProbibility()){
					if(info.getRandom().nextDouble() <= info.getProbiblityOfErrorsBeingRuntimeExceptions()){
						throw new RuntimeException();
					} else {
						//throw new RifidiConnectionIllegalStateException();
					}
				}
		}
		return ((String)message).getBytes();
	}

}

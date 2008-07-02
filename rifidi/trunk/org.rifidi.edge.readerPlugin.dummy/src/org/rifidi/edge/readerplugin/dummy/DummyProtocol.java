package org.rifidi.edge.readerplugin.dummy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.exception.readerConnection.RifidiInvalidMessageFormat;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class DummyProtocol extends Protocol {
	private static final Log logger = LogFactory.getLog(DummyProtocol.class);
	

	StringBuilder byteBin = new StringBuilder();
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.protocol.Protocol#add(byte)
	 */
	@Override
	public Object add(byte b) throws RifidiInvalidMessageFormat {
		// TODO Auto-generated method stub
		byteBin.append((char)b);
		
		if(byteBin.length() >= 2) {
			if (byteBin.substring(byteBin.length()-2).equals("\n\n")) {
				String retVal = byteBin.toString();
				byteBin = new StringBuilder();
				return retVal;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.protocol.Protocol#toByteArray(java.lang.Object)
	 */
	@Override
	public byte[] toByteArray(Object o) throws RifidiInvalidMessageFormat {
		logger.debug(o);
		//All objects coming in are strings and are sent out as raw bytes.
		return ( (String)o ).getBytes();
	}

}

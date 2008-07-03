package org.rifidi.edge.readerPlugin.thingmagic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.exception.readerConnection.RifidiInvalidMessageFormat;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class ThingMagicProtocol extends Protocol {
	private static final Log logger = LogFactory.getLog(ThingMagicProtocol.class);
	
	StringBuilder byteBin = new StringBuilder();
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.protocol.Protocol#add(byte)
	 */
	@Override
	public Object add(byte b) throws RifidiInvalidMessageFormat {
	
		byteBin.append((char) b);
		
		if(byteBin.length() >= 2) {
			if (byteBin.substring(byteBin.length()-2).equals("\n\n")) {
				String retVal = byteBin.toString().replace("\n\n", "");
				logger.debug(retVal);
				byteBin = new StringBuilder();
				return retVal;
			}
		}
		//logger.debug(byteBin.toString());
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

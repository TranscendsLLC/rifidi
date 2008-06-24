package org.rifidi.edge.readerPlugin.thingmagic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.protocol.Protocol;

public class ThingMagicProtocol extends Protocol {
	private static final Log logger = LogFactory.getLog(ThingMagicProtocol.class);
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.buffer.Protocol#fromObject(java.lang.Object)
	 */
	@Override
	public byte[] fromObject(Object arg) {
		logger.debug(arg);
		return ( (String)arg ).getBytes();
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.buffer.Protocol#toObject(byte[])
	 */
	@Override
	public List<Object> toObject(byte[] arg) {
		
		List<Object> retVal = new ArrayList<Object>();
		
		String input = new String(arg);
		logger.debug(input);
		if(!input.equals("\n\n")){		
		
			for (String s : input.split("\n\n")) {
				if (!s.isEmpty())
					retVal.add(s);
			}		
		}
		
		return retVal;
	}

}

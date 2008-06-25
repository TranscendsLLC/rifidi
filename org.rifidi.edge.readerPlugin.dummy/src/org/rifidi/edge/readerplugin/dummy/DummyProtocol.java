package org.rifidi.edge.readerplugin.dummy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.protocol.Protocol;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class DummyProtocol extends Protocol {
	private static final Log logger = LogFactory.getLog(DummyProtocol.class);
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.buffer.Protocol#fromObject(java.lang.Object)
	 */
	@Override
	public byte[] fromObject(Object arg) {
		logger.debug(arg);
		//All objects coming in are strings and are sent out as raw bytes.
		return ( (String)arg ).getBytes();
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.buffer.Protocol#toObject(byte[])
	 */
	@Override
	public List<Object> toObject(byte[] arg) {
		
		List<Object> retVal = new ArrayList<Object>();
		
		String input = new String(arg);
		//logger.debug(input);
		
		//the end of a tag stream is a double newline.
		if(!input.equals("\n\n")){		
		
			for (String s : input.split("\n\n")) {
				if (!s.isEmpty())
					retVal.add(s);
			}		
		}
		
		return retVal;
	}

}

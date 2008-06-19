/*
 *  AlienReaderPluginFactory.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.alien;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exception.readerConnection.RifidiReaderPluginCreationException;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;

/**
 * This is a factory class for the Alien reader plugin.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AlienReaderPluginFactory implements ISpecificReaderPluginFactory {
	
	/**
	 * The logger for this class.  
	 */
	private static final Log logger = LogFactory.getLog(AlienReaderPluginFactory.class);
	
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory#createSpecificReaderAdapter(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	@Override
	public IReaderPlugin createSpecificReaderAdapter(AbstractReaderInfo abstractConnectionInfo) throws RifidiReaderPluginCreationException{
		
		if(abstractConnectionInfo instanceof AlienReaderInfo)
		{
			logger.debug("createSpecificReaderAdapter called.");
			return new AlienReaderPlugin((AlienReaderInfo)abstractConnectionInfo);
		}
		return null;
	}
}

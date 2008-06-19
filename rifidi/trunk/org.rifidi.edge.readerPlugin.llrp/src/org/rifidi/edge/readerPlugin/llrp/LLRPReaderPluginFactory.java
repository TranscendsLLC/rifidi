/*
 *  LLRPReaderPluginFactory.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.llrp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;

/**
 * The plugin factory for the LLRP.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class LLRPReaderPluginFactory implements ISpecificReaderPluginFactory {

	/**
	 * The logger.
	 */
	private static final Log logger = LogFactory
			.getLog(LLRPReaderPluginFactory.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory#createSpecificReaderAdapter(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	@Override
	public IReaderPlugin createSpecificReaderAdapter(
			AbstractReaderInfo abstractConnectionInfo) {
		if (abstractConnectionInfo instanceof LLRPReaderInfo) {
			logger.debug("createSpecificReaderAdapter called.");
			return new LLRPReaderPlugin((LLRPReaderInfo) abstractConnectionInfo);
		}
		return null;
	}
}

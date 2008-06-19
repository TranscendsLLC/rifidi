/*
 *  ISpecificReaderPluginFactory.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.readerPlugin;

import org.rifidi.edge.core.exception.readerConnection.RifidiReaderPluginCreationException;

public interface ISpecificReaderPluginFactory {

	/**
	 * Factory for reader plugins
	 * @param abstractConnectionInfo The connection info for the reader
	 * @return The reader plugin
	 * @throws RifidiReaderPluginCreationException
	 */
	public IReaderPlugin createSpecificReaderAdapter(AbstractReaderInfo abstractConnectionInfo)
		throws RifidiReaderPluginCreationException;

}

/**
 * 
 */
package org.rifidi.edge.readerPlugin.alien;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exception.readerPlugin.RifidiReaderAdapterCreationException;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;

/**
 * @author Matthew Dean - matt@pramari.com
 *
 */
public class AlienReaderAdapterFactory implements ISpecificReaderPluginFactory {
	private static final Log logger = LogFactory.getLog(AlienReaderAdapterFactory.class);	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory#createSpecificReaderAdapter(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	@Override
	public IReaderPlugin createSpecificReaderAdapter(AbstractReaderInfo abstractConnectionInfo) throws RifidiReaderAdapterCreationException{
		
		if(abstractConnectionInfo instanceof AlienConnectionInfo)
		{
			logger.debug("createSpecificReaderAdapter called.");
			return new AlienReaderAdapter((AlienConnectionInfo)abstractConnectionInfo);
		}
		return null;
	}
}

/**
 * 
 */
package org.rifidi.edge.readerPlugin.llrp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;

/**
 * @author Matthew Dean - matt@pramari.com
 *
 */
public class LLRPReaderPluginFactory implements ISpecificReaderPluginFactory {

	private static final Log logger = LogFactory.getLog(LLRPReaderPluginFactory.class);
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory#createSpecificReaderAdapter(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	@Override
	public IReaderPlugin createSpecificReaderAdapter(AbstractReaderInfo abstractConnectionInfo) {
		if(abstractConnectionInfo instanceof LLRPReaderInfo)
		{
			logger.debug("createSpecificReaderAdapter called.");
			return new LLRPReaderPlugin((LLRPReaderInfo)abstractConnectionInfo);
		}
		return null;
	}
}

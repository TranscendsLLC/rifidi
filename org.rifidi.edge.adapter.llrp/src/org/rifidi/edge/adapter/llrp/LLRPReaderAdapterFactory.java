/**
 * 
 */
package org.rifidi.edge.adapter.llrp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

/**
 * @author Matthew Dean - matt@pramari.com
 *
 */
public class LLRPReaderAdapterFactory implements ISpecificReaderAdapterFactory {

	private static final Log logger = LogFactory.getLog(LLRPReaderAdapterFactory.class);
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory#createSpecificReaderAdapter(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	@Override
	public IReaderAdapter createSpecificReaderAdapter(AbstractConnectionInfo abstractConnectionInfo) {
		if(abstractConnectionInfo instanceof LLRPConnectionInfo)
		{
			logger.debug("createSpecificReaderAdapter called.");
			return new LLRPReaderAdapter((LLRPConnectionInfo)abstractConnectionInfo);
		}
		return null;
	}
}

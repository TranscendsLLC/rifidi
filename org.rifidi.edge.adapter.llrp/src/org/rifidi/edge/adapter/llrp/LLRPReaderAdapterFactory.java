/**
 * 
 */
package org.rifidi.edge.adapter.llrp;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

/**
 * @author Matthew Dean - matt@pramari.com
 *
 */
public class LLRPReaderAdapterFactory implements ISpecificReaderAdapterFactory {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory#createSpecificReaderAdapter(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	@Override
	public IReaderAdapter createSpecificReaderAdapter(AbstractConnectionInfo abstractConnectionInfo) {
		if(abstractConnectionInfo instanceof LLRPConnectionInfo)
		{
			return new LLRPReaderAdapter((LLRPConnectionInfo)abstractConnectionInfo);
		}
		return null;
	}
}

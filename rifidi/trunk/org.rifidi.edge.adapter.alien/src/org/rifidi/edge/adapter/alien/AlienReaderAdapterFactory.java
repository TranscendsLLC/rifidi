/**
 * 
 */
package org.rifidi.edge.adapter.alien;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

/**
 * @author Matthew Dean - matt@pramari.com
 *
 */
public class AlienReaderAdapterFactory implements ISpecificReaderAdapterFactory {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory#createSpecificReaderAdapter(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	@Override
	public IReaderAdapter createSpecificReaderAdapter(AbstractConnectionInfo abstractConnectionInfo) {
		if(abstractConnectionInfo instanceof AlienConnectionInfo)
		{
			return new AlienReaderAdapter((AlienConnectionInfo)abstractConnectionInfo);
		}
		return null;
	}
}

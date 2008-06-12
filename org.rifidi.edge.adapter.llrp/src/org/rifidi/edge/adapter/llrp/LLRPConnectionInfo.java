/**
 * 
 */
package org.rifidi.edge.adapter.llrp;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class LLRPConnectionInfo extends AbstractConnectionInfo {
	
	@Override
	public Class<? extends AbstractConnectionInfo> getReaderAdapterType() {
		return this.getClass();
	}
}

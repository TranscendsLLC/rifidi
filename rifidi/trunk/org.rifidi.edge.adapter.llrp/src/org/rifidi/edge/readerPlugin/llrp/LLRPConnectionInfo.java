/**
 * 
 */
package org.rifidi.edge.readerPlugin.llrp;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class LLRPConnectionInfo extends AbstractReaderInfo {
	
	@Override
	public Class<? extends AbstractReaderInfo> getReaderAdapterType() {
		return this.getClass();
	}
}


package org.rifidi.edge.client.ale.logicalreaders;

import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;

/**
 * TODO: Class level comment.  
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public interface ALEListener {
	/**
	 * Set the stub for accessing ALE.
	 * 
	 * @param stub
	 */
	void setALEStub(ALEServicePortType stub);
}

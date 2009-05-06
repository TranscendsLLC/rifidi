/**
 * 
 */
package org.rifidi.edge.client.ale.logicalreaders;
//TODO: Comments
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ALELRServicePortType;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ALELRListener {
	/**
	 * Set the stub for accessing ALELR.
	 * 
	 * @param stub
	 */
	void setALELRStub(ALELRServicePortType stub);
}

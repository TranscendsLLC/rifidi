/**
 * 
 */
package org.rifidi.edge.client.ale.ecspecview.modelmanager;

import java.util.List;

import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;

/**
 * @author kyle
 * 
 */
public interface ModelChangedListener {

	public void modelUpdated(List<ALEServicePortType> model);

}

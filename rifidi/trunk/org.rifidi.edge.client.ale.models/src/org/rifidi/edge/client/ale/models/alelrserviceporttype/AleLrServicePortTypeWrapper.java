/* 
 *  AleLrServicePortTypeWrapper.java
 *  Created:	Apr 16, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.models
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.models.alelrserviceporttype;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ALELRServicePortType;
import org.rifidi.edge.client.ale.connection.service.ConnectionService;
import org.rifidi.edge.client.ale.models.aleserviceporttype.AleServicePortTypeWrapper;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AleLrServicePortTypeWrapper {

	private ALELRServicePortType aleLrServicePortType = null;
	private AleServicePortTypeWrapper wrapper = null;
	private WritableSet logicalReaders = new WritableSet();
	private ConnectionService consService = null;
	private Log logger = LogFactory.getLog(AleLrServicePortTypeWrapper.class);
	private URL endpoint = null;

	/**
	 * 
	 */
	public AleLrServicePortTypeWrapper(AleServicePortTypeWrapper wrapper) {
		this.wrapper = wrapper;
	}

	public String getName() {
		return wrapper.getName();
	}

}

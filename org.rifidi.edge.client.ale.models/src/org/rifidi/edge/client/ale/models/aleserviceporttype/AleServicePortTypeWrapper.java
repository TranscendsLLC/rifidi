/* 
 *  AleServicePortTypeWrapper.java
 *  Created:	Apr 14, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.models
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.models.aleserviceporttype;

import java.net.URL;
import java.util.ArrayList;

import javax.management.ServiceNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.connection.handler.ConnectionHandler;
import org.rifidi.edge.client.ale.connection.service.ConnectionService;
import org.rifidi.edge.client.ale.models.ecspec.RemoteSpecModelWrapper;
import org.rifidi.edge.client.ale.models.enums.ConnectionStatus;
import org.rifidi.edge.client.ale.models.exceptions.RifidiNoEndpointDefinedException;
import org.rifidi.edge.client.ale.models.serviceprovider.SpecDataManager;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AleServicePortTypeWrapper extends SpecDataManager {

	private ALEServicePortType aleServicePortType = null;

	private ConnectionService conSvc = null;
	private Log logger = LogFactory.getLog(AleServicePortTypeWrapper.class);
	private URL endpoint = null;

	/**
	 * 
	 */
	public AleServicePortTypeWrapper(URL endpoint) {
		this.endpoint = endpoint;
		this.setName("EdgeServer");

	}

	public AleServicePortTypeWrapper() {
		this.setName("EdgeServer");
	}

	public void connect() throws RifidiNoEndpointDefinedException {
		logger.debug("Trying to connect ALE...");
		if (this.endpoint != null) {
			ConnectionHandler conHan = new ConnectionHandler();

			try {
				conSvc = conHan.getService();
				this.aleServicePortType = conSvc
						.getAleServicePortType(endpoint);
				connectionStatusChanged(ConnectionStatus.CONNECTED);
			} catch (ServiceNotFoundException e) {
				logger.error(e.getMessage());
			}

		} else
			throw new RifidiNoEndpointDefinedException();

	}

	/**
	 * @return the endpoint
	 */
	public URL getEndpoint() {
		return endpoint;
	}

	/**
	 * @param endpoint
	 *            the endpoint to set
	 */
	public void setEndpoint(URL endpoint) {
		this.endpoint = endpoint;
	}

	public void disconnect() {
		logger.debug("disconnecting ALE...");
		this.aleServicePortType = null;
		connectionStatusChanged(ConnectionStatus.DISCONNECTED);
	}

	public Object[] getSpecs() {

		if (this.aleServicePortType != null) {

			try {
				ArrayList<RemoteSpecModelWrapper> alEsWrs = new ArrayList<RemoteSpecModelWrapper>();
				for (String string : this.aleServicePortType.getECSpecNames(
						new EmptyParms()).getString()) {
					alEsWrs.add(new RemoteSpecModelWrapper(string, this));
				}

				if (this.specWrappers.size() == 0) {
					this.specWrappers.addAll(alEsWrs);
				} else
					this.specWrappers.retainAll(alEsWrs);
			} catch (ImplementationExceptionResponse e) {
				logger.debug(e.getMessage());
				disconnect();
			} catch (SecurityExceptionResponse e) {
				logger.debug(e.getMessage());
				disconnect();
			}
		}

		return this.specWrappers.toArray();
	}

	/**
	 * @return the aleServicePortType
	 */
	public ALEServicePortType getAleServicePortType() {
		return aleServicePortType;
	}

	public String getStandardVersion() {
		try {
			return this.aleServicePortType.getStandardVersion(new EmptyParms());
		} catch (ImplementationExceptionResponse e) {
			logger.debug(e.getMessage());
			return e.getMessage();
		}
	}

	public String getVendorVersion() {
		try {
			return this.aleServicePortType.getVendorVersion(new EmptyParms());
		} catch (ImplementationExceptionResponse e) {
			logger.debug(e.getMessage());
			return e.getMessage();
		}
	}

}

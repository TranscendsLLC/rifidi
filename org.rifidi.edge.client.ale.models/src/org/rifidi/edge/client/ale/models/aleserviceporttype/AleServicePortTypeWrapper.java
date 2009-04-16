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
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.connection.handler.ConnectionHandler;
import org.rifidi.edge.client.ale.connection.service.ConnectionService;
import org.rifidi.edge.client.ale.models.ecspec.EcSpecModelWrapper;
import org.rifidi.edge.client.ale.models.enums.ConnectionStatus;
import org.rifidi.edge.client.ale.models.exceptions.RifidiNoEndpointDefinedException;
import org.rifidi.edge.client.ale.models.listeners.ConnectionChangeListener;
import org.rifidi.edge.client.ale.models.serviceprovider.IEcSpecDataManager;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AleServicePortTypeWrapper implements ConnectionChangeListener,
		IEcSpecDataManager {

	private ALEServicePortType aleServicePortType = null;
	private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
	private WritableSet ecSpecs = new WritableSet();
	private ArrayList<ConnectionChangeListener> connectionChangeListeners = new ArrayList<ConnectionChangeListener>();
	private ConnectionService conSvc = null;
	private Log logger = LogFactory.getLog(AleServicePortTypeWrapper.class);
	private URL endpoint = null;
	private String name = "EdgeServer";

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 */
	public AleServicePortTypeWrapper(URL endpoint) {
		this.endpoint = endpoint;

	}

	public AleServicePortTypeWrapper() {

	}

	@Override
	public void connectionStatusChanged(ConnectionStatus status) {
		if (this.connectionStatus != status) {
			this.connectionStatus = status;
			logger.debug("Connection Status: " + status.toString());
			for (ConnectionChangeListener listener : connectionChangeListeners) {
				listener.connectionStatusChanged(status);
			}
		}
	}

	public void addConnectionChangeListener(ConnectionChangeListener listener) {
		this.connectionChangeListeners.add(listener);
	}

	public void removeConnectionChangeListener(ConnectionChangeListener listener) {
		this.connectionChangeListeners.remove(listener);
	}

	public void connect() throws RifidiNoEndpointDefinedException {
		logger.debug("Trying to connect...");
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
		logger.debug("disconnecting...");
		this.aleServicePortType = null;
		connectionStatusChanged(ConnectionStatus.DISCONNECTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.ale.models.serviceprovider.IEcSpecDataManager#
	 * getEcSpecNames()
	 */
	@Override
	public Object[] getEcSpecs() {
		if (this.aleServicePortType != null) {
			try {
				ArrayList<EcSpecModelWrapper> alEsWrs = new ArrayList<EcSpecModelWrapper>();
				for (String string : this.aleServicePortType.getECSpecNames(
						new EmptyParms()).getString()) {
					alEsWrs.add(new EcSpecModelWrapper(string, this));
				}
				if (ecSpecs.size() == 0) {
					ecSpecs.addAll(alEsWrs);
				} else
					ecSpecs.retainAll(alEsWrs);
			} catch (ImplementationExceptionResponse e) {
				logger.error(e.getMessage());
			} catch (SecurityExceptionResponse e) {
				logger.error(e.getMessage());
			}
		}

		return ecSpecs.toArray();
	}

	/**
	 * @return the aleServicePortType
	 */
	public ALEServicePortType getAleServicePortType() {
		return aleServicePortType;
	}

	public boolean isConnected() {
		return (this.connectionStatus == ConnectionStatus.CONNECTED);
	}

	public void addSetChangeListener(ISetChangeListener listener) {
		this.ecSpecs.addSetChangeListener(listener);
	}

	public void removeSetChangeListener(ISetChangeListener listener) {
		this.ecSpecs.removeSetChangeListener(listener);
	}
}

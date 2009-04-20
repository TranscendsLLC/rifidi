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
import java.util.ArrayList;

import javax.management.ServiceNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ALELRServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.connection.handler.ConnectionHandler;
import org.rifidi.edge.client.ale.connection.service.ConnectionService;
import org.rifidi.edge.client.ale.models.enums.ConnectionStatus;
import org.rifidi.edge.client.ale.models.exceptions.RifidiNoEndpointDefinedException;
import org.rifidi.edge.client.ale.models.listeners.ConnectionChangeListener;
import org.rifidi.edge.client.ale.models.lrspec.RemoteLrSpecModelWrapper;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AleLrServicePortTypeWrapper {

	private ALELRServicePortType aleLrServicePortType = null;
	private WritableSet logicalReaders = new WritableSet();
	private ConnectionService conSvc = null;
	/**
	 * 
	 */

	private Log logger = LogFactory.getLog(AleLrServicePortTypeWrapper.class);
	private URL endpoint = null;
	private String name = "EdgeServer";
	private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
	private ArrayList<ConnectionChangeListener> connectionChangeListeners = new ArrayList<ConnectionChangeListener>();

	public void connect() throws RifidiNoEndpointDefinedException {
		logger.debug("Trying to connect ALELR...");
		if (this.endpoint != null) {
			ConnectionHandler conHan = new ConnectionHandler();

			try {
				conSvc = conHan.getService();
				this.aleLrServicePortType = conSvc
						.getAleLrServicePortType(endpoint);
				connectionStatusChanged(ConnectionStatus.CONNECTED);
			} catch (ServiceNotFoundException e) {
				logger.error(e.getMessage());
			}
		} else
			throw new RifidiNoEndpointDefinedException();
	}

	/**
	 * @param connected
	 */
	private void connectionStatusChanged(ConnectionStatus status) {
		if (this.connectionStatus != status) {
			this.connectionStatus = status;
			logger.debug("Connection Status: " + status.toString());
			for (ConnectionChangeListener listener : this.connectionChangeListeners) {
				listener.connectionStatusChanged(status);
			}
		}

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
	 * @return the connectionStatus
	 */
	public ConnectionStatus getConnectionStatus() {
		return connectionStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.ale.models.serviceprovider.SpecDataManager#getSpecs
	 * ()
	 */
	public Object[] getSpecs() {
		if (this.aleLrServicePortType != null) {
			ArrayList<RemoteLrSpecModelWrapper> alLsWrs = new ArrayList<RemoteLrSpecModelWrapper>();
			try {
				for (String string : this.aleLrServicePortType
						.getLogicalReaderNames(new EmptyParms()).getString()) {
					alLsWrs.add(new RemoteLrSpecModelWrapper(string, this));
				}
				if (this.logicalReaders.size() == 0) {
					this.logicalReaders.addAll(alLsWrs);
				} else
					this.logicalReaders.retainAll(alLsWrs);
			} catch (SecurityExceptionResponse e) {
				logger.debug(e.getMessage());
				disconnect();
			} catch (ImplementationExceptionResponse e) {
				logger.debug(e.getMessage());
				disconnect();
			}

		}
		return this.logicalReaders.toArray();
	}

	public void disconnect() {
		logger.debug("disconnectiong LR...");
		this.aleLrServicePortType = null;
		connectionStatusChanged(ConnectionStatus.DISCONNECTED);
	}

	/**
	 * @return the aleLrServicePortType
	 */
	public ALELRServicePortType getAleLrServicePortType() {
		return aleLrServicePortType;
	}

	public void addConnectionChangeListener(ConnectionChangeListener listener) {
		this.connectionChangeListeners.add(listener);
	}

	public void removeConnectionChangeListener(ConnectionChangeListener listener) {
		this.connectionChangeListeners.remove(listener);
	}

	public void addSetChangeListener(ISetChangeListener listener) {
		this.logicalReaders.addSetChangeListener(listener);
	}

	public void removeSetChangeListener(ISetChangeListener listener) {
		this.logicalReaders.removeSetChangeListener(listener);
	}

	public String getStandardVersion() {
		try {
			return this.aleLrServicePortType
					.getStandardVersion(new EmptyParms());
		} catch (ImplementationExceptionResponse e) {
			logger.debug(e.getMessage());
			return e.getMessage();
		}
	}

	public String getVendorVersion() {
		try {
			return this.aleLrServicePortType.getVendorVersion(new EmptyParms());
		} catch (ImplementationExceptionResponse e) {
			logger.debug(e.getMessage());
			return e.getMessage();
		}

	}

	public boolean isConnected() {
		return (this.connectionStatus == ConnectionStatus.CONNECTED);
	}

}

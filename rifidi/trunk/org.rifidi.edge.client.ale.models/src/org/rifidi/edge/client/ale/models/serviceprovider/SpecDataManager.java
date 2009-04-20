/* 
 *  SpecDataManager.java
 *  Created:	Apr 17, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.models
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.models.serviceprovider;

import java.util.ArrayList;

import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.rifidi.edge.client.ale.models.enums.ConnectionStatus;
import org.rifidi.edge.client.ale.models.listeners.ConnectionChangeListener;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public abstract class SpecDataManager {
	protected WritableSet specWrappers = new WritableSet();
	protected String name = "";
	protected ArrayList<ConnectionChangeListener> connectionChangeListeners = new ArrayList<ConnectionChangeListener>();
	protected ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
	
	/**
	 * @return the connectionStatus
	 */
	public ConnectionStatus getConnectionStatus() {
		return connectionStatus;
	}
	
	protected void connectionStatusChanged(ConnectionStatus status) {
		if (this.connectionStatus != status) {
			this.connectionStatus = status;
//			logger.debug("Connection Status: " + status.toString());
			for (ConnectionChangeListener listener : getConnectionChangeListeners()) {
				listener.connectionStatusChanged(status);
			}
		}
	}
	public boolean isConnected(){
		return (this.connectionStatus==ConnectionStatus.CONNECTED);
	}

	/**
	 * @return the connectionChangeListeners
	 */
	public ArrayList<ConnectionChangeListener> getConnectionChangeListeners() {
		return connectionChangeListeners;
	}

	/**
	 * @return the specWrappers
	 */
	protected WritableSet getSpecWrappers() {
		return specWrappers;
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

	public abstract Object[] getSpecs();
	
	public void addConnectionChangeListener(ConnectionChangeListener listener) {
		this.connectionChangeListeners.add(listener);
	}

	public void removeConnectionChangeListener(ConnectionChangeListener listener) {
		this.connectionChangeListeners.remove(listener);
	}
	
	public void addSetChangeListener(ISetChangeListener listener) {
		this.specWrappers.addSetChangeListener(listener);
	}

	public void removeSetChangeListener(ISetChangeListener listener) {
		this.specWrappers.removeSetChangeListener(listener);
	}
}

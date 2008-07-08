package org.rifidi.edge.readerplugin.thingmagic;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.communication.service.ConnectionEventListener;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionStreams;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;
import org.rifidi.edge.readerplugin.thingmagic.protocol.ThingMagicCommunicationProtocol;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class ThingMagicManager extends ConnectionManager {
	Set<ConnectionEventListener> listeners = Collections.synchronizedSet(new HashSet<ConnectionEventListener>());

	public ThingMagicManager(ReaderInfo readerInfo) {
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void connect(ReaderInfo readerInfo) throws RifidiConnectionException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addConnectionEventListener(ConnectionEventListener event) {
		listeners.add(event);	
	}
	

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#disconnect()
	 */
	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#getCommunicationProtocol()
	 */
	@Override
	public CommunicationProtocol getCommunicationProtocol() {
		// TODO Auto-generated method stub
		return new ThingMagicCommunicationProtocol();
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#getMaxNumConnectionsAttemps()
	 */
	@Override
	public int getMaxNumConnectionsAttemps() {
		// TODO Auto-generated method stub
		return 3;
	}
	@Override
	public long getReconnectionIntervall() {
		// TODO Auto-generated method stub
		return 1000;
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#removeConnectionEventListener(org.rifidi.edge.core.communication.service.ConnectionEventListener)
	 */
	@Override
	public void removeConnectionEventListener(ConnectionEventListener event) {
		listeners.remove(event);
		
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#startKeepAlive()
	 */
	@Override
	public void startKeepAlive() {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#stopKeepAlive()
	 */
	@Override
	public void stopKeepAlive() {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionExceptionListener#connectionExceptionEvent(java.lang.Exception)
	 */
	@Override
	public void connectionExceptionEvent(Exception exception) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConnectionStreams createCommunication(ReaderInfo readerInfo)
			throws RifidiConnectionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void fireEvent() {
		// TODO Auto-generated method stub
		
	}



}

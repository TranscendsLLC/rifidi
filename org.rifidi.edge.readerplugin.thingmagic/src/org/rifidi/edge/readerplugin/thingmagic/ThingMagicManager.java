package org.rifidi.edge.readerplugin.thingmagic;


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
	

	public ThingMagicManager(ReaderInfo readerInfo) {
		super(readerInfo);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void addConnectionEventListener(ConnectionEventListener event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void connect() throws RifidiConnectionException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ConnectionStreams createCommunication()
			throws RifidiConnectionException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public CommunicationProtocol getCommunicationProtocol() {
		// TODO Auto-generated method stub
		return new ThingMagicCommunicationProtocol();
	}
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
		// TODO Auto-generated method stub
		
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



}

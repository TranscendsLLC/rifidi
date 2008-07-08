package org.rifidi.edge.readerplugin.thingmagic;


import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


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
		info = (ThingMagicReaderInfo) readerInfo;
	}

	private Socket socket;
	private ThingMagicReaderInfo info;
	
	@Override
	public void connect() throws RifidiConnectionException {
		// TODO Auto-generated method stub

		
		//TODO Fire events;
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#disconnect()
	 */
	@Override
	public void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#getCommunicationProtocol()
	 */
	@Override
	public CommunicationProtocol getCommunicationProtocol() {
		return new ThingMagicCommunicationProtocol();
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#getMaxNumConnectionsAttemps()
	 */
	@Override
	public int getMaxNumConnectionsAttemps() {
		//TODO implement this.
		return 3;
	}
	@Override
	public long getReconnectionIntervall() {
		// TODO implement this.
		return 1000;
	}
	

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#startKeepAlive()
	 */
	@Override
	public void startKeepAlive() {
		// ignore this.
		
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#stopKeepAlive()
	 */
	@Override
	public void stopKeepAlive() {
		// ignore this.
		
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionExceptionListener#connectionExceptionEvent(java.lang.Exception)
	 */
	@Override
	public void connectionExceptionEvent(Exception exception) {
		// TODO What do we do with this???
	}

	@Override
	public ConnectionStreams createCommunication()
			throws RifidiConnectionException {
		// TODO Auto-generated method stub
		
		
		try {
			return new ConnectionStreams(socket.getInputStream(), socket.getOutputStream());
		} catch (IOException e) {
			throw new RifidiConnectionException(e);
		}
	}


}

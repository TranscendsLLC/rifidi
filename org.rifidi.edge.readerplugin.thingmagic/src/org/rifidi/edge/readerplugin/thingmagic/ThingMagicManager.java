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

	private Socket socket;
	private ThingMagicReaderInfo info;
	
	@Override
	public void connect(ReaderInfo readerInfo) throws RifidiConnectionException {
		// TODO Auto-generated method stub
		info = (ThingMagicReaderInfo) readerInfo;
		
		if (!info.isSsh()) {
			try {
				socket = new Socket(info.getIpAddress(), info.getPort());
			} catch (UnknownHostException e) {
				throw new RifidiConnectionException(e);
			} catch (IOException e) {
				throw new RifidiConnectionException(e);
			}
		} else {
			//TODO implement connection to reader by ssh
			throw new UnsupportedOperationException("Connections to Merucry 4 or 5, ThingMagic readers by ssh not impemented.");
		}
		
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
		return 3;
	}
	@Override
	public long getReconnectionIntervall() {
		// TODO Auto-generated method stub
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
	public ConnectionStreams createCommunication(ReaderInfo readerInfo)
			throws RifidiConnectionException {
		// TODO Auto-generated method stub
		try {
			return new ConnectionStreams(socket.getInputStream(), socket.getOutputStream());
		} catch (IOException e) {
			throw new RifidiConnectionException(e);
		}
	}

	@Override
	protected void fireEvent() {
		// TODO Cannot tell if this is a fire disconnect event or fire connect event...
		
	}



}

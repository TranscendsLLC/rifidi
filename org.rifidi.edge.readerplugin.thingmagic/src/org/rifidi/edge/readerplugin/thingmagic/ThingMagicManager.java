package org.rifidi.edge.readerplugin.thingmagic;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;
import org.rifidi.edge.readerplugin.thingmagic.protocol.ThingMagicCommunicationProtocol;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class ThingMagicManager implements ConnectionManager {
	

	private ReaderInfo readerInfo;
	private Connection connection;

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#connect(org.rifidi.edge.core.readerplugin.ReaderInfo, org.rifidi.edge.core.communication.Connection)
	 */
	@Override
	public void connect(ReaderInfo readerInfo, Connection connection) {
		// TODO: Check if it is a real Mercury 4 or 5, ThingMagic reader.
		// TODO: Add support for connecting to the reader over SSH.
		
		this.readerInfo = readerInfo;
		this.connection = connection;
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
		return new ThingMagicCommunicationProtocol();
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#getConnectionAttemptInterval()
	 */
	@Override
	public long getConnectionAttemptInterval() {
		// TODO Auto-generated method stub
		return 500;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#getMaxConnectionAttemps()
	 */
	@Override
	public int getMaxConnectionAttemps() {
		// TODO Auto-generated method stub
		return 3;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#reconnect()
	 */
	@Override
	public void reconnect() {
		disconnect();
		connect(readerInfo, connection);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#startKeepAlive()
	 */
	@Override
	public void startKeepAlive() {
		// TODO Auto-generated method stub
		
	}

}

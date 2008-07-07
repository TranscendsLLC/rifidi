package org.rifidi.edge.readerplugin.thingmagic;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;
import org.rifidi.edge.readerplugin.thingmagic.protocol.ThingMagicCommunicationProtocol;

public class ThingMagicManager implements ConnectionManager {

	@Override
	public void connect(ReaderInfo readerInfo, Connection connection) {
		// TODO Auto-generated method stub

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
	public long getConnectionAttemptInterval() {
		// TODO Auto-generated method stub
		return 500;
	}

	@Override
	public int getMaxConnectionAttemps() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public void reconnect() {
		// TODO Auto-generated method stub

	}

}

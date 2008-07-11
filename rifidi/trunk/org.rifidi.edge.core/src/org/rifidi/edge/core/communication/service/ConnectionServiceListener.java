package org.rifidi.edge.core.communication.service;

import org.rifidi.edge.core.communication.Connection;

public interface ConnectionServiceListener {
	
	public void addEvent(Connection connection);

	public void removeEvent(Connection connection);
	
}

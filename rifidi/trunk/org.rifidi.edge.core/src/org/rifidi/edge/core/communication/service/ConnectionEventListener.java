package org.rifidi.edge.core.communication.service;


import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionExceptionListener;



public interface ConnectionEventListener extends ConnectionExceptionListener {
	
	public void connected();
	
	public void disconnected();
	
	public void disconnectedOnError();
	
}

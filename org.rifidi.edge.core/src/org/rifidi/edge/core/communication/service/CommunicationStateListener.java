package org.rifidi.edge.core.communication.service;


public interface CommunicationStateListener{

	public void connected();

	public void disconnected();

	public void error();

}

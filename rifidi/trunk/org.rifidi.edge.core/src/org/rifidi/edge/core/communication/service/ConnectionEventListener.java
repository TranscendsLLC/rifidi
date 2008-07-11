package org.rifidi.edge.core.communication.service;


public interface ConnectionEventListener{

	public void connected();

	public void disconnected();

	public void error();

}

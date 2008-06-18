package org.rifidi.edge.rmi.service;

import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection;

public interface RMIServerService {

	public void start();

	public void stop();

	public void bind(String url, RemoteReaderConnection remoteReaderConnection);
	
	public void unbind(String url);
}

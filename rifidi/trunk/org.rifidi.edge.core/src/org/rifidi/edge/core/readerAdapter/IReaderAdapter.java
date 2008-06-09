package org.rifidi.edge.core.readerAdapter;

public interface IReaderAdapter {

	public void connect();

	public void disconnect();

	public void sendCommand();

	public void startStreamTags();

	public void stopStreamTags();
	
}

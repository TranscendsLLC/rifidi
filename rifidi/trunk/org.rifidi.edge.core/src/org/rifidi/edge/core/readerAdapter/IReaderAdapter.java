package org.rifidi.edge.core.readerAdapter;

import java.util.List;

import org.rifidi.edge.core.tag.TagRead;

public interface IReaderAdapter {

	public void connect();

	public void disconnect();

	public void sendCommand(byte[] command);

	public void startStreamTags();

	public void stopStreamTags();
	
	public List<TagRead> getNextTags();
	
	public boolean isBlocking();
	
}

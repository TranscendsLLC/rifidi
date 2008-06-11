package org.rifidi.edge.core.readerAdapter;

import java.util.List;

import org.rifidi.edge.core.tag.TagRead;

public interface IReaderAdapter {

	public boolean connect();

	public boolean disconnect();

	public void sendCommand(byte[] command);

	public List<TagRead> getNextTags();
	
	public boolean isBlocking();
	
}

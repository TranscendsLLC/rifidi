package org.rifidi.edge.core.adapter.dummyadapter;

import java.util.List;

import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.tag.TagRead;

public class DummyReaderAdapter implements IReaderAdapter {

	@Override
	public void connect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<TagRead> getNextTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendCommand(byte[] command) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startStreamTags() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopStreamTags() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isBlocking() {
		// TODO Auto-generated method stub
		return false;
	}

}

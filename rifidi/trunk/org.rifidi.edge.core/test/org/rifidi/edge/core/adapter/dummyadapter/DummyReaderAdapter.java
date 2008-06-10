package org.rifidi.edge.core.adapter.dummyadapter;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.tag.TagRead;

public class DummyReaderAdapter implements IReaderAdapter {
	
	boolean connected = false;

	@Override
	public void connect() {
		connected = true;

	}

	@Override
	public void disconnect() {
		connected=false;

	}

	@Override
	public List<TagRead> getNextTags() {
		if(connected){
			TagRead tr = new TagRead();
			byte[] b = {0x01, 0x02, 0x03, 0x04 };
			tr.setId(b);
			tr.setLastSeenTime(System.currentTimeMillis());
			ArrayList<TagRead> reads = new ArrayList<TagRead>();
			reads.add(tr);
			return reads;
		}
		return null;
	}

	@Override
	public void sendCommand(byte[] command) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startStreamTags() {

		

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

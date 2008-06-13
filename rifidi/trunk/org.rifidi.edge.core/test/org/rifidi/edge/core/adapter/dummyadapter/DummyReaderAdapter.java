package org.rifidi.edge.core.adapter.dummyadapter;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommand;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;

public class DummyReaderAdapter implements IReaderAdapter {
	
	boolean connected = false;

	@Override
	public boolean connect() {
		connected = true;
		return true;
	}

	@Override
	public boolean disconnect() {
		connected=false;
		return true;
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
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand) {
		// TODO Jerry Dummy method need to be implemented
		return null;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

}

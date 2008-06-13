package org.rifidi.edge.core.adapter.dummyadapter;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.adapter.RifidiAdapterIllegalStateException;
import org.rifidi.edge.core.exception.adapter.RifidiConnectionException;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommand;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;

public class DummyReaderAdapter implements IReaderAdapter {
	
	boolean connected = false;

	@Override
	public void connect() throws RifidiConnectionException {
		connected = true;
	}

	@Override
	public void disconnect() throws RifidiConnectionException {
		connected=false;
	}

	@Override
	public List<TagRead> getNextTags() throws RifidiAdapterIllegalStateException {
		
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
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand) 
		throws RifidiAdapterIllegalStateException, RifidiIIllegialArgumentException
	{
		// TODO Jerry Dummy method need to be implemented
		return null;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

}

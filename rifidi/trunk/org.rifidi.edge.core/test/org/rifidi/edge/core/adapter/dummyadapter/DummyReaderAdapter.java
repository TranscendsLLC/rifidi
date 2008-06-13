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

	private EDummyError error;

	private boolean connected = false;

	public DummyReaderAdapter() {
		error = EDummyError.NONE;
	}

	@Override
	public void connect() throws RifidiConnectionException {
		switch (error) {
			case CONNECT:
				throw new RifidiConnectionException();
			case CONNECT_RUNTIME:
				throw new RuntimeException();
		}
		connected = true;
	}

	@Override
	public void disconnect() throws RifidiConnectionException {
		switch (error) {
			case DISCONNECT:
				throw new RifidiConnectionException();
			case DISCONNECT_RUNTIME:
				throw new RuntimeException();
		}
		connected = false;
	}

	@Override
	public List<TagRead> getNextTags()
			throws RifidiAdapterIllegalStateException {
		switch (error) {
			case GET_NEXT_TAGS:
				throw new RifidiAdapterIllegalStateException();
			case GET_NEXT_TAGS_RUNTIME:
				throw new RuntimeException();
		}

		if (connected) {
			TagRead tr = new TagRead();
			byte[] b = { 0x01, 0x02, 0x03, 0x04 };
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
			throws RifidiAdapterIllegalStateException,
			RifidiIIllegialArgumentException {
		switch (error) {
			case SEND_CUSTOM_COMMAND:
				throw new RifidiAdapterIllegalStateException();
			case SEND_CUSTOM_COMMAND2:
				throw new RifidiIIllegialArgumentException();
			case SEND_CUSTOM_COMMAND_RUNTIME:
				throw new RuntimeException();
		}

		if (!(customCommand instanceof DummyCustomCommand))
			throw new RifidiIIllegialArgumentException();

		DummyCustomCommand command = (DummyCustomCommand) customCommand;

		DummyCustomCommandResult result = new DummyCustomCommandResult();

		result.setResult(command.getCommand() + " <Result> ");

		return result;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	public void setError(EDummyError error) {
		this.error = error;
	}
}

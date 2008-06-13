package org.rifidi.edge.core.readerAdapter;

import java.util.List;

import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.adapter.RifidiAdapterIllegalStateException;
import org.rifidi.edge.core.exception.adapter.RifidiConnectionException;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommand;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;

public interface IReaderAdapter {

	public void connect() throws RifidiConnectionException;

	public void disconnect() throws RifidiConnectionException;

	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand)
		throws RifidiAdapterIllegalStateException, RifidiIIllegialArgumentException;

	public List<TagRead> getNextTags() throws RifidiAdapterIllegalStateException;
	
	public boolean isBlocking();
	
}

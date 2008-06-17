package org.rifidi.edge.core.readerPlugin;

import java.util.List;

import org.rifidi.edge.core.exception.RifidiAdapterIllegalStateException;
import org.rifidi.edge.core.exception.RifidiConnectionException;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;

public interface IReaderPlugin {

	public void connect() throws RifidiConnectionException;

	public void disconnect() throws RifidiConnectionException;

	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand)
		throws RifidiAdapterIllegalStateException, RifidiIIllegialArgumentException;

	public List<TagRead> getNextTags() throws RifidiAdapterIllegalStateException;
	
	public boolean isBlocking();
	
}

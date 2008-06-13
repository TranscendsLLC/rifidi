package org.rifidi.edge.core.readerAdapter;

import java.util.List;

import org.rifidi.edge.core.readerAdapter.commands.ICustomCommand;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;

public interface IReaderAdapter {

	public boolean connect();

	public boolean disconnect();

	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand);

	public List<TagRead> getNextTags();
	
	public boolean isBlocking();
	
}

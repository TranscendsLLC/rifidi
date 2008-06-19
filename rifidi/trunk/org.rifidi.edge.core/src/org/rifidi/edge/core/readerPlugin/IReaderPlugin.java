package org.rifidi.edge.core.readerPlugin;

import java.util.List;

import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;

public interface IReaderPlugin {

	/**
	 * @throws RifidiConnectionException
	 */
	public void connect() throws RifidiConnectionException;

	/**
	 * @throws RifidiConnectionException
	 */
	public void disconnect() throws RifidiConnectionException;

	/**
	 * @param customCommand
	 * @return
	 * @throws RifidiConnectionIllegalStateException
	 * @throws RifidiIIllegialArgumentException
	 */
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand)
		throws RifidiConnectionIllegalStateException, RifidiIIllegialArgumentException;

	/**
	 * @return
	 * @throws RifidiConnectionIllegalStateException
	 */
	public List<TagRead> getNextTags() throws RifidiConnectionIllegalStateException;
	
	/**
	 * @return
	 */
	public boolean isBlocking();
	
}

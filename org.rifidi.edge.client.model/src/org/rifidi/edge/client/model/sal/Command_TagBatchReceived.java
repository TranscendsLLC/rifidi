/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.core.api.tags.TagBatch;
import org.rifidi.edge.core.api.tags.TagDTO;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Command_TagBatchReceived implements RemoteEdgeServerCommand {

	private RemoteEdgeServer edgeServer;
	private TagBatch batch;
	private Set<RemoteTag> tags;

	public Command_TagBatchReceived(RemoteEdgeServer server, TagBatch batch) {
		this.edgeServer = server;
		this.batch = batch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#execute
	 * ()
	 */
	@Override
	public void execute() {
		tags = new HashSet<RemoteTag>();
		for (TagDTO dto : batch.getTags()) {
			tags.add(new RemoteTag(dto, edgeServer.tdtEngine));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#
	 * executeEclipse()
	 */
	@Override
	public void executeEclipse() {
		RemoteReader reader = (RemoteReader) edgeServer.getRemoteReaders().get(
				batch.getReaderID());
		if (reader != null) {
			// remove all tags that are not in the new set
			reader.getTags().retainAll(tags);
			// add all the new tags
			reader.getTags().addAll(tags);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#getType
	 * ()
	 */
	@Override
	public String getType() {
		return "TAG_BATCH_RECEIVED";
	}

}

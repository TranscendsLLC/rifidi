package org.rifidi.edge.readerplugin.thingmagic.commands;

import java.io.IOException;

import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;

@CommandDesc(name="GetTagsCurrentlyOnAntennas")
public class GetTagsOnceCommand implements Command {

	@Override
	public void start(Connection connection, MessageQueue messageQueue) {
		try {
			connection.sendMessage("select id, timestamp from tag_id;\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String recieved = "";
		try {
			recieved = (String) connection.recieveMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ( !recieved.equals("") ) {
			String[] rawTags = recieved.split("\n");
			for (String rawTag: rawTags){
				//logger.debug(rawTag);
				
				//All tag data sent back is separated by vertical bars.
				String[] rawTagItems = rawTag.split("\\|");
				
				TagMessage tag = new TagMessage();
				
				
				//logger.debug(rawTagItems[0]);
				
				tag.setId(ByteAndHexConvertingUtility.fromHexString(rawTagItems[0].substring(2, rawTagItems[0].length())));
				
				//TODO: correct the time stamps.
				tag.setLastSeenTime(System.nanoTime()); 
				messageQueue.addMessage(tag);
			}
			
		}

	}

	@Override
	public void stop() {
		// Do Nothing

	}

}

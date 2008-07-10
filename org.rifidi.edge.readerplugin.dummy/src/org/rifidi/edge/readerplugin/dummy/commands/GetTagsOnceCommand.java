package org.rifidi.edge.readerplugin.dummy.commands;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;

@CommandDesc(name="GetTagsCurrentlyOnAntennas")
public class GetTagsOnceCommand implements Command {
	private static final Log logger = LogFactory.getLog(GetTagsOnceCommand.class);
	boolean running = true;
	
	@Override
	public CommandReturnStatus start(Connection connection, MessageQueue messageQueue) {
		logger.debug("Getting tags.");
		//TODO: Need to set this up properly.
//		switch (info.getErrorToSet()) {
//			case GET_NEXT_TAGS:
//				throw new IOException();
//			case GET_NEXT_TAGS_RUNTIME:
//				throw new RuntimeException();
//			case RANDOM:
//				if (random.nextDouble() <= info.getRandomErrorProbibility()){
//					if(random.nextDouble() <= info.getProbiblityOfErrorsBeingRuntimeExceptions()){
//						throw new RuntimeException();
//					} else {
//						throw new IOException();
//					}
//				}
//		}
		
		String rawtag = ByteAndHexConvertingUtility.toHexString("Hallo".getBytes()).replace(" ", "") +
						"|" + 1565467895l + "\n\n";
		
		try {
			connection.sendMessage(rawtag);
			rawtag = (String) connection.recieveMessage();
		} catch (IOException e1) {
			return CommandReturnStatus.INTERRUPTED;
		}
		
		
		if ( !rawtag.equals("") ) {
			String[] rawTags = rawtag.split("\n");
			for (String rawTag: rawTags){
				//logger.debug(rawTag);
				
				//All tag data sent back is separated by vertical bars.
				String[] rawTagItems = rawTag.split("\\|");
				
				TagMessage tag = new TagMessage();
				
				
				//logger.debug(rawTagItems[0]);
				
				tag.setId(ByteAndHexConvertingUtility.fromHexString(rawTagItems[0].substring(2, rawTagItems[0].length())));
				
				//TODO: correct the time stamps.
				tag.setLastSeenTime(System.nanoTime()); 
				//logger.debug(tag.toXML());
				try {
					messageQueue.addMessage(tag);
				} catch (RifidiMessageQueueException e) {
					return CommandReturnStatus.INTERRUPTED;
				}
			}
			
		}
		
		return CommandReturnStatus.SUCCESSFUL;
	}

	@Override
	public void stop() {
		// Do Nothing

	}

}

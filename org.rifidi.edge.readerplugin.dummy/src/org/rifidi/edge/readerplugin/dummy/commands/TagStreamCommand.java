package org.rifidi.edge.readerplugin.dummy.commands;

import java.io.IOException;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;

@CommandDesc(name="TagStreaming")
public class TagStreamCommand implements Command {

	boolean running = true;
	private MessageQueue queue;

	@Override
	public void start(Connection connection, MessageQueue messageQueue) throws IOException{
		//TODO: Need to set this up properly.
//		switch (info.getErrorToSet()) {
//			case GET_NEXT_TAGS:
//				throw new RifidiConnectionIllegalStateException();
//			case GET_NEXT_TAGS_RUNTIME:
//				throw new RuntimeException();
//			case RANDOM:
//				if (random.nextDouble() <= info.getRandomErrorProbibility()){
//					if(random.nextDouble() <= info.getProbiblityOfErrorsBeingRuntimeExceptions()){
//						throw new RuntimeException();
//					} else {
//						throw new RifidiConnectionIllegalStateException();
//					}
//				}
//		}
		System.out.println("TagStreaming is running!");
		this.queue = messageQueue;
		TagMessage message = new TagMessage();
		message.setId("Hallo".getBytes());
		message.setLastSeenTime(1565467895l);
		while (running){
			try {
				queue.addMessage(message);
			} catch (RifidiMessageQueueException e) {
				e.printStackTrace();
				//TODO make quit better
				return;
			}
		}
		System.out.println("TagStreaming is stopped");
	
	}

	@Override
	public void stop() {
		running = false;
	}

}

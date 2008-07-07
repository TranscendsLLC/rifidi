package org.rifidi.edge.readerplugin.dummy.commands;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;

@CommandDesc(name="TagStreaming")
public class TagStreamCommand implements Command {

	private Thread thread;
	private MessageQueue queue;

	@Override
	public void start(Connection connection, MessageQueue messageQueue) {
		System.out.println("TagStreaming is running!");
		this.queue = messageQueue;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				TagMessage message = new TagMessage();
				message.setId("Hallo".getBytes());
				message.setLastSeenTime(1565467895l);
				try {
					while (!Thread.interrupted()) {
						Thread.sleep(1000);
						queue.addMessage(message);
					}
				} catch (InterruptedException e) {
					// Ignore
				}
				System.out.println("TagStreaming is stopped");
			}
		});
		thread.start();
		
	
	}

	@Override
	public void stop() {
		thread.interrupt();
	}

}

/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.autonomous;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.threads.ReadThread;
import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.readerplugin.alien.AlienMessageParser;
import org.rifidi.edge.readerplugin.alien.messages.AlienMessage;
import org.rifidi.edge.readerplugin.alien.messages.AlienTag;
import org.rifidi.edge.readerplugin.alien.messages.AlienTagReadEventFactory;
import org.rifidi.edge.readerplugin.alien.messages.ReadCycleMessageCreator;
import org.springframework.jms.core.JmsTemplate;

/**
 * This class is a handler for a single open socket with an Alien reader that is
 * sending data when it is in autonomous mode
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienAutonomousHandler implements Runnable {

	/** A class used to read from the socket */
	private ReadThread readThread;
	/** A shared queue between the readthread and this handler */
	private LinkedBlockingQueue<ByteMessage> queue;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(AlienAutonomousHandler.class);
	/** Client socket */
	private Socket socket;
	/** Session for this Handler */
	private SensorSession session;
	/** A factory for converting alien tag reads to TagReadEvents */
	private AlienTagReadEventFactory tagReadEventFactory;
	/** Template for sending out tag reads */
	private JmsTemplate template;

	/**
	 * Create a new Handler for a connection with an Alien Reader in autonomous
	 * mode
	 * 
	 * @param stream
	 *            The input stream from the socket
	 * @throws IOException
	 */
	public AlienAutonomousHandler(Socket clientSocket, SensorSession session,
			JmsTemplate template) throws IOException {
		queue = new LinkedBlockingQueue<ByteMessage>();
		this.socket = clientSocket;
		readThread = new ReadThread(new AlienMessageParser(), clientSocket
				.getInputStream(), queue);
		this.session = session;
		this.template = template;
		this.tagReadEventFactory = new AlienTagReadEventFactory(session
				.getSensor().getID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// create the reader thread and start it
		final Thread readerThread = new Thread(readThread);
		readerThread.start();

		// get the current thread so we can interrupt it with the gaurdian
		final Thread autoModeThread = Thread.currentThread();
		// create a gaurdian that interrupts this thread should the read thread
		// stop before this one is interrupted
		Thread gaurdian = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// wait on readerthread to stop
					readerThread.join();
					// interrupt the other thread
					autoModeThread.interrupt();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
		gaurdian.start();

		// process messages while this thread has not been interrupted
		try {
			while (!Thread.interrupted()) {
				// take messages from the shared queue
				ByteMessage message = queue.take();
				handleMessage(new String(message.message));
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			try {
				socket.close();
				logger.info("Socket: " + socket.getRemoteSocketAddress()
						+ " closed. Exiting AlienAutonomousHandler");
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Private helper method to do work of converting a tag and sending message
	 * 
	 * @param rawMessage
	 */
	private void handleMessage(String rawMessage) {
		AlienMessage message = new AlienMessage(rawMessage);
		Set<TagReadEvent> tagReadEvents = new HashSet<TagReadEvent>();
		for (AlienTag tag : message.getTagList()) {
			tagReadEvents.add(tagReadEventFactory.getTagReadEvent(tag));
		}
		ReadCycle readCycle = new ReadCycle(tagReadEvents, session.getSensor()
				.getID(), System.currentTimeMillis());
		session.getSensor().send(readCycle);
		template.send(new ReadCycleMessageCreator(readCycle));
	}
}

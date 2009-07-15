/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.autonomous;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.threads.AbstractReadThread;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.readerplugin.alien.AlienMessageParsingStrategy;
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
public class AlienAutonomousReadThread extends AbstractReadThread {

	/** The logger for this class */
	private Log logger = LogFactory.getLog(AlienAutonomousReadThread.class);
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
	public AlienAutonomousReadThread(Socket clientSocket,
			SensorSession session, JmsTemplate template) throws IOException {
		super(clientSocket.getInputStream(), new AlienMessageParsingStrategy());
		this.socket = clientSocket;
		this.session = session;
		this.template = template;
		this.tagReadEventFactory = new AlienTagReadEventFactory(session
				.getSensor().getID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.base.threads.AbstractReadThread#run()
	 */
	@Override
	public void run() {
		// process messages while this thread has not been interrupted
		try {
			super.run();
		} finally {
			try {
				socket.close();
				logger.info("Socket: " + socket.getRemoteSocketAddress()
						+ " closed. Exiting AlienAutonomousReadThread");
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.threads.AbstractReadThread#processMessage
	 * (byte[])
	 */
	@Override
	protected void processMessage(byte[] byteMessage) {
		String rawMessage = new String(byteMessage);
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

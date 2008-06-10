package org.rifidi.edge.core.session.jms;

import javax.jms.MessageProducer;

public class JMSMessageThread implements Runnable {

	private MessageProducer messageProducer;

	public JMSMessageThread(MessageProducer messageProducer) {
		this.messageProducer = messageProducer;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}

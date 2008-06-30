package org.rifidi.edge.junit.jms.service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.jms.service.helper.JMSHelper;
import org.rifidi.edge.jms.service.threads.JMSMessageThread;
import org.rifidi.edge.readerplugin.dummy.DummyProtocol;
import org.rifidi.edge.readerplugin.dummy.DummyReaderInfo;
import org.rifidi.edge.readerplugin.dummy.DummyReaderPlugin;

public class JMSSessionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Make sure we can set and get the JMSThread
	 */
	@Test
	public void testJMSThread() {
		JMSHelper jmsHelper = new JMSHelper();
		DummyReaderInfo info = new DummyReaderInfo();
		info.setIPAddress("127.0.0.1");
		info.setPort(10001);

		if (connectionFactory == null) {
			Assert.fail("Coudn't get connection Factory out of Services");
		}

		jmsHelper.initializeJMSQueue(connectionFactory, "test");

		if (!jmsHelper.isInitialized()) {
			Assert.fail("Error while initializing jmsHelper.");
		}

		DummyReaderPlugin adapter = new DummyReaderPlugin(info);

		try {
			adapter.connect(communicationService.createConnection(adapter,
					info, new DummyProtocol()));
		} catch (RifidiConnectionException e1) {
			Assert.fail();
			e1.printStackTrace();
		} catch (ConnectException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		JMSMessageThread jmsThread = new JMSMessageThread(1, adapter, jmsHelper);
		if (!jmsThread.start()) {
			Assert.fail("Error while starting JMSThread");
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			MessageConsumer consumer = jmsHelper.getSession().createConsumer(
					jmsHelper.getDestination());

			// Get the next message from the queue
			Message m = consumer.receive(500);

			logger.info(((TextMessage) m).getText() + "== END ==");

			m = consumer.receive(5000);

			logger.info(((TextMessage) m).getText() + "== END ==");

		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail();
		}

		jmsThread.stop();
		adapter.dispose();
	}

}

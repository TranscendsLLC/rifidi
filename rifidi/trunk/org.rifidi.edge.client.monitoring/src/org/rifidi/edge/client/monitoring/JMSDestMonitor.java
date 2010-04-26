/**
 * 
 */
package org.rifidi.edge.client.monitoring;

import java.awt.font.TextMeasurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.rifidi.edge.client.model.SALModelPlugin;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * @author kyle
 * 
 */
public class JMSDestMonitor implements MessageListener {

	private DefaultMessageListenerContainer container;
	private ConsoleManager console;

	public JMSDestMonitor() {
		this.console = new ConsoleManager();
	}

	public void refresh() {
		if (this.container != null) {
			this.container.stop();
			this.container.destroy();
			this.container = null;
		}

		String ip = SALModelPlugin.getDefault().getPreferenceStore().getString(
				"org.rifidi.edge.client.server.ip");
		String port = SALModelPlugin.getDefault().getPreferenceStore()
				.getString("org.rifidi.edge.client.server.port.jms");
		this.container = new DefaultMessageListenerContainer();
		this.container.setMessageListener(this);
		this.container.setDestination(new ActiveMQTopic("org.rifidi.tracking"));
		ActiveMQConnectionFactory connFac = new ActiveMQConnectionFactory();
		connFac.setBrokerURL("tcp://" + ip + ":" + port);
		this.container.setConnectionFactory(connFac);
		this.container.afterPropertiesSet();
		this.container.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message arg0) {
		if (arg0 instanceof TextMessage) {
			TextMessage tm = (TextMessage) arg0;
			try {
				this.console.writeMessage(tm.getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

	}

}

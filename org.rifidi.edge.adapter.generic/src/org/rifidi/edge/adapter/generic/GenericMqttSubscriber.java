/**
 * 
 */
package org.rifidi.edge.adapter.generic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.rifidi.edge.adapter.generic.strategy.TagParsingStrategy;

/**
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class GenericMqttSubscriber {

	/** Logger */
	private final Log logger = LogFactory.getLog(getClass());

	private MqttClient client = null;
	private Boolean restdebug;
	private GenericSensorSession session;
	private String topic;
	private SimpleMqttCallback callback;

	public GenericMqttSubscriber(String uri, String topic, String clientID, Boolean restdebug, GenericSensorSession session) {
		this.restdebug = restdebug;
		this.session = session;
		this.topic = topic;
		try {
			client = new MqttClient(uri, clientID);
		} catch (MqttException e) {
			logger.error("Exception trying to connect to mqtt server: " + uri);
		}
		this.callback = new SimpleMqttCallback();
	}
	
	public void connect() {
		try {
			client.connect();
			client.subscribe(this.topic);
			client.setCallback(this.callback);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public class SimpleMqttCallback implements MqttCallback {

		public void connectionLost(Throwable throwable) {
			logger.error("Generic MQTT Connection lost, reconnecting: " + client.getServerURI());	
//			try {
//				Thread.sleep(3000);
//				connect();
//			} catch (InterruptedException e) {
//			}
		}

		public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
			if(restdebug) {
				logger.debug("Message arrived: " + s + ", " + new String(mqttMessage.getPayload()));
			}
			try {
				String message = new String(mqttMessage.getPayload());
				session.sendTags(TagParsingStrategy.processTags(message, restdebug));
			} catch(Exception e) {
				logger.error("Error while parsing incoming tags to Generic through MQTT: " + e.getMessage());
			}
		}

		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
		}
	}

}

package org.rifidi.app.rifidiservices.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyMqttUtil {
	
	public static void postMqttMesssage(MqttClient mqttClient, String mqttTopic,
			int mqttQos, Object messageContent){
		
		/** Logger for this class */
		final Log logger = LogFactory.getLog(MyMqttUtil.class);
		
		try {
			
			

			JAXBContext jaxbContext = JAXBContext
					.newInstance(messageContent.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);

			// Create xml String and send to server using mqttClient
			Writer writer = new StringWriter();
			jaxbMarshaller.marshal(messageContent, writer);
			String content = writer.toString();
			writer.close();

			logger.info("Publishing message: " + content);
			MqttMessage message = new MqttMessage(content.getBytes());
			message.setQos(mqttQos);

			try {

				mqttClient.connect();
				logger.info("Connected to broker: "
						+ mqttClient.getServerURI());

			} catch (MqttException mEx) {

				logger.error("Error connecting to broker", mEx);
				throw new RuntimeException(mEx);

			}

			try {

				mqttClient.publish(mqttTopic, message);
				logger.info("Message published");

			} catch (MqttException mEx) {

				logger.error("Error publishing to queue", mEx);
				throw new RuntimeException(mEx);

			}

			try {

				mqttClient.disconnect();
				logger.info("mqttClient disconnected.");

			} catch (MqttException mEx) {

				logger.error("Error trying to disconnect mqttClient", mEx);
				throw new RuntimeException(mEx);

			}

		} catch (JAXBException jEx) {

			logger.error("Error publishing to queue", jEx);
			throw new RuntimeException(jEx);

		} catch (IOException ioEx) {

			logger.error("Error publishing to queue", ioEx);
			throw new RuntimeException(ioEx);

		}
		
	}

}

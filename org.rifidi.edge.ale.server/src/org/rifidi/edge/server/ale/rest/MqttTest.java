package org.rifidi.edge.server.ale.rest;

public class MqttTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MqttPublisher publisher = new MqttPublisher();
		publisher.publish("Prueba mensaje a publicar");
	}

}

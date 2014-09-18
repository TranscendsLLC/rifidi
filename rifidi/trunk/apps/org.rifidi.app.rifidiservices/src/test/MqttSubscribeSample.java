/*******************************************************************************
 * Copyright (c) 2009, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Dave Locke - initial API and implementation and/or initial documentation
 */

package test;

import java.io.IOException;
import java.sql.Timestamp;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 * A sample application that demonstrates how to use the Paho MQTT v3.1 Client blocking API.
 *
 * It can be run from the command line in one of two modes:
 *  - as a publisher, sending a single message to a topic on the server
 *  - as a subscriber, listening for messages from the server
 *
 *  There are three versions of the sample that implement the same features
 *  but do so using using different programming styles:
 *  <ol>
 *  <li>Sample (this one) which uses the API which blocks until the operation completes</li>
 *  <li>SampleAsyncWait shows how to use the asynchronous API with waiters that block until
 *  an action completes</li>
 *  <li>SampleAsyncCallBack shows how to use the asynchronous API where events are
 *  used to notify the application when an action completes<li>
 *  </ol>
 *
 *  If the application is run with the -h parameter then info is displayed that
 *  describes all of the options / parameters.
 */
public class MqttSubscribeSample 
		implements MqttCallback {

	/**
	 * The main entry point of the sample.
	 *
	 * This method handles parsing of the arguments specified on the
	 * command-line before performing the specified action.
	 */
	public static void main(String[] args) {

		
		try {
			// Create an instance of this class
			MqttSubscribeSample sampleClient = new MqttSubscribeSample();

			sampleClient.subscribe();
			
		} catch(MqttException me) {
			// Display full details of any exception that occurs
			System.out.println("reason "+me.getReasonCode());
			System.out.println("msg "+me.getMessage());
			System.out.println("loc "+me.getLocalizedMessage());
			System.out.println("cause "+me.getCause());
			System.out.println("excep "+me);
			me.printStackTrace();
		}
	}

	// Private instance variables
	/**
	 * @param brokerUrl the url of the server to connect to
	 * @param clientId the client id to connect with
	 * @param cleanSession clear state at end of connection or not (durable or non-durable subscriptions)
	 * @param quietMode whether debug should be printed to standard out
     * @param userName the username to connect with
     * @param password the password for the user
	 * @throws MqttException
	 */
	
	private String brokerUrl = "tcp://iot.eclipse.org:1883";
	private MqttClient client;
	private boolean quietMode = false;
	private MqttConnectOptions 	conOpt;
	private boolean clean = true;
	private String password = null;
	private String userName = null;
	private String clientId = "testMQTTRifidi";
	//private String topicName = "transcendstest";
	private String topicName = "Alien_1";
	private int qos = 2;

    public MqttSubscribeSample() 
    			throws MqttException {
    	
    	
    	//This sample stores in a temporary directory... where messages temporarily
    	// stored until the message has been delivered to the server.
    	//..a real application ought to store them somewhere
    	// where they are not likely to get deleted or tampered with
    	String tmpDir = System.getProperty("java.io.tmpdir");
    	MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

    	try {
    		// Construct the connection options object that contains connection parameters
    		// such as cleanSession and LWT
	    	conOpt = new MqttConnectOptions();
	    	conOpt.setCleanSession(clean);
	    	if(password != null ) {
	    	  conOpt.setPassword(this.password.toCharArray());
	    	}
	    	if(userName != null) {
	    	  conOpt.setUserName(this.userName);
	    	}

    		// Construct an MQTT blocking mode client
			client = new MqttClient(this.brokerUrl,clientId, dataStore);

			// Set this wrapper as the callback handler
	    	client.setCallback(this);

		} catch (MqttException e) {
			e.printStackTrace();
			log("Unable to set up client: "+e.toString());
			System.exit(1);
		}
    }

    /**
     * Publish / send a message to an MQTT server
     * @param topicName the name of the topic to publish to
     * @param qos the quality of service to delivery the message at (0,1,2)
     * @param payload the set of bytes to send to the MQTT server
     * @throws MqttException
     */
    public void publish(String topicName, int qos, byte[] payload) throws MqttException {

    	// Connect to the MQTT server
    	log("Connecting to "+brokerUrl + " with client ID "+client.getClientId());
    	client.connect(conOpt);
    	log("Connected");

    	String time = new Timestamp(System.currentTimeMillis()).toString();
    	log("Publishing at: "+time+ " to topic \""+topicName+"\" qos "+qos);

    	// Create and configure a message
   		MqttMessage message = new MqttMessage(payload);
    	message.setQos(qos);

    	// Send the message to the server, control is not returned until
    	// it has been delivered to the server meeting the specified
    	// quality of service.
    	client.publish(topicName, message);

    	// Disconnect the client
    	client.disconnect();
    	log("Disconnected");
    }

    /**
     * Subscribe to a topic on an MQTT server
     * Once subscribed this method waits for the messages to arrive from the server
     * that match the subscription. It continues listening for messages until the enter key is
     * pressed.
     * @param topicName to subscribe to (can be wild carded)
     * @param qos the maximum quality of service to receive messages at for this subscription
     * @throws MqttException
     */
    public void subscribe() throws MqttException {

    	// Connect to the MQTT server
    	client.connect(conOpt);
    	log("Connected to "+brokerUrl+" with client ID "+client.getClientId());

    	// Subscribe to the requested topic
    	// The QoS specified is the maximum level that messages will be sent to the client at.
    	// For instance if QoS 1 is specified, any messages originally published at QoS 2 will
    	// be downgraded to 1 when delivering to the client but messages published at 1 and 0
    	// will be received at the same level they were published at.
    	log("Subscribing to topic \""+topicName+"\" qos "+qos);
    	client.subscribe(topicName, qos);

    	// Continue waiting for messages until the Enter is pressed
    	log("Press <Enter> to exit");
		try {
			System.in.read();
		} catch (IOException e) {
			//If we can't read we'll just exit
		}

		// Disconnect the client from the server
		client.disconnect();
		log("Disconnected");
    }

    /**
     * Utility method to handle logging. If 'quietMode' is set, this method does nothing
     * @param message the message to log
     */
    private void log(String message) {
    	if (!quietMode) {
    		System.out.println(message);
    	}
    }

	/****************************************************************/
	/* Methods to implement the MqttCallback interface              */
	/****************************************************************/

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
	public void connectionLost(Throwable cause) {
		// Called when the connection to the server has been lost.
		// An application may choose to implement reconnection
		// logic at this point. This sample simply exits.
		log("Connection to " + brokerUrl + " lost!" + cause);
		System.exit(1);
	}

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
	public void deliveryComplete(IMqttDeliveryToken token) {
		// Called when a message has been delivered to the
		// server. The token passed in here is the same one
		// that was passed to or returned from the original call to publish.
		// This allows applications to perform asynchronous
		// delivery without blocking until delivery completes.
		//
		// This sample demonstrates asynchronous deliver and
		// uses the token.waitForCompletion() call in the main thread which
		// blocks until the delivery has completed.
		// Additionally the deliveryComplete method will be called if
		// the callback is set on the client
		//
		// If the connection to the server breaks before delivery has completed
		// delivery of a message will complete after the client has re-connected.
		// The getPendingTokens method will provide tokens for any messages
		// that are still to be delivered.
	}

    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
	public void messageArrived(String topic, MqttMessage message) throws MqttException {
		// Called when a message arrives from the server that matches any
		// subscription made by the client
		String time = new Timestamp(System.currentTimeMillis()).toString();
		System.out.println("Time:\t" +time +
                           "  Topic:\t" + topic +
                           "  Message:\t" + new String(message.getPayload()) +
                           "  QoS:\t" + message.getQos());
	}

	/****************************************************************/
	/* End of MqttCallback methods                                  */
	/****************************************************************/

	   static void printHelp() {
	      System.out.println(
	          "Syntax:\n\n" +
	              "    Sample [-h] [-a publish|subscribe] [-t <topic>] [-m <message text>]\n" +
	              "            [-s 0|1|2] -b <hostname|IP address>] [-p <brokerport>] [-i <clientID>]\n\n" +
	              "    -h  Print this help text and quit\n" +
	              "    -q  Quiet mode (default is false)\n" +
	              "    -a  Perform the relevant action (default is publish)\n" +
	              "    -t  Publish/subscribe to <topic> instead of the default\n" +
	              "            (publish: \"Sample/Java/v3\", subscribe: \"Sample/#\")\n" +
	              "    -m  Use <message text> instead of the default\n" +
	              "            (\"Message from MQTTv3 Java client\")\n" +
	              "    -s  Use this QoS instead of the default (2)\n" +
	              "    -b  Use this name/IP address instead of the default (m2m.eclipse.org)\n" +
	              "    -p  Use this port instead of the default (1883)\n\n" +
	              "    -i  Use this client ID instead of SampleJavaV3_<action>\n" +
	              "    -c  Connect to the server with a clean session (default is false)\n" +
	              "     \n\n Security Options \n" +
	              "     -u Username \n" +
	              "     -z Password \n" +
	              "     \n\n SSL Options \n" +
	              "    -v  SSL enabled; true - (default is false) " +
	              "    -k  Use this JKS format key store to verify the client\n" +
	              "    -w  Passpharse to verify certificates in the keys store\n" +
	              "    -r  Use this JKS format keystore to verify the server\n" +
	              " If javax.net.ssl properties have been set only the -v flag needs to be set\n" +
	              "Delimit strings containing spaces with \"\"\n\n" +
	              "Publishers transmit a single message then disconnect from the server.\n" +
	              "Subscribers remain connected to the server and receive appropriate\n" +
	              "messages until <enter> is pressed.\n\n"
	          );
    }

}

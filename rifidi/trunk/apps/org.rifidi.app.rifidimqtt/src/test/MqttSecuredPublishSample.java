package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttSecuredPublishSample
		implements MqttCallback {

    public static void main(String[] args) 
    		throws Exception {
    	
    	MqttSecuredPublishSample mqttSecuredSubscribeSample = new MqttSecuredPublishSample();
    	mqttSecuredSubscribeSample.publish();
    }

	public void publish() 
			throws Exception {
		
		String topic        = "transcendstest";
        String content      = "testing mqtt 3";
        int qos             = 2;
        //String broker       = "tcp://iot.eclipse.org:1883";
        //String broker       = "tcp://localhost:1883";
        //String broker       = "ssl://iot.eclipse.org:1883";
        String broker       = "ssl://localhost:8883";
        String clientId     = "clientIdPublisher";
        //MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setConnectionTimeout(60);
            connOpts.setKeepAliveInterval(60);
            connOpts.setSocketFactory(getSSLSocketFactory());
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            
            sampleClient.setCallback(this);
            
            sampleClient.publish(topic, message);
            
            //System.out.println("Message published");
            sampleClient.disconnect();
            System.out.println("Disconnected");
            //System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
		
	}
	
	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println("connectionLost: " + arg0);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		System.out.println("deliveryComplete: " + arg0);
		
	}

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("messageArrived: " + arg0 + "\nMqttMessage: " + arg1);
	}
	
	
	/**
	 * 
	 * keystore generated into test/resources with command:
	 * 
	 * 
	 * 
	 * keytool -keystore clientkeystore.jks -alias testclient -genkey -keyalg
	 * RSA
	 * 
	 * -> mandatory to put the name surname
	 * 
	 * -> password is passw0rd
	 * 
	 * -> type yes at the end
	 * 
	 * 
	 * 
	 * to generate the crt file from the keystore
	 * 
	 * -- keytool -certreq -alias testclient -keystore clientkeystore.jks -file
	 * testclient.csr
	 * 
	 * 
	 * 
	 * keytool -export -alias testclient -keystore clientkeystore.jks -file
	 * testclient.crt
	 * 
	 * 
	 * 
	 * to import an existing certificate:
	 * 
	 * keytool -keystore clientkeystore.jks -import -alias testclient -file
	 * testclient.crt -trustcacerts
	 */

	private SSLSocketFactory getSSLSocketFactory()
			throws KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException, IOException, CertificateException,
			KeyStoreException {

		KeyStore ks = KeyStore.getInstance("JKS");

		//System.out.println("path: " + new File(".").getAbsolutePath());
		
		//InputStream jksInputStream = getClass().getClassLoader().getResourceAsStream("\\D:\\tmp\\certmqtt\\build.properties");
		
		//InputStream jksInputStream = getClass().getClassLoader().getResourceAsStream("build.properties");
		//InputStream jksInputStream = getClass().getClassLoader().getResourceAsStream("localhost.jks");
		
		InputStream jksInputStream = new FileInputStream("\\D:\\tmp\\certmqtt\\clientkeystore.jks");
				//.getResourceAsStream("clientkeystore.jks");
				

		//ks.load(jksInputStream, "passw0rd".toCharArray());
		ks.load(jksInputStream, "passw0rd".toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
				.getDefaultAlgorithm());

		//kmf.init(ks, "passw0rd".toCharArray());
		kmf.init(ks, "passw0rd".toCharArray());

		TrustManagerFactory tmf = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());

		tmf.init(ks);

		SSLContext sc = SSLContext.getInstance("TLS");

		TrustManager[] trustManagers = tmf.getTrustManagers();

		sc.init(kmf.getKeyManagers(), trustManagers, null);

		SSLSocketFactory ssf = sc.getSocketFactory();

		return ssf;

	}
}


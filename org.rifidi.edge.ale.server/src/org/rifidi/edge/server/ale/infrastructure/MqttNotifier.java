package org.rifidi.edge.server.ale.infrastructure;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.rifidi.edge.epcglobal.ale.ECReports;
import org.rifidi.edge.epcglobal.ale.ECSpec;
import org.rifidi.edge.epcglobal.ale.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.InvalidURIExceptionResponse;
import org.rifidi.edge.utils.RifidiHelper;

public class MqttNotifier extends Notifier implements MqttCallback{
	
	/** logger */
	private static final Logger LOG = Logger.getLogger(MqttNotifier.class);
	
	private URI uri;
	private String host;
	private int port;
	
	//FIXME ALE take from jvm
	private String topic = "ale";
	
	//FIXME ALE take from jvm
    private final int qos = 2;
    
    private String notificationURI;
    
    private String serverUri;
    
    private String clientId;
    
    private MemoryPersistence persistence;
    
    private MqttClient client;
    
    private MqttConnectOptions connOpts;
        
	@Override
	public void init(String uri, RifidiHelper rifidiHelper) throws InvalidURIExceptionResponse {
		// TODO Auto-generated method stub
		try {
			
			this.notificationURI = uri;
			this.uri = new URI(notificationURI);
			setRifidiHelper(rifidiHelper);
			
			String[] segments = uri.split("/");
			if (segments.length == 4){
				String userTopic = segments[3];
				if (userTopic != null && !userTopic.isEmpty()){
					topic = userTopic;
					System.out.println("MqttNotifier.topic: " + topic);
					//Fix uri
					this.uri = new URI(segments[0] + "/" + segments[1] + "/" + segments[2]);
				}
			}
			
			if (!("TCP".equalsIgnoreCase(this.uri.getScheme()))) {
				LOG.error("invalid scheme: " + this.uri.getScheme());
				throw new InvalidURIExceptionResponse("invalid scheme: " + this.uri.getScheme());
			}
									
			host = this.uri.getHost();
			port = this.uri.getPort();
			
			
			serverUri = this.uri.toString();
    		clientId = (new Date().getTime() + getNotifierId()).substring(0, 23);
    		persistence = new MemoryPersistence();
    		
    		System.out.println("serverUri: " + serverUri);
    		System.out.println("clientId: " + clientId);
    		
            client = new MqttClient(serverUri, clientId, persistence);
            connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
			
			
					
		} catch (Exception e) {
			LOG.error("malformed URI");
			throw new InvalidURIExceptionResponse("malformed URI: ", e);
		} 
		
	}

	@Override
	public void notifySubscriber(ECReports reports) throws ImplementationExceptionResponse {
		// TODO Auto-generated method stub	
		publishToMqtt(getXml(reports));
	}
	
//	@Override
//	public void notifyExceptionToSubscriber(ECReports t, ImplementationExceptionResponse e) {
//		// TODO Auto-generated method stub
//		
//		List<String> errorMessages = new ArrayList<>();
//		
////		ECSpec ecSpec = getECSpec(specName);
//		ECSpec ecSpec = t.getECSpec();
//		
//		try {
//			errorMessages = getRifidiHelper().validateSessionInProcessingState(ecSpec);
//			
//			if ( !errorMessages.isEmpty() ){
//				String strError = getRifidiHelper().getErrorMessagesAsSingleText(errorMessages);
//				//throw new ImplementationExceptionResponse(strError);
//				publishExceptionToMqtt(getXml(new ImplementationExceptionResponse(strError)));
//			}
//			
//		} catch (Exception ex){
//			ex.printStackTrace();
////			throw new ImplementationExceptionResponse(ex.getMessage(), ex);
//		}
//		
//		
//	}
	
	@Override
	public void notifyExceptionToSubscriber(ImplementationExceptionResponse e) {
		// TODO Auto-generated method stub
				
		try {
			
				String strXml = getXml( e );
				System.out.println("MqttNotifier.notifyExceptionToSubscriber.strXml: " + strXml);
				publishExceptionToMqtt( strXml );
//				publishExceptionToMqtt( e.getMessage() );
		} catch (Exception ex){
			ex.printStackTrace();
//			throw new ImplementationExceptionResponse(ex.getMessage(), ex);
		}
		
		
	}

	@Override
	public String getURIfromFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println("MqttNotifier connectionLost");
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		System.out.println("MqttNotifier deliveryComplete");
	}

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("MqttNotifier messageArrived");
	}
	
	public void publishToMqtt(String content){
    	
    	try {
    		
    		
            System.out.println("Connecting to broker: " + this.uri.toString());
            client.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing to topic: '" + topic + "' messsage: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            
            client.setCallback(this);
            
            client.publish(topic, message);
                        
            System.out.println("Going to disconnect...");
            client.disconnect();
            System.out.println("Disconnected");
            
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
	}
	
	public void publishExceptionToMqtt(String content){ 
    	
    	try {
    		    		
            System.out.println("Connecting to broker: " + this.uri.toString());
            client.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing to topic: '" + topic + "' messsage: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            
            client.setCallback(this);
            
            client.publish(topic, message);
                        
            System.out.println("Going to disconnect...");
            client.disconnect();
            System.out.println("Disconnected");
            
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
	}

}

/**
 * 
 */
package org.rifidi.edge.tools.diagnostics;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.notification.AntennaEvent;
import org.rifidi.edge.notification.AppStartedEvent;
import org.rifidi.edge.notification.AppStoppedEvent;
import org.rifidi.edge.notification.ReaderExceptionEvent;
import org.rifidi.edge.notification.SensorClosedEvent;
import org.rifidi.edge.notification.SensorConnectingEvent;
import org.rifidi.edge.notification.SensorLoggingInEvent;
import org.rifidi.edge.notification.SensorProcessingEvent;
import org.rifidi.edge.tools.notification.AntennaDTO;
import org.rifidi.edge.tools.notification.AppStartedDTO;
import org.rifidi.edge.tools.notification.AppStoppedDTO;
import org.rifidi.edge.tools.notification.KeepAliveDTO;
import org.rifidi.edge.tools.notification.ReaderExceptionDTO;
import org.rifidi.edge.tools.notification.SensorClosedDTO;
import org.rifidi.edge.tools.notification.SensorConnectingDTO;
import org.rifidi.edge.tools.notification.SensorLoggingInDTO;
import org.rifidi.edge.tools.notification.SensorProcessingDTO;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class MqttNotifierApp extends AbstractRifidiApp {
	

	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());

	/** mqttClient to be used in sending tag data to mqttServer **/
	// If you need a mqtt client
	private MqttClient mqttClient;	
	private String mqttTopic;	
	private String mqttBroker;
	private String mqttClientId;
	
	private Boolean mqttDisable;
	private Boolean processingDisable;
	private Boolean connectingDisable;
	private Boolean logginginDisable;
	private Boolean closedDisable;
	private Boolean appStartedDisable;
	private Boolean appStoppedDisable;
	private Boolean keepAliveDisable;
	private Boolean readerExceptionDisable;
	
	private String keepAliveName;
	private Integer keepAliveTimeout;
	
	private KeepAliveThread keepalive;
	private Thread keepAliveThread;
	


	/** Quality of service used to send the message **/
	/**
	 * The MQTT protocol provides three qualities of service for delivering
	 * messages between clients and servers: "at most once", "at least once" and
	 * "exactly once". QoS0, At most once QoS1, At least once QoS2, Exactly once
	 */
	// You can define a quality of service level for delivering message
	private int mqttQos;
	
	private String ip = "";

	/**
	 * 
	 * @param group
	 * @param name
	 */
	public MqttNotifierApp(String group, String name) {
		super(group, name);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.AbstractRifidiApp#lazyStart()
	 */
	@Override
	public boolean lazyStart() {
		String lazyStart= getProperty(LAZY_START, "false");
		return Boolean.parseBoolean(lazyStart);
	}
	
	//clever method to get the first non-loopback inet address
	static InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6) throws SocketException {
	    Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
	    while (en.hasMoreElements()) {
	        NetworkInterface i = (NetworkInterface) en.nextElement();
	        for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements();) {
	            InetAddress addr = (InetAddress) en2.nextElement();
	            if (!addr.isLoopbackAddress()) {
	                if (addr instanceof Inet4Address) {
	                    if (preferIPv6) {
	                        continue;
	                    }
	                    return addr;
	                }
	                if (addr instanceof Inet6Address) {
	                    if (preferIpv4) {
	                        continue;
	                    }
	                    return addr;
	                }
	            }
	        }
	    }
	    return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {
		// Here you initialize properties and resources, like mqtt client
		this.mqttQos = Integer.parseInt(getProperty("mqttnotifier.qos", "2"));
		this.mqttBroker = getProperty("mqttnotifier.broker", "tcp://localhost:1883");
		this.mqttClientId = getProperty("mqttnotifier.clientid", "MyAppRifidiServicesId");
		this.mqttTopic = getProperty("mqttnotifier.topic", "notifications");
		this.mqttDisable = Boolean.parseBoolean(getProperty("mqttnotifier.global.disable", "false"));
		this.processingDisable = Boolean.parseBoolean(getProperty("mqttnotifier.processing.disable", "false"));
		this.connectingDisable = Boolean.parseBoolean(getProperty("mqttnotifier.connecting.disable", "false"));
		this.logginginDisable = Boolean.parseBoolean(getProperty("mqttnotifier.loggingin.disable", "false"));
		this.closedDisable = Boolean.parseBoolean(getProperty("mqttnotifier.closed.disable", "false"));
		this.appStartedDisable = Boolean.parseBoolean(getProperty("mqttnotifier.app.started.disable", "false"));
		this.appStoppedDisable = Boolean.parseBoolean(getProperty("mqttnotifier.app.stopped.disable", "false"));
		this.keepAliveDisable = Boolean.parseBoolean(getProperty("mqttnotifier.keepalive.disable", "false"));
		this.readerExceptionDisable = Boolean.parseBoolean(getProperty("mqttnotifier.readerexception.disable","false"));
		
		//KeepAlive properties
		this.keepAliveName = getProperty("mqttnotifier.keepalive.name", "");
		this.keepAliveTimeout = Integer.parseInt(getProperty("mqttnotifier.keepalive.timeout", "20000"));
		
		MemoryPersistence persistence = new MemoryPersistence();

		try {
			this.mqttClient = new MqttClient(this.mqttBroker, this.mqttClientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
		} catch (MqttException mEx) {
			logger.error("Error creating mqttClient instance to broker", mEx);
			throw new RuntimeException(mEx);
		}
		
		if(!keepAliveDisable && !mqttDisable) {
			this.keepalive = new KeepAliveThread(keepAliveName, keepAliveTimeout);
			keepAliveThread = new Thread(keepalive);
			keepAliveThread.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_start()
	 */
	@Override
	protected void _start() {
		try {
			InetAddress localip = getFirstNonLoopbackAddress(true, false);
			//NetworkInterface network = NetworkInterface.getByInetAddress(localip);
			if(localip!=null) {
				this.ip = localip.getHostAddress();
			} else {
				this.ip = "127.0.0.1";
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		final String ip = this.ip;
		final String mqttTopic = this.mqttTopic;
		final MqttClient mqttClient = this.mqttClient;
		final int mqttQos = this.mqttQos;

		addStatement("select * from SensorProcessingEvent", new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1, EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null && !mqttDisable && !processingDisable) {
					for (EventBean b : arg0) {
						SensorProcessingEvent sce = (SensorProcessingEvent) b.getUnderlying();
						// send to mqtt
						SensorProcessingDTO sc = new SensorProcessingDTO();
						sc.setIp(ip);
						sc.setSensor(sce.getSensorID());
						sc.setTimestamp(System.currentTimeMillis());
						postMqttMesssage(mqttClient, mqttTopic, mqttQos, sc);
					}
				}
			}
		});

		addStatement("select * from SensorClosedEvent", new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1, EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null && !mqttDisable && !closedDisable) {
					for (EventBean b : arg0) {
						SensorClosedEvent sde = (SensorClosedEvent) b.getUnderlying();
						// send to mqtt
						SensorClosedDTO sd = new SensorClosedDTO();
						sd.setIp(ip);
						sd.setSensor(sde.getSensorID());
						sd.setTimestamp(System.currentTimeMillis());
						postMqttMesssage(mqttClient, mqttTopic, mqttQos, sd);
						
					}
				}

			}
		});
		
		addStatement("select * from SensorConnectingEvent", new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1, EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null && !mqttDisable && !connectingDisable) {
					for (EventBean b : arg0) {
						SensorConnectingEvent sde = (SensorConnectingEvent) b.getUnderlying();
						// send to mqtt
						SensorConnectingDTO sd = new SensorConnectingDTO();
						sd.setIp(ip);
						sd.setSensor(sde.getSensorID());
						sd.setTimestamp(System.currentTimeMillis());
						postMqttMesssage(mqttClient, mqttTopic, mqttQos, sd);
						
					}
				}

			}
		});
		
		addStatement("select * from SensorLoggingInEvent", new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1, EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null && !mqttDisable && !logginginDisable) {
					for (EventBean b : arg0) {
						SensorLoggingInEvent sde = (SensorLoggingInEvent) b.getUnderlying();
						// send to mqtt
						SensorLoggingInDTO sd = new SensorLoggingInDTO();
						sd.setIp(ip);
						sd.setSensor(sde.getSensorID());
						sd.setTimestamp(System.currentTimeMillis());
						postMqttMesssage(mqttClient, mqttTopic, mqttQos, sd);						
					}
				}

			}
		});
		
		addStatement("select * from AntennaEvent", new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1, EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null && !mqttDisable && !logginginDisable) {
					for (EventBean b : arg0) {
						AntennaEvent event = (AntennaEvent) b.getUnderlying();
						// send to mqtt
						AntennaDTO dto = new AntennaDTO();
						dto.setUp(event.isUp());
						dto.setAntenna(event.getAntennaID());
						dto.setSensor(event.getSensorID());
						dto.setTimestamp(System.currentTimeMillis());						
						postMqttMesssage(mqttClient, mqttTopic, mqttQos, dto);						
					}
				}

			}
		});
		
		addStatement("select * from AppStartedEvent", new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1, EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null && !mqttDisable && !appStartedDisable) {
					for (EventBean b : arg0) {
						AppStartedEvent ase = (AppStartedEvent) b.getUnderlying();
						// send to mqtt
						AppStartedDTO as = new AppStartedDTO();
						as.setIp(ip);
						as.setName(ase.getName());
						as.setGroup(ase.getGroup());
						as.setTimestamp(System.currentTimeMillis());
						postMqttMesssage(mqttClient, mqttTopic, mqttQos, as);
					}
				}

			}
		});
		
		addStatement("select * from AppStoppedEvent", new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1, EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null && !mqttDisable && !appStoppedDisable) {
					for (EventBean b : arg0) {
						AppStoppedEvent ase = (AppStoppedEvent) b.getUnderlying();
						// send to mqtt
						AppStoppedDTO as = new AppStoppedDTO();
						as.setIp(ip);
						as.setName(ase.getName());
						as.setGroup(ase.getGroup());
						as.setTimestamp(System.currentTimeMillis());
						postMqttMesssage(mqttClient, mqttTopic, mqttQos, as);
					}
				}

			}
		});
		
		addStatement("select * from ReaderExceptionEvent", new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1, EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null && !mqttDisable && !readerExceptionDisable) {
					for (EventBean b : arg0) {
						ReaderExceptionEvent re = (ReaderExceptionEvent) b.getUnderlying();
						// send to mqtt
						ReaderExceptionDTO dto = new ReaderExceptionDTO();
						dto.setErrordescription(re.getErrordesc());
						dto.setTimestamp(re.getTimestamp());
						dto.setSensor(re.getSensorID());
						dto.setStatuscode(re.getStatuscode());
						postMqttMesssage(mqttClient, mqttTopic, mqttQos, dto);
					}
				}
			}
		});
	}

	public synchronized void postMqttMesssage(MqttClient mqttClient, String mqttTopic, int mqttQos, Object messageContent) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(messageContent.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Create xml String and send to server using mqttClient
			Writer writer = new StringWriter();
			jaxbMarshaller.marshal(messageContent, writer);
			String content = writer.toString();
			writer.close();

			logger.debug("Publishing message: " + content);
			
			MqttMessage message = new MqttMessage(content.getBytes());
			message.setQos(mqttQos);
			synchronized (mqttClient) {
				try {
					mqttClient.connect();
					logger.debug("Connected to broker: " + mqttClient.getServerURI());
				} catch (MqttException mEx) {
					logger.error("Error connecting to broker", mEx);
					throw new RuntimeException(mEx);
				}

				try {
					mqttClient.publish(mqttTopic, message);
					logger.debug("Message published");
				} catch (MqttException mEx) {
					logger.error("Error publishing to queue", mEx);
					throw new RuntimeException(mEx);
				}

				try {
					mqttClient.disconnect();
					logger.debug("mqttClient disconnected.");
				} catch (MqttException mEx) {
					logger.error("Error trying to disconnect mqttClient", mEx);
					throw new RuntimeException(mEx);
				}
			}
		} catch (JAXBException jEx) {
			logger.error("Error publishing to queue", jEx);
			throw new RuntimeException(jEx);
		} catch (IOException ioEx) {
			logger.error("Error publishing to queue", ioEx);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_stop()
	 */
	@Override
	public void _stop() {
		if (this.keepalive != null) {
			this.keepalive.stop = true;
		}
	}
	
	/**
	 * Keep alive thread -- will call postMqttMessage with a keepalive payload at set intervals.  
	 * 
	 * @author Matthew Dean - matt@transcends.co
	 */
	private class KeepAliveThread implements Runnable {
		
		volatile boolean stop = false;
		Long starttime = null;
		Integer timeout = null;
		String name;
		
		/**
		 * @param name 	The name of the server
		 * @param ip   	The ip of the server
		 */
		public KeepAliveThread(String name, Integer timeout) {
			this.name = name;
			this.timeout = timeout;
			starttime = System.currentTimeMillis();
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			while (!stop) {
				// Create the keepalive payload
				KeepAliveDTO dto = new KeepAliveDTO();
				// Set the IP
				dto.setIp(ip);
				dto.setName(name);
				Long currenttime = System.currentTimeMillis();
				dto.setTimestamp(currenttime);
				dto.setUptime(currenttime - starttime);

				// Post the message to Mqtt
				postMqttMesssage(mqttClient, mqttTopic, mqttQos, dto);
				
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
				}
			}
		}
	}

}

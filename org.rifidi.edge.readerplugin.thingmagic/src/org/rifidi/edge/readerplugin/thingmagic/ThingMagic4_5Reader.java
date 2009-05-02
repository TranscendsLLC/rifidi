package org.rifidi.edge.readerplugin.thingmagic;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.annotations.JMXMBean;
import org.rifidi.configuration.annotations.Operation;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.configuration.annotations.PropertyType;
import org.rifidi.edge.core.notifications.NotifierService;
import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.core.readers.ReaderSession;
import org.springframework.jms.core.JmsTemplate;

public class ThingMagic4_5Reader extends AbstractReader<ThingMagic4_5ReaderSession>{

	private ThingMagic4_5ReaderSession session;
	private int sessionID = 0;
	
	/** IP address of the readerSession. */
	private String ipAddress = ThingMagic4_5ReaderDefaultValues.ip;
	
	/** Port to connect to. */
	private Integer port = Integer.parseInt(ThingMagic4_5ReaderDefaultValues.port);
	
	/** Time between two connection attempts. */
	private Long reconnectionInterval = Long.parseLong(ThingMagic4_5ReaderDefaultValues.reconnectionInterval);
	
	/** Number of connection attempts before a connection goes into fail state. */
	private Integer maxNumConnectionAttempts = Integer.parseInt(ThingMagic4_5ReaderDefaultValues.maxNumConnectionAttempts);
	
	private NotifierServiceWrapper notifyServiceWrapper;
	private JmsTemplate template;
	private Destination destination;

	
	
	@Override
	public synchronized void applyPropertyChanges() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized ReaderSession createReaderSession() {
		if (session==null) {
			sessionID++;
			session = new ThingMagic4_5ReaderSession(Integer.toString(sessionID),
					ipAddress, maxNumConnectionAttempts, maxNumConnectionAttempts,
					maxNumConnectionAttempts, destination, template, notifyServiceWrapper, this.getID());
			
			// TODO: remove this once we get AspectJ in here!
			NotifierService service = notifyServiceWrapper.getNotifierService();
			if (service != null) {
				service.addSessionEvent(this.getID(), Integer
						.toString(sessionID));
			}
		}
		return null;
	}

	@Override
	public void destroyReaderSession(ReaderSession session) {
		// TODO Auto-generated method stub
		if ( this.session == session){
			for (Integer id : session.currentCommands().keySet()) {
				session.killComand(id);
			}
			this.session.disconnect();
			this.session = null;
			
			// TODO: remove this once we get AspectJ in here!
			NotifierService service = notifyServiceWrapper.getNotifierService();
			if (service != null) {
				service.removeSessionEvent(this.getID(), Integer
						.toString(sessionID));
			}
			
		}
	}

	@Override
	public Map<String, ReaderSession> getReaderSessions() {
		Map<String, ReaderSession> map =  new HashMap<String, ReaderSession>();
		if (session != null) {
			map.put(session.getID(), session);
		}
		return map;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}


	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/***
	 * 
	 * @param wrapper
	 *            The JMS Notifier to set
	 */
	public void setNotifiyServiceWrapper(NotifierServiceWrapper wrapper) {
		this.notifyServiceWrapper = wrapper;
	}


	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Property(displayName = "IP Address", description = "Address of the readerSession.", writable = true, category = "Connection", defaultValue = ThingMagic4_5ReaderDefaultValues.ip, orderValue = 0)
	public String getIpAddress() {
		return ipAddress;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	
	@Property(displayName = "Port", description = "Port of the readerSession.", writable = true, type = PropertyType.PT_INTEGER, category = "Connection", defaultValue = ThingMagic4_5ReaderDefaultValues.port, orderValue = 1, minValue = "0", maxValue = "65535")
	public Integer getPort() {
		return port;
	}

	public void setReconnectionInterval(Long reconnectionInterval) {
		this.reconnectionInterval = reconnectionInterval;
	}

	@Property(displayName = "Reconnection Interval", description = "Time between two connection attempts (ms).", writable = true, type = PropertyType.PT_LONG, category = "Connection", defaultValue = ThingMagic4_5ReaderDefaultValues.reconnectionInterval, orderValue = 4, minValue = "0")
	public Long getReconnectionInterval() {
		return reconnectionInterval;
	}

	public void setMaxNumConnectionAttempts(Integer maxNumConnectionAttempts) {
		this.maxNumConnectionAttempts = maxNumConnectionAttempts;
	}

	@Property(displayName = "Maximum Connection Attempts", description = "Number of times to try to connect to the readerSession before the connection is marked as failed.", writable = true, type = PropertyType.PT_INTEGER, category = "Connection", defaultValue = ThingMagic4_5ReaderDefaultValues.maxNumConnectionAttempts, orderValue = 5, minValue = "0")
	public Integer getMaxNumConnectionAttempts() {
		return maxNumConnectionAttempts;
	}

	
	
	
}

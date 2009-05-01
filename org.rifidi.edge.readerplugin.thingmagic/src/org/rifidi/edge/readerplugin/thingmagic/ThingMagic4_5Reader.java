package org.rifidi.edge.readerplugin.thingmagic;

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

	ThingMagic4_5ReaderSession session;
	
	/** IP address of the readerSession. */
	private String ipAddress = "192.168.0.1";
	
	/** Port to connect to. */
	private Integer port = 8080;
	
	/** Time between two connection attempts. */
	private Long reconnectionInterval = 100l;
	
	/** Number of connection attempts before a connection goes into fail state. */
	private Integer maxNumConnectionAttempts = 3;
	
	@Override
	public void applyPropertyChanges() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized ReaderSession createReaderSession() {
		if (session==null) {
			
		}
		return null;
	}

	@Override
	public void destroyReaderSession(ReaderSession session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, ReaderSession> getReaderSessions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void setDestination(Destination destination) {
		// TODO Auto-generated method stub
		
	}

	public void setTemplate(JmsTemplate template) {
		// TODO Auto-generated method stub
		
	}

	public void setNotifiyServiceWrapper(
			NotifierServiceWrapper notifierServiceWrapper) {
		// TODO Auto-generated method stub
		
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Property(displayName = "IP Address", description = "Address of the readerSession.", writable = true, category = "Connection", defaultValue = "192.168.0.1", orderValue = 0)
	public String getIpAddress() {
		return ipAddress;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	
	@Property(displayName = "Port", description = "Port of the readerSession.", writable = true, type = PropertyType.PT_INTEGER, category = "Connection", defaultValue = "8080", orderValue = 1, minValue = "0", maxValue = "65535")
	public Integer getPort() {
		return port;
	}

	public void setReconnectionInterval(Long reconnectionInterval) {
		this.reconnectionInterval = reconnectionInterval;
	}

	public Long getReconnectionInterval() {
		return reconnectionInterval;
	}

	public void setMaxNumConnectionAttempts(Integer maxNumConnectionAttempts) {
		this.maxNumConnectionAttempts = maxNumConnectionAttempts;
	}

	@Property(displayName = "Maximum Connection Attempts", description = "Number of times to try to connect to the readerSession before the connection is marked as failed.", writable = true, type = PropertyType.PT_INTEGER, category = "Connection", defaultValue = "3", orderValue = 5, minValue = "0")
	public Integer getMaxNumConnectionAttempts() {
		return maxNumConnectionAttempts;
	}

}

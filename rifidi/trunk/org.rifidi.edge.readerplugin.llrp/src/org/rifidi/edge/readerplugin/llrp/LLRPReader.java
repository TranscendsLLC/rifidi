/*
 *  LLRPReader.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.llrp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.configuration.annotations.PropertyType;
import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.core.readers.ReaderSession;
import org.springframework.jms.core.JmsTemplate;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class LLRPReader extends AbstractReader<LLRPReaderSession> {
	/** Logger for this class. */
	private Log logger = LogFactory.getLog(LLRPReader.class);
	/** The only session an LLRP reader allows. */
	private LLRPReaderSession session;
	/** A hashmap containing all the properties for this reader */
	private ConcurrentHashMap<String, String> readerProperties;
	/** IP address of the readerSession. */
	private String ipAddress = "127.0.0.1";
	/** Time between two connection attempts. */
	private Long reconnectionInterval = 500l;
	/** Number of connection attempts before a connection goes into fail state. */
	private Integer maxNumConnectionAttempts = 10;
	/** JMS destination. */
	private Destination destination;
	/** Spring JMS template */
	private JmsTemplate template;
	/** The ID of the session */
	private int sessionID = 0;

	/**
	 * 
	 */
	public LLRPReader() {
		this.readerProperties = new ConcurrentHashMap<String, String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.AbstractReader#createReaderSession()
	 */
	@Override
	public synchronized ReaderSession createReaderSession() {
		if (session == null) {
			sessionID++;
			session = new LLRPReaderSession(Integer.toString(sessionID),
					ipAddress, (int) (long) reconnectionInterval,
					maxNumConnectionAttempts, destination, template);
			return session;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.AbstractReader#destroyReaderSession(org.
	 * rifidi.edge.core.readers.ReaderSession)
	 */
	@Override
	public void destroyReaderSession(ReaderSession session) {
		logger.debug("Destroying reader session");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.AbstractReader#getReaderSessions()
	 */
	@Override
	public Map<String, ReaderSession> getReaderSessions() {
		Map<String, ReaderSession> ret = new HashMap<String, ReaderSession>();
		if (session != null) {
			ret.put(session.getID(), session);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.AbstractReader#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		session.transact(session.createSetReaderConfig());
	}

	/**
	 * @param reconnectionInterval
	 *            the reconnectionInterval to set
	 */
	public void setReconnectionInterval(Long reconnectionInterval) {
		this.reconnectionInterval = reconnectionInterval;
	}

	/**
	 * @return the maxNumConnectionAttempts
	 */
	@Property(displayName = "Maximum Connection Attempts", description = "Number of times to try to connect to the readerSession"
			+ ": before the connection is marked as failed.", writable = true, type = PropertyType.PT_INTEGER)
	public Integer getMaxNumConnectionAttempts() {
		return maxNumConnectionAttempts;
	}

	/**
	 * @param maxNumConnectionAttempts
	 *            the maxNumConnectionAttempts to set
	 */
	public void setMaxNumConnectionAttempts(Integer maxNumConnectionAttempts) {
		this.maxNumConnectionAttempts = maxNumConnectionAttempts;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
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

	/**
	 * @return the readerProperties
	 */
	public ConcurrentHashMap<String, String> getReaderProperties() {
		return readerProperties;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
}

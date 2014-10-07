/*
 *  GenericSensorSession.java
 *
 *  Created:	Aug 4, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.adapter.csl;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.csl.util.CslRfidTagServer;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.notification.ReadCycle;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.sessions.AbstractServerSocketSensorSession;
import org.rifidi.edge.sensors.sessions.MessageParsingStrategy;
import org.rifidi.edge.sensors.sessions.MessageParsingStrategyFactory;
import org.rifidi.edge.sensors.sessions.MessageProcessingStrategy;
import org.rifidi.edge.sensors.sessions.MessageProcessingStrategyFactory;


/**
 * The session class for the Generic sensor.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class CslSocketSensorSession extends AbstractServerSocketSensorSession {

	/** Logger for this class. */
	// private static final Log logger = LogFactory
	// .getLog(GenericSensorSession.class);

	/** Service used to send out notifications */
	private volatile NotifierService notifierService;

	private CslSocketSensorSessionTagHandler tagHandler = null;
	private CslRfidTagServer CslReader;

	private String notifyAddrPort;
	private String ipAddress;
	private String power_ant;
	private String dwellTime;
	private String country;
	private String inventoryRound;
	private String algorithm_Q;
	private String link_Profile;
	
	private static final Log logger = LogFactory.getLog(CslSocketSensorSession.class);
	
	private String startQ;
	private String gpo_0;
	private String gpo_1;

	private String displayName;		
	
	/** The ID for the reader. */
	private String readerID = null;

	public CslSocketSensorSession(AbstractSensor<?> sensor, String ID,
			NotifierService notifierService, String readerID,
			String notifyAddrPort, String ipAddress, String displayName, 
			String power_ant, String dwellTime, String country, 
			String inventoryRound, String algorithm_Q, String link_Profile,
			String startQ, String gpo_0, String gpo_1, 
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		// super(sensor, ID, notifyAddrPort, 10, commandConfigurations);
		super(sensor, ID, Integer.parseInt(notifyAddrPort), 10, commandConfigurations);
		this.readerID = readerID;
		this.notifierService = notifierService;
		this.tagHandler = new CslSocketSensorSessionTagHandler(readerID);
		this.notifyAddrPort = notifyAddrPort;
		this.ipAddress = ipAddress;
		this.power_ant = power_ant;
		this.dwellTime = dwellTime;
		this.country = country;
		this.displayName = displayName;
		
		this.inventoryRound = inventoryRound;
		this.algorithm_Q = algorithm_Q;
		this.link_Profile = link_Profile;
		
		this.startQ = startQ;
		this.gpo_0 = gpo_0;
		this.gpo_1 = gpo_1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractReaderSession#setStatus(org
	 * .rifidi.edge.core.api.SessionStatus)
	 */
	@Override
	protected void setStatus(SessionStatus status) {
		super.setStatus(status);
		// TODO: Remove this once we have aspectJ
		notifierService.sessionStatusChanged(this.readerID, this.getID(),
				status);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorSession#connect()
	 */
	@Override
	public void connect() throws IOException {
		int ret_val = 0;
		String cmdString;
		
		int p_notifyAddrPort = Integer.parseInt(this.notifyAddrPort);
		
		int p_dwellTime = Integer.parseInt(this.dwellTime);
		int p_inventoryRound = Integer.parseInt(this.inventoryRound);

		int p_power = Integer.parseInt(this.power_ant);
		int p_link_Profile = Integer.parseInt(this.link_Profile);
		String p_country = this.country;
		int p_algorithm_Q = Arrays.asList(InvAlgorithm_Num).indexOf(this.algorithm_Q);
		int p_startQ = Integer.parseInt(this.startQ);			

		
		String p_gpo0 = this.gpo_0;
		String p_gpo1 = this.gpo_1;
		
		
		// RF Power
		if((p_power < 0) || (p_power > 300))
		{
			ret_val = -11;
			cmdString = String.format("Error in setting RF Power !!! \n");
			System.out.println(cmdString);
			logger.info(String.format(cmdString));
		}
		
		// Link Profile		
		if((p_link_Profile < 0) || (p_link_Profile > 4))
		{
			ret_val = -12;
			cmdString = String.format("Error in setting Profile !!! \n");
			System.out.println(cmdString);
			logger.info(String.format(cmdString));
		}
		
		// Q Algorithm
		if((p_algorithm_Q < 0) || (p_algorithm_Q > 3))
		{
			ret_val = -14;
			cmdString = String.format("Error in setting Algorithm Q !!! \n");
			System.out.println(cmdString);
			logger.error(String.format(cmdString));			
		}
		// Start Q;
		if((p_startQ < 0) || (p_startQ > 15))
		{
			ret_val = -15;
			cmdString = String.format("Error in setting Start Q !!! \n");
			System.out.println(cmdString);
			logger.info(String.format(cmdString));
		}
		
		
		if(ret_val == 0)
		{
			// connect to CSL reader
			// CslReader = new CslRfidTagServer(this.ipAddress, 300, 1515, 300, "");
			CslReader = new CslRfidTagServer(this.ipAddress, p_power, p_notifyAddrPort, 300, "", p_dwellTime, p_country,
												p_inventoryRound, p_algorithm_Q, p_link_Profile, p_startQ);
			try {
				CslReader.StartInventoryAsync();
				super._connect();
				super.submitQueuedCommands();
			} catch (IOException e) {
				throw new IOException();
			}
		}
		else
		{
			throw new IOException();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorSession#connect()
	 */
	@Override
	public void disconnect() {
		super.disconnect();
		CslReader.StopInventory();
	}

	/**
	 * Parses and sends the tag to the desired destination.
	 * 
	 * @param tag
	 */
	public void sendTag(byte[] message) {
		TagReadEvent event = this.tagHandler.parseTag(new String(message));

		Set<TagReadEvent> tres = new HashSet<TagReadEvent>();
		tres.add(event);
		ReadCycle cycle = new ReadCycle(tres, readerID,
				System.currentTimeMillis());

		this.getSensor().send(cycle);

		// TODO: SEND TAGS
		// this.template.send(this.template.getDefaultDestination(),
		// new ReadCycleMessageCreator(cycle));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.sessions.AbstractServerSocketSensorSession
	 * #getMessageParsingStrategyFactory()
	 */
	@Override
	protected MessageParsingStrategyFactory getMessageParsingStrategyFactory() {
		return new MessageParsingStrategyFactory() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.rifidi.edge.sensors.sessions.MessageParsingStrategyFactory
			 * #createMessageParser()
			 */
			@Override
			public MessageParsingStrategy createMessageParser() {
				return new MessageParsingStrategy() {

					List<Byte> messageList = new LinkedList<Byte>();

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * org.rifidi.edge.sensors.sessions.MessageParsingStrategy
					 * #isMessage(byte)
					 */
					@Override
					public byte[] isMessage(byte message) {
						if (message == '\n') {
							byte[] retVal = new byte[messageList.size()];
							int index = 0;
							for (Byte b : messageList) {
								retVal[index] = b;
								index++;
							}
							return retVal;
						}
						messageList.add(message);
						return null;
					}
				};
			}

		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.sessions.AbstractServerSocketSensorSession
	 * #getMessageProcessingStrategyFactory()
	 */
	@Override
	protected MessageProcessingStrategyFactory getMessageProcessingStrategyFactory() {
		return new CslSocketMessageProcessingStrategyFactory(this);
	}

	/**
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class CslSocketMessageProcessingStrategyFactory implements
			MessageProcessingStrategyFactory {

		private CslSocketSensorSession session = null;

		public CslSocketMessageProcessingStrategyFactory(
				CslSocketSensorSession session) {
			this.session = session;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.rifidi.edge.sensors.sessions.MessageProcessingStrategyFactory
		 * #createMessageProcessor()
		 */
		@Override
		public MessageProcessingStrategy createMessageProcessor() {
			return new CslSocketMessageProcessingStrategy(session);
		}

	}

	/*
	 * Message processing strategy for the GenericSensorSession. This class will
	 * take in a pipe-delimited message, parse out the expected values: ID:(tag
	 * id), Antenna:(antenna tag was last seen on), and Timestamp:(milliseconds
	 * since epoch expected).
	 * 
	 * Anything else will go in the extrainformation hashmap as a key:value pair
	 * separated by a colon.
	 */
	private class CslSocketMessageProcessingStrategy implements
			MessageProcessingStrategy {

		private CslSocketSensorSession session = null;

		public CslSocketMessageProcessingStrategy(CslSocketSensorSession session) {
			this.session = session;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.rifidi.edge.sensors.sessions.MessageProcessingStrategy#
		 * processMessage(byte[])
		 */
		@Override
		public void processMessage(byte[] message) {
			this.session.sendTag(message);
		}
	}

    public String[] InvAlgorithm_Num = 
    {
    	"Fix_Q",
   		"Dyn_Q1",
   		"Dyn_Q2",
   		"Dyn_Q"
    };
	
}

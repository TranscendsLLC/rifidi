/*
 *  SerialBarcodeSensorSession.java
 *
 *  Created:	May 12, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.opticon;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.core.sensors.sessions.AbstractSerialSensorSession;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.readerplugin.opticon.tags.OpticonTagHandler;

/**
 * The Session for the Opticon Barcode reader. This reader can either be set in
 * HID mode or Serial mode; however we will always set it as a "serial" reader
 * for our purposes. Here are the settings necessary for getting this reader to
 * work:
 * 
 * SET <br>
 * 1. USB VPC [C01] {Serial Mode} <br>
 * 2. Clear all prefixes [MG] <br>
 * 3. Preamble [MZ] <br>
 * 4. ^B (STX) [1B] <br>
 * 5. Clear all suffixes [PR] <br>
 * 6. Postamble [PS] <br>
 * 7. ^C (ETX) [1C] <br>
 * 8. Multiple Read [S1] <br>
 * 9. 50ms [AH] <br>
 * 10. Enable Auto Trigger [+I] <br>
 * END
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class OpticonSensorSession extends AbstractSerialSensorSession {

	/** Service used to send out notifications */
	private volatile NotifierService notifierService;

	/** The ID for the reader. */
	private String readerID = null;

	/**
	 * 
	 */
	private OpticonTagHandler taghandler = null;

	/**
	 * 
	 * @param sensor
	 * @param ID
	 * @param destination
	 * @param template
	 * @param commandConfigurations
	 * @param commPortName
	 * @param baud
	 * @param databits
	 * @param stopbits
	 * @param parity
	 */
	public OpticonSensorSession(AbstractSensor<?> sensor, String ID,
			NotifierService notifierService, String readerID,
			Set<AbstractCommandConfiguration<?>> commandConfigurations,
			String commPortName) {
		super(sensor, ID, commandConfigurations, commPortName,
				OpticonConstants.BAUD, OpticonConstants.DATA_BITS,
				OpticonConstants.STOP_BITS, OpticonConstants.PARITY, false);
		this.readerID = readerID;
		this.notifierService = notifierService;
		this.taghandler = new OpticonTagHandler(readerID);
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
	 * @seeorg.rifidi.edge.core.sensors.sessions.AbstractSerialSensorSession#
	 * getMessageParsingStrategyFactory()
	 */
	@Override
	protected MessageParsingStrategyFactory getMessageParsingStrategyFactory() {
		return new OpticonMessageParsingStrategyFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.sessions.AbstractSerialSensorSession#
	 * messageReceived(org.rifidi.edge.core.sensors.messages.ByteMessage)
	 */
	@Override
	protected void messageReceived(ByteMessage message) {

		ReadCycle rc = this.taghandler.processTag(message.message);

		this.getSensor().send(rc);

		// this.template.send(this.template.getDefaultDestination(),
		// new ReadCycleMessageCreator(rc));
	}

	/**
	 * Factory class for creating OpticonMessageParsingStrategies.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class OpticonMessageParsingStrategyFactory implements
			MessageParsingStrategyFactory {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory
		 * #createMessageParser()
		 */
		@Override
		public MessageParsingStrategy createMessageParser() {
			return new OpticonMessageParsingStrategy();
		}
	}

	/**
	 * All messages coming back from the barcode reader are 10 characters long.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class OpticonMessageParsingStrategy implements
			MessageParsingStrategy {
		/** The message currently being processed. */
		private List<Byte> messagebuilder = new ArrayList<Byte>();

		private static final byte STARTBYTE = 2;

		private static final byte ENDBYTE = 3;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy#isMessage
		 * (byte)
		 */
		@Override
		public byte[] isMessage(byte message) {
			if (message == STARTBYTE) {
				messagebuilder.clear();
			} else if (message == ENDBYTE) {
				List<Byte> actualmessage = new ArrayList<Byte>();
				for (Byte b : messagebuilder) {
					actualmessage.add(b);
				}
				byte retval[] = new byte[actualmessage.size()];
				for (int i = 0; i < actualmessage.size(); i++) {
					retval[i] = actualmessage.get(i);
				}
				messagebuilder.clear();
				actualmessage.clear();
				return retval;
			} else {
				messagebuilder.add(message);
			}
			return null;
		}
	}
}

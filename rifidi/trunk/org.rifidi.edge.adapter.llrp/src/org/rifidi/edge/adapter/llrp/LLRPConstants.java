/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.adapter.llrp;

import org.llrp.ltk.generated.enumerations.AccessReportTriggerType;
import org.llrp.ltk.generated.enumerations.GPIPortState;
import org.llrp.ltk.generated.enumerations.NotificationEventType;
import org.llrp.ltk.generated.enumerations.ROReportTriggerType;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG;
import org.llrp.ltk.generated.parameters.AccessReportSpec;
import org.llrp.ltk.generated.parameters.C1G2EPCMemorySelector;
import org.llrp.ltk.generated.parameters.EventNotificationState;
import org.llrp.ltk.generated.parameters.GPIPortCurrentState;
import org.llrp.ltk.generated.parameters.ROReportSpec;
import org.llrp.ltk.generated.parameters.ReaderEventNotificationSpec;
import org.llrp.ltk.generated.parameters.TagReportContentSelector;
import org.llrp.ltk.types.Bit;
import org.llrp.ltk.types.UnsignedShort;

/**
 * Constants class for the LLRP plugin.
 * 
 * @author Matthew Dean
 */
public class LLRPConstants {
	/**
	 * Default IP for the reader.
	 */
	public final static String LOCALHOST = "127.0.0.1";

	/**
	 * Default port for the reader.
	 */
	public final static String PORT = "5084";

	/**
	 * Default port for the reader.
	 */
	public final static String PORT_MAX = "65535";

	/**
	 * Default port for the reader.
	 */
	public final static String PORT_MIN = "0";

	/**
	 * THe interval between reconnect attempts.
	 */
	public final static String RECONNECTION_INTERVAL = "500";

	/**
	 * The max times to try to connect before giving up.
	 */
	public final static String MAX_CONNECTION_ATTEMPTS = "-1";

	/** The default path to the SET_READER_CONFIG XML */
	public final static String SET_READER_CONFIG_PATH = "config/SET_READER_CONFIG.llrp";
	
	/**
	 * A default SET_READER_CONFIG message to use in case the one from the file
	 * cannot be loaded.
	 * 
	 * @return
	 */
	public static SET_READER_CONFIG createDefaultConfig() {
		SET_READER_CONFIG setReaderConfig = new SET_READER_CONFIG();

		// Create a default RoReportSpec so that reports are sent at the end of
		// ROSpecs
		ROReportSpec roReportSpec = new ROReportSpec();
		roReportSpec.setN(new UnsignedShort(0));
		roReportSpec.setROReportTrigger(new ROReportTriggerType(
				ROReportTriggerType.None));
		TagReportContentSelector tagReportContentSelector = new TagReportContentSelector();
		tagReportContentSelector.setEnableAccessSpecID(new Bit(0));
		tagReportContentSelector.setEnableAntennaID(new Bit(1));
		tagReportContentSelector.setEnableChannelIndex(new Bit(0));
		tagReportContentSelector.setEnableFirstSeenTimestamp(new Bit(0));
		tagReportContentSelector.setEnableInventoryParameterSpecID(new Bit(0));
		tagReportContentSelector.setEnableLastSeenTimestamp(new Bit(0));
		tagReportContentSelector.setEnablePeakRSSI(new Bit(0));
		tagReportContentSelector.setEnableROSpecID(new Bit(1));
		tagReportContentSelector.setEnableSpecIndex(new Bit(0));
		tagReportContentSelector.setEnableTagSeenCount(new Bit(0));
		C1G2EPCMemorySelector epcMemSel = new C1G2EPCMemorySelector();
		epcMemSel.setEnableCRC(new Bit(0));
		epcMemSel.setEnablePCBits(new Bit(0));
		tagReportContentSelector
				.addToAirProtocolEPCMemorySelectorList(epcMemSel);
		roReportSpec.setTagReportContentSelector(tagReportContentSelector);
		setReaderConfig.setROReportSpec(roReportSpec);

		// Set default AccessReportSpec

		AccessReportSpec accessReportSpec = new AccessReportSpec();
		accessReportSpec.setAccessReportTrigger(new AccessReportTriggerType(
				AccessReportTriggerType.Whenever_ROReport_Is_Generated));
		setReaderConfig.setAccessReportSpec(accessReportSpec);

		// Set up reporting for AISpec events, ROSpec events, and GPI Events

		ReaderEventNotificationSpec eventNoteSpec = new ReaderEventNotificationSpec();
		EventNotificationState noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.AISpec_Event));
		noteState.setNotificationState(new Bit(0));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.ROSpec_Event));
		noteState.setNotificationState(new Bit(0));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.GPI_Event));
		noteState.setNotificationState(new Bit(1));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		setReaderConfig.setReaderEventNotificationSpec(eventNoteSpec);

		setReaderConfig.setResetToFactoryDefault(new Bit(0));
		
		GPIPortCurrentState gpiState1 = new GPIPortCurrentState();
		gpiState1.setGPIPortNum(new UnsignedShort(1));
		gpiState1.setConfig(new Bit(1));
		gpiState1.setState(new GPIPortState(GPIPortState.Low));
		
		GPIPortCurrentState gpiState2 = new GPIPortCurrentState();
		gpiState2.setGPIPortNum(new UnsignedShort(2));
		gpiState2.setConfig(new Bit(1));
		gpiState2.setState(new GPIPortState(GPIPortState.Low));
		
		GPIPortCurrentState gpiState3 = new GPIPortCurrentState();
		gpiState3.setGPIPortNum(new UnsignedShort(3));
		gpiState3.setConfig(new Bit(1));
		gpiState3.setState(new GPIPortState(GPIPortState.Low));
		
		GPIPortCurrentState gpiState4 = new GPIPortCurrentState();
		gpiState4.setGPIPortNum(new UnsignedShort(4));
		gpiState4.setConfig(new Bit(1));
		gpiState4.setState(new GPIPortState(GPIPortState.Low));
		
		setReaderConfig.addToGPIPortCurrentStateList(gpiState1);
		setReaderConfig.addToGPIPortCurrentStateList(gpiState2);
		setReaderConfig.addToGPIPortCurrentStateList(gpiState3);
		setReaderConfig.addToGPIPortCurrentStateList(gpiState4);

		return setReaderConfig;
	}

}

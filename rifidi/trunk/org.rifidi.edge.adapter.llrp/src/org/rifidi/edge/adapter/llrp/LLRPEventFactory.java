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

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.llrp.ltk.generated.interfaces.AirProtocolTagData;
import org.llrp.ltk.generated.messages.READER_EVENT_NOTIFICATION;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;
import org.llrp.ltk.generated.parameters.AntennaID;
import org.llrp.ltk.generated.parameters.C1G2_CRC;
import org.llrp.ltk.generated.parameters.C1G2_PC;
import org.llrp.ltk.generated.parameters.EPCData;
import org.llrp.ltk.generated.parameters.EPC_96;
import org.llrp.ltk.generated.parameters.GPIEvent;
import org.llrp.ltk.generated.parameters.TagReportData;
import org.llrp.ltk.types.LLRPMessage;
import org.llrp.ltk.types.UnsignedShort;
import org.rifidi.edge.notification.EPCGeneration2Event;
import org.rifidi.edge.notification.ReadCycle;
import org.rifidi.edge.notification.StandardTagReadEventFieldNames;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * This factory creates events that can be added to the Esper runtime from LLRP
 * messages
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class LLRPEventFactory {

	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(LLRPEventFactory.class);

	/**
	 * Create an event that can be added to the esper runtime.
	 * 
	 * @param message
	 *            The message from the LLRP reader
	 * @param readerID
	 *            The ID of the reader that recieved the message
	 * @return the Rifidi Event or null if no relavent can be created
	 */
	public static Object createEvent(LLRPMessage message, String readerID, Map<Integer,Integer> rssiFilter) {
		// If we have A RO_ACCESS_REPORT, return a ReadCycle
		if (message instanceof RO_ACCESS_REPORT) {
			return createReadCycle((RO_ACCESS_REPORT) message, readerID, rssiFilter);
		}
		// If we have a GPIEvent Notification, return a GPIEvent
		if (message instanceof READER_EVENT_NOTIFICATION) {
			READER_EVENT_NOTIFICATION notification = (READER_EVENT_NOTIFICATION) message;
			GPIEvent gpiEvent = notification.getReaderEventNotificationData()
					.getGPIEvent();
			if (gpiEvent != null) {
				return createGPIEvent(gpiEvent, readerID);
			}
		}
		return null;
	}

	/**
	 * This method parses a RO_ACCESS_REPORT into a ReadCycle
	 * 
	 * @param rar
	 *            The RO_ACCESS_REPORT to parse
	 * @param readerID
	 *            The reader ID of the reader that got the report
	 * @return
	 */
	private static ReadCycle createReadCycle(RO_ACCESS_REPORT rar,
			String readerID, Map<Integer,Integer> rssiFilterMap) {
		List<TagReportData> trdl = rar.getTagReportDataList();
		Set<TagReadEvent> tagreaderevents = new HashSet<TagReadEvent>();
		for (TagReportData t : trdl) {
			AntennaID antid = t.getAntennaID();
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			if (t.getEPCParameter() instanceof EPCData) {
				int epcLength = 0; // in 16-bit words
				List<AirProtocolTagData> aptdList = t.getAirProtocolTagDataList();
				if (aptdList != null) {
					for (AirProtocolTagData aptd : aptdList) {
						if (aptd instanceof C1G2_PC) {
							UnsignedShort pcBits = ((C1G2_PC) aptd).getPC_Bits();
							if (pcBits != null && pcBits.toInteger() != null) {
								epcLength = pcBits.intValue() >>> 11;
								// Multiply by 4 to get the length
								epcLength = epcLength * 4;
							}
						}
					}
				}
				EPCData id = (EPCData) t.getEPCParameter();
				String EPCData = id.getEPC().toString();
				EPCData = StringUtils.leftPad(EPCData, epcLength * 2, "0");
				gen2event.setEPCMemory(parseString(EPCData), EPCData, (id.getByteLength()-1) * 8);
			} else {
				int epcLength = 0;
				List<AirProtocolTagData> aptdList = t.getAirProtocolTagDataList();
				if (aptdList != null) {
					for (AirProtocolTagData aptd : aptdList) {
						if (aptd instanceof C1G2_PC) {
							UnsignedShort pcBits = ((C1G2_PC) aptd).getPC_Bits();
							if (pcBits != null && pcBits.toInteger() != null) {
								epcLength = pcBits.intValue() >>> 11;
								// Multiply by 4 to get the length
								epcLength = epcLength * 4;
							}
						}
					}
				}
				EPC_96 id = (EPC_96) t.getEPCParameter();
				String EPCData = id.getEPC().toString();
				EPCData = StringUtils.leftPad(EPCData, epcLength, "0");
				gen2event.setEPCMemory(parseString(EPCData), EPCData, 96);
			}
			TagReadEvent tag;
			if(t.getLastSeenTimestampUTC()==null) {
				tag = new TagReadEvent(readerID, gen2event, antid
					.getAntennaID().intValue(), System.currentTimeMillis());
			} else {
				tag = new TagReadEvent(readerID, gen2event, antid
						.getAntennaID().intValue(), t.getLastSeenTimestampUTC().getMicroseconds().toLong()/1000);
					
			}
			// Add the custom information to the tags.
			if (t.getROSpecID() != null) {
				String rosid = t.getROSpecID().getROSpecID().toInteger()
						.toString();
				tag.addExtraInformation(LLRPTagReadEventFieldNames.ROSPEC_ID,
						rosid);
			}
			if (t.getPeakRSSI() != null) {
				Integer rssi = t.getPeakRSSI().getPeakRSSI().toInteger();
				try {
				if (rssiFilterMap != null) {
					boolean filter = false;
					if (rssiFilterMap.get(tag.getAntennaID()) != null && rssiFilterMap.get(tag.getAntennaID())!=0) {
						filter = rssiFilterMap.get(tag.getAntennaID()) < rssi;
					} else if (rssiFilterMap.get(0) != null && rssiFilterMap.get(0)!=0) {
						filter = rssiFilterMap.get(0) > rssi;
					}
					if (filter) {
						continue;
					}
				}
				} catch(Exception e) {
					e.printStackTrace();
				}
				tag.addExtraInformation(StandardTagReadEventFieldNames.RSSI, rssi.toString());
			}

			if (t.getSpecIndex() != null) {
				String specindex = t.getSpecIndex().getSpecIndex().toInteger()
						.toString();
				tag.addExtraInformation(LLRPTagReadEventFieldNames.SPEC_INDEX,
						specindex);
			}
			if (t.getInventoryParameterSpecID() != null) {
				String invparamspecid = t.getInventoryParameterSpecID()
						.getInventoryParameterSpecID().toInteger().toString();
				tag.addExtraInformation(
						LLRPTagReadEventFieldNames.INVPARAMSPECID,
						invparamspecid);
			}

			if (t.getChannelIndex() != null) {
				String channelindex = t.getChannelIndex().getChannelIndex()
						.toInteger().toString();
				tag.addExtraInformation(
						LLRPTagReadEventFieldNames.CHANNELINDEX, channelindex);
			}

			if (t.getFirstSeenTimestampUTC() != null) {
				String firstseenutc = t.getFirstSeenTimestampUTC()
						.getMicroseconds().toBigInteger().toString();
				tag.addExtraInformation(
						LLRPTagReadEventFieldNames.FIRSTSEENUTC, firstseenutc);
			}

			if (t.getFirstSeenTimestampUptime() != null) {
				String firstseenuptime = t.getFirstSeenTimestampUptime()
						.getMicroseconds().toBigInteger().toString();
				tag.addExtraInformation(
						LLRPTagReadEventFieldNames.FIRSTSEENUPTIME,
						firstseenuptime);
			}

			if (t.getLastSeenTimestampUTC() != null) {
				String lastseenutc = t.getLastSeenTimestampUTC()
						.getMicroseconds().toBigInteger().toString();
				tag.addExtraInformation(LLRPTagReadEventFieldNames.LASTSEENUTC,
						lastseenutc);
			}

			if (t.getLastSeenTimestampUptime() != null) {
				String lastseenuptime = t.getLastSeenTimestampUptime()
						.getMicroseconds().toBigInteger().toString();
				tag.addExtraInformation(
						LLRPTagReadEventFieldNames.LASTSEENUPTIME,
						lastseenuptime);
			}

			if (t.getTagSeenCount() != null) {
				String tagseencount = t.getTagSeenCount().getTagCount()
						.toInteger().toString();
				tag.addExtraInformation(
						LLRPTagReadEventFieldNames.TAGSEENCOUNT, tagseencount);
			}

			for (AirProtocolTagData aptd : t.getAirProtocolTagDataList()) {
				if (aptd instanceof C1G2_CRC) {
					String crc = ((C1G2_CRC) aptd).getCRC().toInteger()
							.toString();
					tag.addExtraInformation(
							LLRPTagReadEventFieldNames.AIRPROT_CRC, crc);
				} else if (aptd instanceof C1G2_PC) {
					String pc = ((C1G2_PC) aptd).getPC_Bits().toInteger()
							.toString();
					tag.addExtraInformation(
							LLRPTagReadEventFieldNames.AIRPROT_PC, pc);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug(tag.getTag().getFormattedID() + " ANT: "
						+ tag.getAntennaID());
			}
			
			tagreaderevents.add(tag);
		}
		
		if(tagreaderevents.size()==0) {
			return null;
		}
		
		return new ReadCycle(tagreaderevents, readerID, System.currentTimeMillis());
	}

	/**
	 * A helper method that takes in a hex string and returns a BigInteger
	 * 
	 * @param input
	 * @return
	 */
	private static BigInteger parseString(String input) {
		BigInteger retVal = null;
		try {
			input = input.trim();
			if (input.length() % 2 != 0) {
				input = "0" + input;
			}
			if (input.length() % 2 != 0) {
				input = "0" + input;
			}
			retVal = new BigInteger(input, 16);
		} catch (Exception e) {
			logger.warn("There was a problem when parsing LLRP Tags.  "
					+ "tag has not been added", e);
		}
		return retVal;
	}

	/**
	 * This method parses a GPIEvent from LLRP and returns a Rifidi GPI event
	 * that can be added to esper
	 * 
	 * @param llrpGPIEvent
	 * @param readerID
	 * @return
	 */
	private static org.rifidi.edge.notification.GPIEvent createGPIEvent(
			GPIEvent llrpGPIEvent, String readerID) {
		int port = llrpGPIEvent.getGPIPortNumber().toInteger();
		boolean state = llrpGPIEvent.getGPIEvent().toBoolean();
		return new org.rifidi.edge.notification.GPIEvent(readerID, port, state);
	}
}

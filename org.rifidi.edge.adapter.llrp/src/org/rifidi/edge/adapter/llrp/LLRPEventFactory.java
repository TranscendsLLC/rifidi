/**
 * 
 */
package org.rifidi.edge.adapter.llrp;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
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
	public static Object createEvent(LLRPMessage message, String readerID) {
		// If we have A RO_ACCESS_REPORT, return a ReadCycle
		if (message instanceof RO_ACCESS_REPORT) {
			return createReadCycle((RO_ACCESS_REPORT) message, readerID);
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
			String readerID) {
		List<TagReportData> trdl = rar.getTagReportDataList();
		Set<TagReadEvent> tagreaderevents = new HashSet<TagReadEvent>();
		for (TagReportData t : trdl) {
			AntennaID antid = t.getAntennaID();
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			if (t.getEPCParameter() instanceof EPCData) {
				EPCData id = (EPCData) t.getEPCParameter();
				String EPCData = id.getEPC().toString();
				//Left padding non-significant zeros, as they are not preserved.  
				//FIXME: This has not been tested yet.  
				EPCData = StringUtils.leftPad(EPCData,
						(id.getByteLength()-1) * 2, "0");
				gen2event.setEPCMemory(parseString(EPCData), (id
						.getByteLength()-1) * 8);
			} else {
				EPC_96 id = (EPC_96) t.getEPCParameter();
				String EPCData = id.getEPC().toString();
				//Left padding, as non-significant zeros are not preserved.  
				EPCData = StringUtils.leftPad(EPCData, 24, "0");
				gen2event.setEPCMemory(parseString(EPCData), 96);
			}

			TagReadEvent tag = new TagReadEvent(readerID, gen2event, antid
					.getAntennaID().intValue(), System.currentTimeMillis());
			// Add the custom information to the tags.
			if (t.getROSpecID() != null) {
				String rosid = t.getROSpecID().getROSpecID().toInteger()
						.toString();
				tag.addExtraInformation(LLRPTagReadEventFieldNames.ROSPEC_ID,
						rosid);
			}
			if (t.getPeakRSSI() != null) {
				String rssi = t.getPeakRSSI().getPeakRSSI().toInteger()
						.toString();
				tag.addExtraInformation(StandardTagReadEventFieldNames.RSSI,
						rssi);
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

			// for (String key : tag.getExtraInformation().keySet()) {
			// System.out.println(key + ", "
			// + tag.getExtraInformation().get(key));
			// }
			tagreaderevents.add(tag);
		}
		return new ReadCycle(tagreaderevents, readerID, System
				.currentTimeMillis());
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
			byte[] decode = Hex.decodeHex(input.toCharArray());
			retVal = new BigInteger(decode);
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

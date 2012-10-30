/**
 * 
 */
package org.rifidi.edge.adapter.thinkify50;

import java.math.BigInteger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.rifidi.edge.notification.EPCGeneration2Event;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class Thinkify50TagHandler {
	private Thinkify50SensorSession session;

	private String readerID;

	/**
	 * 
	 * @param session
	 */
	public Thinkify50TagHandler(Thinkify50SensorSession session, String readerID) {
		this.session = session;
		this.readerID = readerID;
	}

	/**
	 * 
	 * @param tag
	 */
	public TagReadEvent tagArrived(String epc, Long time, Float rssi, int count) {
		EPCGeneration2Event gen2event = new EPCGeneration2Event();
		int numbits = epc.length() * 4;
		BigInteger bigint = null;
		try {
			bigint = new BigInteger(Hex.decodeHex(epc.toCharArray()));
		} catch (DecoderException e) {
			throw new RuntimeException("Cannot decode tag: " + epc);
		}
		gen2event.setEPCMemory(bigint, numbits);
		TagReadEvent tre = new TagReadEvent(readerID, gen2event, 1, time);
		tre.addExtraInformation("RSSI", rssi);
		return tre;
	}

}

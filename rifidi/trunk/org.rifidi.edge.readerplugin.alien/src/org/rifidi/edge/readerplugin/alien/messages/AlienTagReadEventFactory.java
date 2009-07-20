/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.messages;

import java.math.BigInteger;

import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;
import org.rifidi.edge.core.services.notification.data.EPCGeneration1Event;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * This is a factory to create new TagReadEvent objects from AlienTag objects
 * for a single reader.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienTagReadEventFactory {

	/** The reader this factory is used for */
	private String readerID;

	/**
	 * Constructor
	 * 
	 * @param readrID
	 *            The FACTORY_ID of the reader this factory is used for
	 */
	public AlienTagReadEventFactory(String readerID) {
		this.readerID = readerID;
	}

	/**
	 * Convert an AlienTag into a TagReadEvent. This method is threadsafe.
	 * 
	 * @param alienTag
	 *            The tag to convert
	 * @return
	 */
	public TagReadEvent getTagReadEvent(AlienTag alienTag) {
		// the new event
		DatacontainerEvent tagData = null;
		// a big integer representation of the epc
		BigInteger epc = new BigInteger(alienTag.getId_hex(), 16);

		// choose whether to make a gen1 or a gen2 tag
		if (alienTag.getProtocol() == 1) {
			EPCGeneration1Event gen1event = new EPCGeneration1Event();
			// make some wild guesses on the length of the epc field
			if (epc.bitLength() > 96) {
				gen1event.setEPCMemory(epc, 192);
			} else if (epc.bitLength() > 64) {
				gen1event.setEPCMemory(epc, 96);
			} else {
				gen1event.setEPCMemory(epc, 64);
			}
			tagData = gen1event;
		} else {
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			// make some wild guesses on the length of the epc field
			if (epc.bitLength() > 96) {
				gen2event.setEPCMemory(epc, 192);
			} else if (epc.bitLength() > 64) {
				gen2event.setEPCMemory(epc, 96);
			} else {
				gen2event.setEPCMemory(epc, 64);
			}
			tagData = gen2event;
		}
		return new TagReadEvent(readerID, tagData, alienTag.getAntenna(),
				alienTag.getLastSeenDate().getTime());
	}

}

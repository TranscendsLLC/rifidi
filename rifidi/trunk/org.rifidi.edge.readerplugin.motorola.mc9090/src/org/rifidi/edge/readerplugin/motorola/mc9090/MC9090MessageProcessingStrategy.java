/**
 * 
 */
package org.rifidi.edge.readerplugin.motorola.mc9090;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategy;
import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.ReadCycleMessageCreator;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.readerplugin.motorola.mc9090.tags.MC9090TagMessage;
import org.springframework.jms.core.JmsTemplate;

/**
 * This is a strategy for handling a complete MC9090 message. It parses the
 * message into tag events and places the tags on the internal JMS message bus
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class MC9090MessageProcessingStrategy implements
		MessageProcessingStrategy {

	/** Template for sending out tag reads */
	private JmsTemplate template;
	/** Session for this Handler */
	private SensorSession session;

	/**
	 * @param template
	 * @param session
	 */
	public MC9090MessageProcessingStrategy(JmsTemplate template,
			SensorSession session) {
		super();
		this.template = template;
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategy#
	 * processMessage(byte[])
	 */
	@Override
	public void processMessage(byte[] message) {
		String strMessage = new String(message);
		Set<TagReadEvent> tagReadEvents = new HashSet<TagReadEvent>();

		// If we have seen at least one tag
		if (!strMessage.equals("")) {
			// tags are deliminated by a newline character
			for (String rawTagMessage : strMessage.split("\n")) {
				// parse the tag
				MC9090TagMessage tagMessage = new MC9090TagMessage(
						rawTagMessage);
				// create the TagReadEvent
				TagReadEvent event = createTagReadEvent(tagMessage);
				if (event != null) {
					tagReadEvents.add(event);
				}
			}
		}

		// create the read cycle
		ReadCycle readCycle = new ReadCycle(tagReadEvents, session.getSensor()
				.getID(), System.currentTimeMillis());
		// give the read cycle to the logical sensor
		session.getSensor().send(readCycle);
		// put the tag message on the internal JMS bus
		template.send(new ReadCycleMessageCreator(readCycle));

	}

	/**
	 * A private helper method to wrap a MC9090TagMessage in a TagReadEvent
	 * 
	 * @param mc9090Tag
	 * @return
	 */
	private TagReadEvent createTagReadEvent(MC9090TagMessage mc9090Tag) {
		// the new event
		DatacontainerEvent tagData = null;
		// a big integer representation of the epc
		BigInteger epc = new BigInteger(mc9090Tag.getHexID(), 16);

		// choose whether to make a gen1 or a gen2 tag
		if (mc9090Tag.getType().contains("GEN2")) {
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
		} else {
			return null;
		}
		return new TagReadEvent(session.getSensor().getID(), tagData, mc9090Tag
				.getAntenna(), mc9090Tag.getLastSeenTime().getTime());
	}

}

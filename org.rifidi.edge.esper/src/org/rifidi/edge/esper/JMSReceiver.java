/**
 * 
 */
package org.rifidi.edge.esper;

import java.io.IOException;
import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epcglobalinc.LevelTypeList;
import org.fosstrak.tdt.TDTEngine;
import org.rifidi.edge.core.messages.EPCGeneration2Event;

import com.espertech.esper.client.EPServiceProvider;

/**
 * Instances of this class are used to receive JMS messages and forward them to
 * esper.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class JMSReceiver implements MessageListener {
	/** Logger for this class. */
	private static final Log log = LogFactory.getLog(JMSReceiver.class);
	/** Tag data translation engine. */
	private TDTEngine engine;
	/** Instance of esper engine. */
	private EPServiceProvider epService;

	/**
	 * Constructor.
	 */
	public JMSReceiver() {
		try {
			engine = new TDTEngine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param esperManagement
	 *            the esperManagement to set
	 */
	public void setEsperManagement(EsperManagementService esperManagement) {
		// TODO: we might want to do something if we already got a service
		epService = esperManagement.getProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message message) {
		try {
			log.debug("message " + message);
			Object obj = ((ObjectMessage) message).getObject();
			if (obj instanceof EPCGeneration2Event) {
				HashMap<String, String> extraparams = new HashMap<String, String>();
				String mem = ((EPCGeneration2Event) obj).getEPCMemory().toString(2);
				int fill = ((EPCGeneration2Event) obj).getEpcLength()
						- mem.length();
				// big integer swallows leading zeroes, reattech 'em
				while (fill > 0) {
					mem = "0" + mem;
					fill--;
				}
				ProcessedEvent event=new ProcessedEvent((EPCGeneration2Event) obj, engine
						.convert(mem, extraparams,
								LevelTypeList.PURE_IDENTITY), mem,
						((EPCGeneration2Event) obj).getEPCMemory().toString(16));
				// forward event to esper
				System.out.println(event.getHex());
				epService.getEPRuntime().sendEvent(event);
				return;
			}
			epService.getEPRuntime().sendEvent(obj);
			// acknowledege as the last step, just to be sure
			message.acknowledge();
		} catch (JMSException e) {
			log.error("Failed to acknowledge: " + e, e);
		}
	}

}

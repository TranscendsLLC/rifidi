/**
 * 
 */
package org.rifidi.edge.esper;
//TODO: Comments
import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.messages.ReadCycle;
import org.rifidi.edge.core.messages.TagReadEvent;

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
	/** Instance of esper engine. */
	private EPServiceProvider epService;

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
			if(message instanceof ObjectMessage){
				Object obj = ((ObjectMessage) message).getObject();
				if (obj instanceof ReadCycle) {
					ReadCycle cycle = (ReadCycle) obj;
					for (TagReadEvent ev : cycle.getTags()) {
						epService.getEPRuntime().sendEvent(ev);
					}
					return;
				}
				epService.getEPRuntime().sendEvent(obj);
				// acknowledege as the last step, just to be sure	
			}
			message.acknowledge();
		} catch (JMSException e) {
			log.error("Failed to acknowledge: " + e, e);
		}
	}

}

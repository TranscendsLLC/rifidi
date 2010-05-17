/**
 * 
 */
package org.rifidi.edge.app.tracking;

import org.rifidi.edge.core.app.api.JMSRifidiApp;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * Sends tags out over jms with this format:
 * 
 * tag:12345|time:1234556899|readerid:alien_1|antennaid:2|rssi:2.4
 * 
 * @author kyle
 * 
 */
public class TagMonitor extends JMSRifidiApp {

	/**
	 * Constructor
	 * 
	 * @param name
	 */
	public TagMonitor(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.RifidiApp#_start()
	 */
	@Override
	protected void _start() {
		super._start();
		StatementAwareUpdateListener listener = new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					for (EventBean bean : arg0) {
						TagReadEvent event = (TagReadEvent) bean
								.getUnderlying();
						sendTextMessage(formTagMessage(event));
					}
				}

			}
		};

		addStatement("select * from ReadCycle[select * from tags]", listener);

	}

	private String formTagMessage(TagReadEvent event) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("tag:" + event.getTag().getFormattedID());
		buffer.append("|time:" + event.getTimestamp());
		buffer.append("|readerid:" + event.getReaderID());
		buffer.append("|antennaid:" + event.getAntennaID());
		if (event.getExtraInformation().containsKey(TagReadEvent.RSSI))
			buffer.append("|rssi:"
					+ event.getExtraInformation().get(TagReadEvent.RSSI));

		return buffer.toString();
	}

}

/**
 * 
 */
package org.rifidi.edge.app.diag.tagmonitor;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.core.app.api.AbstractRifidiApp;
import org.rifidi.edge.core.app.api.AppState;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EventBean;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class TagMonitorApp extends AbstractRifidiApp {

	private Log logger = LogFactory.getLog(TagMonitorApp.class);

	/**
	 * @param group
	 * @param name
	 */
	public TagMonitorApp(String group, String name) {
		super(group, name);
		System.out.println("Got into the constructor!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_start()
	 */
	@Override
	protected void _start() {
		System.out.println("Called the esper!");

		addStatement("create window tagmonitortags.win:time(1 sec)"
				+ "as TagReadEvent");
		addStatement("insert into tagmonitortags select * from ReadCycle[select * from tags]");
	}

	/**
	 * Starts the tag monitor.
	 * 
	 * @param readerID
	 * @return
	 */
	Map<BigInteger, Integer> startTagMonitor(String readerID) {
		if (getState() != AppState.STARTED) {
			logger.warn("TagApp not started. Use 'startapp <AppID>'");
		}
		Map<BigInteger, Integer> tagMap = new HashMap<BigInteger, Integer>();
		EPOnDemandQueryResult result = executeQuery("select * from tagmonitortags where readerID=\""
				+ readerID + "\"");
		if (result.getArray() != null) {
			for (EventBean event : result.getArray()) {
				TagReadEvent tag = (TagReadEvent) event.getUnderlying();
				BigInteger tagid = tag.getTag().getID();
				if (!tagMap.containsKey(tagid)) {
					tagMap.put(tagid, 1);
				} else {
					tagMap.put(tagid, tagMap.get(tagid) + 1);
				}
			}
		}
		return tagMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#getCommandProvider()
	 */
	@Override
	protected CommandProvider getCommandProvider() {
		TagMonitorCommandProvider comm = new TagMonitorCommandProvider();
		comm.setTagMonitorApp(this);
		return comm;
	}

}

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
/**
 * 
 */
package org.rifidi.edge.api.service.tagmonitor;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.api.service.EsperUtil;
import org.rifidi.edge.api.service.RifidiAppService;
import org.rifidi.edge.notification.RSSITagReadEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * 
 * @author Matthew Dean matt@transcends.co
 */
public class RSSIMonitoringServiceImpl extends
		RifidiAppService<RSSIReadZoneSubscriber> implements RSSIMonitoringService {
	
	private static final String TAG_ID = "tagID";
	private static final String READER_ID = "(maxby(avgRSSI)).readerID()";
	private static final String MAX_RSSI = "max(avgRSSI)";
	private static final String SUM_RSSI = "tagCount";
	private static final String ANTENNA = "antenna";
	
	private boolean useRegex;

	
	public RSSIMonitoringServiceImpl(String group, String name) {
		super(group, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.tagmonitor.RSSIMonitoringService#subscribe
	 * (org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber,
	 * java.util.List, java.lang.Float, java.lang.Float,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public void subscribe(RSSIReadZoneSubscriber subscriber,
			HashMap<String,ReadZone> readZones, Float windowTime,
			TimeUnit timeUnit, Integer countThreshold, Double minAvgRSSIThreshold, boolean useRegex) {
		this.useRegex = useRegex;
		this.readZones = readZones;
		this.subscribe(subscriber, new RSSIMonitorEsperFactory(new LinkedList<ReadZone>(readZones.values()), getCounter(), 
				windowTime, timeUnit, countThreshold, minAvgRSSIThreshold, useRegex));
	}

	@Override
	protected StatementAwareUpdateListener createListener(
			final RSSIReadZoneSubscriber subscriber) {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {

				// all additions
				if (arg0 != null) {
					List<RSSITagReadEvent> retVal = new LinkedList<RSSITagReadEvent>();
					for (EventBean b : arg0) {
						HashMap<Object,Object> tre = (HashMap<Object,Object>) b.getUnderlying();
						RSSITagReadEvent tag = new RSSITagReadEvent((String)tre.get(TAG_ID),(String)tre.get(READER_ID),(Integer)tre.get(ANTENNA),(Double)tre.get(MAX_RSSI),(Double)tre.get(SUM_RSSI));

						if ((String)tre.get(READER_ID)!=null) {
							tag.setReadZone(EsperUtil.returnReaderMatch(readZones, useRegex, (String)tre.get(READER_ID), (Integer)tre.get(ANTENNA)));
						}
						retVal.add(tag);
					}
					subscriber.tagArrived(removeDuplicates(retVal));
				}
				// all deletions
				if (arg1 != null) {
					List<RSSITagReadEvent> retVal = new LinkedList<RSSITagReadEvent>();
					for (EventBean b : arg0) {
						HashMap<Object,Object> tre = (HashMap<Object,Object>) b.getUnderlying();
						RSSITagReadEvent tag = new RSSITagReadEvent((String)tre.get(TAG_ID),(String)tre.get(READER_ID),(Integer)tre.get(ANTENNA),(Double)tre.get(MAX_RSSI),(Double)tre.get(SUM_RSSI));
						retVal.add(tag);
					}
					subscriber.tagDeparted(removeDuplicates(retVal));
				}

			}
		};
	}
	
	/**
	 * Removes duplicate values, just in case the esper statement returns multiple tags.  
	 * 
	 * @param tags
	 * @return
	 */
	private List<RSSITagReadEvent> removeDuplicates(List<RSSITagReadEvent> tags) {
		List<RSSITagReadEvent> retVal = new LinkedList<RSSITagReadEvent>();
		for(RSSITagReadEvent tag:tags) {
			//As currently exists, the RSSI esper statements sometimes return 2 identical tags.  This filters them out.  
			if(nothingNull(tag)) {
				boolean exists=false;
				for(RSSITagReadEvent cur:retVal) {
					if(tag.getTagID().equals(cur.getTagID())) {
						exists=true;
						break;
					}
				}
				if(!exists) {
					retVal.add(tag);
				}
			}
		}
		return retVal;
	}
	
	/**
	 * Make sure nothing is null in the returned tag.  
	 * 
	 * @param tag
	 * @return
	 */
	private boolean nothingNull(RSSITagReadEvent tag) {
		return (tag.getTagID()!=null && tag.getReaderID()!=null);
	}
}

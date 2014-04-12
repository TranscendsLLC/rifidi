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
package org.rifidi.edge.api.service.sensormonitor;

import java.util.ArrayList;
import java.util.Set;

import org.rifidi.edge.api.service.RifidiAppEsperFactory;
import org.rifidi.edge.api.service.RifidiAppService;
import org.rifidi.edge.notification.SensorStatusEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This is an implementation of the SensorStatusMonitorService.
 * 
 * @author Matthew Dean
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class SensorStatusMonitoringServiceImpl extends
		RifidiAppService<SensorStatusSubscriber> implements
		SensorStatusMonitoringService {

	/**
	 * Constructor
	 * 
	 * @param group
	 *            the group this application is a part of
	 * @param name
	 *            The name of the application
	 */
	public SensorStatusMonitoringServiceImpl(String group, String name) {
		super(group, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.api.service.sensormonitor.
	 * SensorStatusMonitoringService
	 * #subscribe(org.rifidi.edge.api.service
	 * .sensormonitor.SensorStatusSubscriber, java.util.Set)
	 */
	@Override
	public void subscribe(final SensorStatusSubscriber subscriber,
			Set<String> sensorsToMonitor) {
		ArrayList<String> sensorList = new ArrayList<String>();
		if (sensorsToMonitor != null) {
			sensorList.addAll(sensorsToMonitor);
		}
		RifidiAppEsperFactory esperFactory = new SensorStatusMonitorEsperFactory(
				sensorList);
		subscribe(subscriber, esperFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.RifidiAppService#createListener(
	 * org.rifidi.edge.api.service.RifidiAppSubscriber)
	 */
	@Override
	protected StatementAwareUpdateListener createListener(
			final SensorStatusSubscriber subscriber) {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					for (EventBean bean : arg0) {
						Object event = bean.getUnderlying();
						if (event instanceof SensorStatusEvent) {
							subscriber
									.handleSensorStatusEvent((SensorStatusEvent) event);
						}
					}
				}

			}
		};
	}

}

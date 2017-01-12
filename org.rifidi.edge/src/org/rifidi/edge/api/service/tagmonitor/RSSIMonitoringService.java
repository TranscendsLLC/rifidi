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
package org.rifidi.edge.api.service.tagmonitor;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Matthew Dean matt@transcends.co
 */
public interface RSSIMonitoringService {
   /**
    * Subscribe to the RSSIMonitoring service for the given readzones.  
    * 
    * @param subscriber
    *          The subscriber that will be invoked when tags arrive.
    * @param readZones
    *          The readzones that will be monitored.  Pass in an empty map if you wish to monitor per reader.  
    * @param windowTime
    *          The timeout that will be used to determine if a tag has gone to a new zone.  Times less than 5s are not recommended.  
    * @param timeUnit
    *          The unit of time to use with the timeout.  
    * @param countThreshold
    *          The threshold for number of times a tag must be read before the readzone it has shown up at will be switched.  Do not
    *          set this to greater than the windowTime divided by the frequency in seconds that tag reports will show up as configured
    *          by your reader.  
    * @param minAvgRSSIThreshold
    *          The lowest average RSSI that will have to be seen before the readzone is switched.
    * @param changeRSSIThreshold
    *          The threshold for the amount the maximum average RSSI must be greater than the second maximum average RSSI
    *          (by readzone) before the readzone is switched. 
    * @param useRegex
    *          Are you using regular expressions in the readezones you pass in?  
    */
   public void subscribe(RSSIReadZoneSubscriber subscriber,
         HashMap<String,ReadZone> readZones, Float windowTime, TimeUnit timeUnit, Integer countThreshold, Double minAvgRSSIThreshold, Double changeRSSIThreshold, boolean useRegex);
   public void unsubscribe(RSSIReadZoneSubscriber subscriber);
}
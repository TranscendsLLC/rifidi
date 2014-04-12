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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.api.service.EsperUtil;
import org.rifidi.edge.api.service.RifidiAppEsperFactory;

/**
 * @author kyle
 *
 */
public class UniqueTagIntervalEsperFactory implements RifidiAppEsperFactory {

	private final List<ReadZone> readZones;
	private final Float notifyTime;
	private final TimeUnit notifyTimeUnit;
	private final String windowName;
	private final String timerWindowName;
	private final String delayedStreamName;
	

	/**
	 * @param readZones
	 * @param notifyTime
	 * @param notifyTimeUnit
	 * @param uniqueID
	 */
	public UniqueTagIntervalEsperFactory(List<ReadZone> readZones,
			Float notifyTime, TimeUnit notifyTimeUnit, int uniqueID) {
		super();
		this.readZones = new ArrayList<ReadZone>();
		if(readZones!=null){
			this.readZones.addAll(readZones);
		}
		this.notifyTime = notifyTime;
		this.notifyTimeUnit = notifyTimeUnit;
		windowName = "rifidi_appservice_uniqueTagInterval"+uniqueID;
		timerWindowName="rifidi_appservice_uniqueTagInterval_timer"+uniqueID;
		delayedStreamName="rifidi_appservice_uniqueTagInterval_delayed"+uniqueID;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.service.RifidiAppEsperFactory#createQuery()
	 */
	@Override
	public String createQuery() {
		return "select * from " + windowName;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.service.RifidiAppEsperFactory#createStatements()
	 */
	@Override
	public List<String> createStatements() {
		ArrayList<String> statements = new ArrayList<String>();
		statements.add("create window "+windowName+".std:firstunique(tag.ID) as TagReadEvent");
		statements.add(EsperUtil.buildInsertStatement(windowName, readZones, false));
		statements.add("create window "+timerWindowName+".win:time(" +
				EsperUtil.timeUnitToEsperTime(notifyTime, notifyTimeUnit)+
				") as TagReadEvent");
		statements.add("insert into "+timerWindowName+" select * from " + windowName);
		statements.add("insert rstream into "+delayedStreamName+" select * from " + timerWindowName);
		statements.add("on pattern [every t= "+delayedStreamName+"] " +
				"delete from "+windowName+" where tag.ID=t.tag.ID");
		return statements;
	}

}

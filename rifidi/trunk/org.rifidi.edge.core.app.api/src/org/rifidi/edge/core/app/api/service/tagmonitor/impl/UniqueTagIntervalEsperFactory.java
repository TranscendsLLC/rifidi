/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.core.app.api.service.EsperUtil;
import org.rifidi.edge.core.app.api.service.RifidiAppEsperFactory;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;

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
	 * @see org.rifidi.edge.core.app.api.service.RifidiAppEsperFactory#createQuery()
	 */
	@Override
	public String createQuery() {
		return "select * from " + windowName;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.app.api.service.RifidiAppEsperFactory#createStatements()
	 */
	@Override
	public List<String> createStatements() {
		ArrayList<String> statements = new ArrayList<String>();
		statements.add("create window "+windowName+".std:firstunique(tag.ID) as TagReadEvent");
		statements.add(EsperUtil.buildInsertStatement(windowName, readZones));
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

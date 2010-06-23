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
public class StableSetEsperFactory implements RifidiAppEsperFactory {

	private List<ReadZone> readZones;
	private boolean unique;
	private final String stableSetWindow;
	private final String stableSetWindow_unique;
	private final String stableSetTimeString;
	
	public StableSetEsperFactory(List<ReadZone> readZones, Float stableSetTime,
			TimeUnit stableSetTimeUnit, boolean unique, int windowID) {
		this.readZones = readZones;
		this.unique=unique;
		stableSetTimeString=EsperUtil.timeUnitToEsperTime(stableSetTime, stableSetTimeUnit);
		this.stableSetWindow="rifidi_appservice_stableSetWindow"+windowID;
		this.stableSetWindow_unique="rifidi_appservice_stableSetWindow_unique"+windowID;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.app.api.service.RifidiAppEsperFactory#createQuery()
	 */
	@Override
	public String createQuery() {
		return "select rstream * from " + stableSetWindow;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.app.api.service.RifidiAppEsperFactory#createStatements()
	 */
	@Override
	public List<String> createStatements() {
		List<String> statements = new ArrayList<String>();
		statements.add("create window " + stableSetWindow + ".win:time_accum("
				+ stableSetTimeString + ") as TagReadEvent");
		if (unique) {
			statements.add("create window " + stableSetWindow_unique
					+ ".std:firstunique(tag.ID) as TagReadEvent");
			statements.add(EsperUtil.buildInsertStatement(stableSetWindow_unique,
					readZones));
			statements.add("insert into " + stableSetWindow + " select * from "
					+ stableSetWindow_unique);
		}else{
			statements.add(EsperUtil.buildInsertStatement(stableSetWindow,
					readZones));
		}
		return statements;
	}

}


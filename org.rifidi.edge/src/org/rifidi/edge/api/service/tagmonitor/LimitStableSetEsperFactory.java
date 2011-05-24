/**
 * 
 */
package org.rifidi.edge.api.service.tagmonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.api.service.EsperUtil;
import org.rifidi.edge.api.service.RifidiAppEsperFactory;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class LimitStableSetEsperFactory implements RifidiAppEsperFactory {

	private List<ReadZone> readZones;
	int limit;
	private final String stableSetWindow;
	private final String stableSetWindow_unique;
	private final String stableSetTimeString;

	public LimitStableSetEsperFactory(List<ReadZone> readZones,
			Float stableSetTime, TimeUnit stableSetTimeUnit, int limit,
			int windowID) {
		this.readZones = readZones;
		this.limit = limit;
		stableSetTimeString = EsperUtil.timeUnitToEsperTime(stableSetTime,
				stableSetTimeUnit);
		this.stableSetWindow = "rifidi_appservice_stableSetWindow" + windowID;
		this.stableSetWindow_unique = "rifidi_appservice_stableSetWindow_unique"
				+ windowID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.service.RifidiAppEsperFactory#createQuery()
	 */
	@Override
	public String createQuery() {
		return "select rstream * from " + stableSetWindow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.service.RifidiAppEsperFactory#createStatements()
	 */
	@Override
	public List<String> createStatements() {
		List<String> statements = new ArrayList<String>();
		statements.add("create window " + stableSetWindow + ".win:time_length_batch("
				+ stableSetTimeString + "," + limit + ").std:firstunique(tag.ID) as TagReadEvent");
			statements.add("create window " + stableSetWindow_unique
					+ ".win:length_batch("+ limit +").std:firstunique(tag.ID) as TagReadEvent");
			statements.add(EsperUtil.buildInsertStatement(stableSetWindow_unique,
					readZones));
			statements.add("insert into " + stableSetWindow + " select * from "
					+ stableSetWindow_unique);
		
		return statements;
	}
	
	

	}



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
public class StableSetEsperFactory implements RifidiAppEsperFactory {

	private List<ReadZone> readZones;
	private boolean unique;
	private final String stableSetWindow;
	private final String stableSetWindow_unique;
	private final String stableSetTimeString;
	private final Boolean useRegex;
	
	public StableSetEsperFactory(List<ReadZone> readZones, Float stableSetTime,
			TimeUnit stableSetTimeUnit, boolean unique, int windowID, boolean useRegex) {
		this.readZones = readZones;
		this.unique=unique;
		stableSetTimeString=EsperUtil.timeUnitToEsperTime(stableSetTime, stableSetTimeUnit);
		this.stableSetWindow="rifidi_appservice_stableSetWindow"+windowID;
		this.stableSetWindow_unique="rifidi_appservice_stableSetWindow_unique"+windowID;
		this.useRegex=useRegex;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.service.RifidiAppEsperFactory#createQuery()
	 */
	@Override
	public String createQuery() {
		return "select rstream * from " + stableSetWindow;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.service.RifidiAppEsperFactory#createStatements()
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
					readZones, useRegex));
			statements.add("insert into " + stableSetWindow + " select * from "
					+ stableSetWindow_unique);
		}else{
			statements.add(EsperUtil.buildInsertStatement(stableSetWindow,
					readZones, useRegex));
		}
		return statements;
	}

}


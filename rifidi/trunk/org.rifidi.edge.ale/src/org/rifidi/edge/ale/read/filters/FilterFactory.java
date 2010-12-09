/*
 * 
 * FilterFactory.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.ale.read.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rifidi.edge.ale.api.read.data.ECFilterListMember;
import org.rifidi.edge.ale.read.ALEDataFormats;
import org.rifidi.edge.ale.read.ALEDataTypes;

/**
 * Create filter from the ALE filter descriptions.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class FilterFactory {
	/**
	 * Create a matcher.
	 * 
	 * @param filter
	 * @return
	 */
	public Map<ALEField, List<PatternMatcher>> createMatcher(
			ECFilterListMember filter) {
		Map<ALEField, List<PatternMatcher>> retMap = new HashMap<ALEField, List<PatternMatcher>>();
		List<PatternMatcher> ret = new ArrayList<PatternMatcher>();
		ALEField field = new ALEField(filter.getFieldspec());
		if (ALEDataTypes.EPC.equals(field.getType())) {
			// epc filter pattern
			if (ALEDataFormats.EPC_PURE.equals(field.getFormat())) {
				for (String pattern : filter.getPatList().getPat()) {
					ret.add(new EPCPUREPatternMatcher(pattern));
				}
			} else if (ALEDataFormats.EPC_TAG.equals(field.getFormat())) {
				for (String pattern : filter.getPatList().getPat()) {
					ret.add(new EPCTAGPatternMatcher(pattern));
				}
			}
		} else if (ALEDataTypes.UINT.equals(field.getType())) {
			// uint filter pattern
			if (ALEDataFormats.HEX.equals(field.getFormat())) {
				for (String pattern : filter.getPatList().getPat()) {
					ret.add(new UINTHEXPatternMatcher(pattern));
				}
			} else if (ALEDataFormats.DECIMAL.equals(field.getFormat())) {
				for (String pattern : filter.getPatList().getPat()) {
					ret.add(new UINTDECIMALPatternMatcher(pattern));
				}
			}
		}
		retMap.put(field, ret);
		return retMap;
	}
}

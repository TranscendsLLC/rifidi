/*
 * 
 * GroupFactory.java
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
package org.rifidi.edge.ale.read.groups;

import org.rifidi.edge.ale.read.ALEDataFormats;
import org.rifidi.edge.ale.read.ALEDataTypes;
import org.rifidi.edge.ale.read.filters.ALEField;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class GroupFactory {
	public GroupMatcher createMatcher(ALEField field, String pattern) {
		if (ALEDataTypes.EPC.equals(field.getType())) {
			if (ALEDataFormats.EPC_TAG.equals(field.getFormat())
					|| ALEDataFormats.EPC_PURE.equals(field.getFormat())) {
				return new EPCTAGGROUPPatternMatcher(pattern);
			}
		} else if (ALEDataTypes.UINT.equals(field.getType())) {
			if (ALEDataFormats.HEX.equals(field.getFormat())) {
				return new UINTHEXGROUPPatternMatcher(pattern);
			} else if (ALEDataFormats.DECIMAL.equals(field.getFormat())) {
				return new UINTDECIMALGROUPPatternMatcher(pattern);
			}
		}
		return null;
	}
}

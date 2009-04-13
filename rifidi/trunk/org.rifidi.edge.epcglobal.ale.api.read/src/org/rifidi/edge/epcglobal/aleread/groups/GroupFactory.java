/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.groups;

import org.rifidi.edge.epcglobal.aleread.ALEDataFormats;
import org.rifidi.edge.epcglobal.aleread.ALEDataTypes;
import org.rifidi.edge.epcglobal.aleread.filters.ALEField;

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

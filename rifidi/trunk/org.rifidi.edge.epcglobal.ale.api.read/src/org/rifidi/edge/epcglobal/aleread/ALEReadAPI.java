/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jochen Mader - jochen@pramari.com
 *
 */
public class ALEReadAPI {
	public static final String EC_TIME_UNIT_MS = "MS";
	public static final Map<String, ALEFields> aleIdToEnum;
	static{
		Map<String, ALEFields> fields=new HashMap<String, ALEFields>();
		fields.put("epc", ALEFields.EPC);
		fields.put("accessPwd", ALEFields.ACCESSPWD);
		fields.put("killPwd", ALEFields.KILLPWD);
		fields.put("afi", ALEFields.AFI);
		fields.put("epcBank", ALEFields.EPCBANK);
		fields.put("nsi", ALEFields.NSI);
		fields.put("tidBank", ALEFields.TIDBANK);
		fields.put("userBank", ALEFields.USERBANK);
		aleIdToEnum=Collections.unmodifiableMap(fields);
	}
}

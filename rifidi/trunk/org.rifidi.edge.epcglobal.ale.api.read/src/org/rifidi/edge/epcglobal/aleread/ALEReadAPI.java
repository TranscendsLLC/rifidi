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
	public static final Map<TriggerCondition, String> conditionToName;

	public enum TriggerCondition {
		DATA_AVAILABLE, STABLE_SET, STOP_TRIGGER, DURATION, DESTROIED, UNREQUEST, UNDEFINE, REQUESTED, REPEAT_PERIOD, TRIGGER
	};

	static {
		Map<String, ALEFields> fields = new HashMap<String, ALEFields>();
		fields.put("epc", ALEFields.EPC);
		fields.put("accessPwd", ALEFields.ACCESSPWD);
		fields.put("killPwd", ALEFields.KILLPWD);
		fields.put("afi", ALEFields.AFI);
		fields.put("epcBank", ALEFields.EPCBANK);
		fields.put("nsi", ALEFields.NSI);
		fields.put("tidBank", ALEFields.TIDBANK);
		fields.put("userBank", ALEFields.USERBANK);
		aleIdToEnum = Collections.unmodifiableMap(fields);

		Map<TriggerCondition, String> conditions = new HashMap<TriggerCondition, String>();
		conditions.put(TriggerCondition.STOP_TRIGGER, "TRIGGER");
		conditions.put(TriggerCondition.DURATION, "DURATION");
		conditions.put(TriggerCondition.STABLE_SET, "STABLE_SET");
		conditions.put(TriggerCondition.DATA_AVAILABLE, "DATA_AVAILABLE");
		conditions.put(TriggerCondition.UNREQUEST, "UNREQUEST");
		conditions.put(TriggerCondition.UNDEFINE, "UNDEFINE");
		conditions.put(TriggerCondition.REQUESTED, "REQUESTED");
		conditions.put(TriggerCondition.REPEAT_PERIOD, "REPEAT_PERIOD");
		conditions.put(TriggerCondition.TRIGGER, "TRIGGER");
		conditionToName = Collections.unmodifiableMap(conditions);
	}
}

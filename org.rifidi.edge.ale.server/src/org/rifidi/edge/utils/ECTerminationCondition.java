package org.rifidi.edge.utils;


/**
 * for backwards compatibility to provide the ECTerminationConditions.
 * @author swieland
 *
 */
public class ECTerminationCondition {
	/** termination by trigger. */
	public static final String TRIGGER = "TRIGGER";
	
	/** termination by the duration. */
	public static final String DURATION = "DURATION";
	
	/** the stable set. */
	public static final String STABLE_SET = "STABLE_SET";
	
	/** data is available. */
	public static final String DATA_AVAILABLE = "DATA_AVAILABLE";
	
	/** termination by unrequest. */
	public static final String UNREQUEST = "UNREQUEST";
	
	/** termination by undefine. */
	public static final String UNDEFINE = "UNDEFINE";
}
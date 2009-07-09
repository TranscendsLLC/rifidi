package org.rifidi.edge.core.sensors;

import java.util.Set;

public interface CompositeSensor {

	/**
	 * Get the names of child sensors this sensor has.
	 * 
	 * @return
	 */
	Set<String> getChildren();

}
/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.sensors;

import java.util.Set;

public interface CompositeSensor {

	/**
	 * Get the names of child sensors this sensor has.
	 * 
	 * @return
	 */
	Set<String> getChildren();

}

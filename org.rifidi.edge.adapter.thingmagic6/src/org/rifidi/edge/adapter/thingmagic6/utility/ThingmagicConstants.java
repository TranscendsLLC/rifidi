/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.thingmagic6.utility;

import java.util.Arrays;
import java.util.HashSet;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicConstants {
	public static final String PORT = "tmr:///dev/ttyACM0";

	public static final String RECONNECTION_INTERVAL = "2500";

	public static final String MAX_CONNECTION_ATTEMPTS = "-1";

	public static final HashSet<Integer> VALID_ANTENNAS = new HashSet<Integer>(
			Arrays.asList(1, 2, 3, 4));

	public static final String ANTENNAS = "1,2,3,4";

	public static final String UPGRADE_FIRMWARE = "0";
}

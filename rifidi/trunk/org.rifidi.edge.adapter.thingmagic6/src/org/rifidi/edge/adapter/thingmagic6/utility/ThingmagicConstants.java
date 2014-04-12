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

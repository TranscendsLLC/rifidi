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
package org.rifidi.edge.adapter.llrp;

/**
 * Constants class for the LLRP plugin.
 * 
 * @author Matthew Dean
 */
public class LLRPConstants {
	/**
	 * Default IP for the reader.
	 */
	public final static String LOCALHOST = "127.0.0.1";

	/**
	 * Default port for the reader.
	 */
	public final static String PORT = "5084";

	/**
	 * Default port for the reader.
	 */
	public final static String PORT_MAX = "65535";

	/**
	 * Default port for the reader.
	 */
	public final static String PORT_MIN = "0";

	/**
	 * THe interval between reconnect attempts.
	 */
	public final static String RECONNECTION_INTERVAL = "500";

	/**
	 * The max times to try to connect before giving up.
	 */
	public final static String MAX_CONNECTION_ATTEMPTS = "-1";

	/** The default path to the SET_READER_CONFIG XML */
	public final static String SET_READER_CONFIG_PATH = "config/SET_READER_CONFIG.llrp";

}

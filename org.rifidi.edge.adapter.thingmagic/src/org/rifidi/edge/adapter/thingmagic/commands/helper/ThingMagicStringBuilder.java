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
package org.rifidi.edge.adapter.thingmagic.commands.helper;

/**
 * This is a class that builds the command string for the ThingMagic poll
 * command.
 * 
 * @author Matthew Dean
 */
public class ThingMagicStringBuilder {

	// String temp =
	// "select id,read_count from tag_id WHERE (protocol_id='EPC0' "
	// + "or protocol_id='EPC1' or protocol_id='GEN2') set time_out=200;";

	private static final String READ_ID = "SELECT id, antenna_id FROM tag_id";

	private static final String SPACE = " ";

	private static final String SINGLE_QUOTE = "\'";

	private static final String PROT_START = "WHERE (";

	private static final String PROT_ID = "protocol_id=";

	private static final String OR = " or ";

	private static final String EPC0 = "EPC0";

	private static final String EPC1 = "EPC1";

	private static final String GEN2 = "GEN2";

	private static final String ISO86k6b = "ISO 18000-6B";

	private static final String END_PAREN = ")";

	private static final String TIMEOUT = "set time_out=";

	private static final String END = ";";

	public static String getCommandString(int timeout, boolean epc0,
			boolean epc1, boolean gen2, boolean iso860006b) {
		StringBuilder commandString = new StringBuilder();

		commandString.append(READ_ID);
		if (epc0 || epc1 || gen2 || iso860006b) {
			commandString.append(SPACE);
			commandString.append(PROT_START);
			if (epc0) {
				commandString.append(PROT_ID);
				commandString.append(SINGLE_QUOTE);
				commandString.append(EPC0);
				commandString.append(SINGLE_QUOTE);
				if (epc1 || gen2 || iso860006b) {
					commandString.append(OR);
				}
			}

			if (epc1) {
				commandString.append(PROT_ID);
				commandString.append(SINGLE_QUOTE);
				commandString.append(EPC1);
				commandString.append(SINGLE_QUOTE);
				if (gen2 || iso860006b) {
					commandString.append(OR);
				}
			}
			if (gen2) {
				commandString.append(PROT_ID);
				commandString.append(SINGLE_QUOTE);
				commandString.append(GEN2);
				commandString.append(SINGLE_QUOTE);
				if (iso860006b) {
					commandString.append(OR);
				}
			}
			if (iso860006b) {
				commandString.append(PROT_ID);
				commandString.append(SINGLE_QUOTE);
				commandString.append(ISO86k6b);
				commandString.append(SINGLE_QUOTE);
			}
			commandString.append(END_PAREN);
		}

		commandString.append(SPACE);
		commandString.append(TIMEOUT);
		commandString.append(timeout);
		commandString.append(END);

		return commandString.toString();
	}

}

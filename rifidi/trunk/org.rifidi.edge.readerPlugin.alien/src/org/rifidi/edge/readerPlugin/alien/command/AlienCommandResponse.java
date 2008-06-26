/* 
 * AlienCommand.java
 *  Created:	Jun 26, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.alien.command;

/**
 * A class representing a response from an Alien reader. This class figures out
 * which kind of response it was and stores it.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
@Deprecated
public class AlienCommandResponse {
	/**
	 * Enum that tells what type the message is.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	public enum AlienMessageType {
		TAG_TYPE, VERSION_TYPE, WELCOME_TYPE, INVALID_TYPE, OTHER_TYPE;
	}

	/**
	 * The command.
	 */
	private String command;

	/**
	 * The type of response it is.
	 */
	private AlienMessageType type;

	/**
	 * 
	 * 
	 * @param command
	 */
	public AlienCommandResponse(String command) {
		this.command = command;
		this.determineType(command);
	}

	/**
	 * Returns the type of command that it is.
	 * 
	 * @return TAG_TYPE if it is a tag command, VERSION_TYPE if it is a version
	 *         command, and OTHER_TYPE if it is anything else
	 */
	public AlienMessageType getType() {
		return type;
	}

	/**
	 * Private message that determines what type the command is.
	 * 
	 * @param comm
	 */
	private void determineType(String comm) {
		if (comm.contains(AlienCommandResponses.TAG_RESPONSE)
				&& !comm.contains(AlienCommandResponses.TAG_LIST_CUSTOM)) {
			type = AlienMessageType.TAG_TYPE;
		} else if (comm.contains(AlienCommandResponses.VERSION_RESPONSE)) {
			type = AlienMessageType.VERSION_TYPE;
		} else if (comm.contains(AlienCommandResponses.WELCOME)) {
			type = AlienMessageType.WELCOME_TYPE;
		} else if (comm.contains(AlienCommandResponses.INVALID)) {
			type = AlienMessageType.INVALID_TYPE;
		} else {
			type = AlienMessageType.OTHER_TYPE;
		}
	}

	/**
	 * Strings that will show up in responses to Alien commands.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class AlienCommandResponses {
		/**
		 * A string that will appear in response to a version command.
		 */
		public static final String VERSION_RESPONSE = "ReaderVersion";

		/**
		 * A string that will appear in response to a tag command.
		 */
		public static final String TAG_RESPONSE = "|";

		/**
		 * 
		 */
		public static final String TAG_LIST_CUSTOM = "Custom";

		/**
		 * A string that will appear in an invalid username/password combo.
		 */
		public static final String INVALID = "Invalid";

		/**
		 * A string taht will appear in the welcome screen.
		 */
		public static final String WELCOME = "Alien";
	}

	/**
	 * Returns the command response.
	 * 
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
}

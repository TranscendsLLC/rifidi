/*
 *  CommandConfiguration.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.api.readerplugin.commands;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Command Configuration is an object that holds the necessary information to
 * start a command (either a command proper or a property command). It can be
 * transmitted over RMI to and from the client
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CommandConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * An optional set of arguments for this client
	 */
	private Set<CommandArgument> _arguments;

	/**
	 * The command name
	 */
	private String _commandName;

	public CommandConfiguration(String commandName,
			Set<CommandArgument> arguments) {
		_arguments = arguments;
		_commandName = commandName;
	}

	/**
	 * 
	 * @return The name of this command
	 */
	public String getCommandName() {
		return _commandName;
	}

	/**
	 * 
	 * @param argName
	 *            The name of the command argument
	 * @return The value of the command argument
	 */
	public String getArgValue(String argName) {
		for (CommandArgument ca : _arguments) {
			if (ca.getName().equalsIgnoreCase(argName)) {
				return ca.getValue();
			}
		}
		return null;
	}

	/**
	 * 
	 * @return A set contatining the names of all the arguments
	 */
	public Set<String> getArgNames() {
		Set<String> keys = new HashSet<String>();
		for (CommandArgument ca : _arguments) {
			keys.add(ca.getName());
		}
		return keys;
	}

	/**
	 * It may be possible for an error to be associated with an argument when it
	 * is executing.
	 * 
	 * @param argName
	 *            The name of the argument
	 * @return True if the argument has an error
	 */
	public boolean hasError(String argName) {
		for (CommandArgument ca : _arguments) {
			if (ca.getName().equalsIgnoreCase(argName)) {
				return ca.hasError();
			}
		}
		return false;
	}
}

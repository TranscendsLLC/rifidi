/*
 *  AlienResponse.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.readerplugin.alien.properties;

import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.api.readerplugin.commands.CommandArgument;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienResponse {

	String response = null;
	boolean error = false;

	public void setResponseMessage(String response) {
		if (response == null) {
			error = true;
			this.response = "Error";
		} else {
			if (response.contains("=")) {
				String[] temp = response.split("=");
				this.response = temp[1].trim();

			} else {
				error = true;
				if (response.startsWith("Error")) {
					this.response = response.trim();
				} else {
					this.response = "Error";
				}
			}
		}
	}

	public CommandConfiguration formulateResponse(String propertyName,
			String formElementName) {
		CommandArgument formElement = new CommandArgument(formElementName, response, error);
		Set<CommandArgument> commandArguments = new HashSet<CommandArgument>();
		commandArguments.add(formElement);
		return new CommandConfiguration(propertyName, commandArguments);
	}

}

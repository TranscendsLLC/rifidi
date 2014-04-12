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
package org.rifidi.edge.tools.diagnostics;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * The CommandProvider for the TagGenerator app.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagGeneratorCommandProvider implements CommandProvider {

	private TagGeneratorApp generatorApp;

	public Object _startTagRunner(CommandInterpreter intp) {
		String tagSetID = intp.nextArgument();
		String exposureID = intp.nextArgument();
		generatorApp.startRunner(tagSetID, exposureID);
		return null;
	}

	/**
	 * @param generatorApp
	 *            the generatorApp to set
	 */
	public void setGeneratorApp(TagGeneratorApp generatorApp) {
		this.generatorApp = generatorApp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		StringBuffer retVal = new StringBuffer();
		retVal.append("  ---Diagnostic Tag Generator App Commands---\n");
		retVal.append( "\tstartTagRunner <tagSetID> <exposureID> - starts a given tag runner based "
				+ "on the tag and exposure property files in "
				+ "the Diagnostic data folder\n");
		return retVal.toString();
	}

}

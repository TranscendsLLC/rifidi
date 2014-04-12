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
import org.rifidi.edge.notification.TagReadEvent;

/**
 * A command provider for the TagApp
 * 
 * @author Kyle Neumeier-kyle@pramari.com
 * 
 */
public class TagAppCommandProvider implements CommandProvider {

	private TagApp tagApp;

	public Object _recenttags(CommandInterpreter intp) {
		String readerID = intp.nextArgument();
		if (readerID == null || readerID.isEmpty()) {
			intp.println("Usage: recenttags <readerID>");
			return null;
		}
		for (TagReadEvent tag : tagApp.getRecentTags(readerID)) {
			intp.println(tag);
		}
		return null;
	}

	public Object _currenttags(CommandInterpreter intp) {
		String readerID = intp.nextArgument();
		if (readerID == null || readerID.isEmpty()) {
			intp.println("Usage: currenttags <readerID>");
			return null;
		}
		for (TagReadEvent tag : tagApp.getCurrentTags(readerID)) {
			intp.println(tag);
		}
		return null;
	}

	public Object _tagrate(CommandInterpreter intp) {
		String seconds = intp.nextArgument();
		if (seconds == null || seconds.isEmpty()) {
			seconds = "5";
		}
		tagApp.measureReadRate(seconds);
		return null;
	}

	/**
	 * @param tagApp
	 *            the tagApp to set
	 */
	public void setTagApp(TagApp tagApp) {
		this.tagApp = tagApp;
	}

	@Override
	public String getHelp() {
		StringBuffer retVal = new StringBuffer();
		retVal.append("  ---Diagnostic Tag App Commands---\n");
		retVal.append("\trecenttags <readerID> - Prints out a list of "
				+ "tags recently seen on the given reader.  \n");
		retVal.append("\tcurrenttags <readerID> - Prints out a list of tags"
				+ "currently seen by the given reader.  \n");
		retVal.append("\ttagrate [time] - Measures the number tags seen "
				+ "within the given number of seconds. If no argument is "
				+ "supplied, will default to 5 seconds  \n");
		return retVal.toString();
	}

}

package org.rifidi.edge.app.diag.tags;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

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

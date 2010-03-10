package org.rifidi.edge.app.diag.tags;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

public class TagAppCommandProvider implements CommandProvider {

	private TagApp tagApp;

	public Object _recenttags(CommandInterpreter intp) {
		String readerID = intp.nextArgument();
		if (readerID == null || readerID.isEmpty()) {
			intp.println("Usage: recenttags <readerID>");
			return null;
		}
		for (TagData tag : tagApp.getRecentTags(readerID)) {
			intp.println(tag);
		}
		return null;
	}
	
	public Object _currenttags(CommandInterpreter intp) {
		String readerID = intp.nextArgument();
		if (readerID == null || readerID.isEmpty()) {
			intp.println("Usage: recenttags <readerID>");
			return null;
		}
		for (TagData tag : tagApp.getCurrentTags(readerID)) {
			intp.println(tag);
		}
		return null;
	}
	
	

	/**
	 * @param tagApp the tagApp to set
	 */
	public void setTagApp(TagApp tagApp) {
		this.tagApp = tagApp;
	}



	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}

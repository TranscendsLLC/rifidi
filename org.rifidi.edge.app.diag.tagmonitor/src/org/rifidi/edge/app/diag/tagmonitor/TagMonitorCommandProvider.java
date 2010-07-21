/**
 * 
 */
package org.rifidi.edge.app.diag.tagmonitor;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class TagMonitorCommandProvider implements CommandProvider {

	private TagMonitorApp tagMonitorApp;

	/**
	 * Called by spring
	 * 
	 * @param tagMonitorApp
	 */
	public void setTagMonitorApp(TagMonitorApp tagMonitorApp) {
		this.tagMonitorApp = tagMonitorApp;
	}

	/**
	 * @param intp
	 * @return
	 */
	public Object _tagmonitor(CommandInterpreter intp) {
		String readerID = intp.nextArgument();
		if (readerID == null || readerID.isEmpty()) {
			intp.println("Usage: tagmonitor <readerID>");
			return null;
		}
		Map<BigInteger, Integer> tagmap = tagMonitorApp
				.startTagMonitor(readerID);
		Set<BigInteger> taglist = tagmap.keySet();

		for (BigInteger tag : taglist) {
			intp.println("The tag " + tag + " was seen " + tagmap.get(tag)
					+ " times.");
		}
		intp.println(tagmap.size() + " tags were seen in total.");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		StringBuffer retVal = new StringBuffer();
		retVal.append("  ---Diagnostic Tag Monitor Commands---\n");
		retVal.append("\ttagmonitor <readerID> - Prints out a list of "
				+ "tags seen on the reader, along with the number of ti.  \n");
		return retVal.toString();
	}

}

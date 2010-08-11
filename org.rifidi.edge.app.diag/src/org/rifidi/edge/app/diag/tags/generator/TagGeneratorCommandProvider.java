/**
 * 
 */
package org.rifidi.edge.app.diag.tags.generator;

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

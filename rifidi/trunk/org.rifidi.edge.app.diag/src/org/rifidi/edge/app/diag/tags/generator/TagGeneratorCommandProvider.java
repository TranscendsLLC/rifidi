/**
 * 
 */
package org.rifidi.edge.app.diag.tags.generator;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Owner
 *
 */
public class TagGeneratorCommandProvider implements CommandProvider {

	private TagGeneratorApp generatorApp;
	
	public Object _generate(CommandInterpreter intp){
		generatorApp.generate("123456", "LLRP_1", 100);
		return null;
	}
	
	/**
	 * @param generatorApp the generatorApp to set
	 */
	public void setGeneratorApp(TagGeneratorApp generatorApp) {
		this.generatorApp = generatorApp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		return "";
	}

}

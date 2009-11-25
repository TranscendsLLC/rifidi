/**
 * 
 */
package org.rifidi.edge.app.db.commands;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.app.db.DBApp;

/**
 * @author kyle
 * 
 */
public class DBAppCommandProvider implements CommandProvider {

	private volatile DBApp app;

	public Object _dbstart(CommandInterpreter intp) {
		app.start();
		return null;
	}

	public Object _dbstop(CommandInterpreter intp) {
		app.stop();
		return null;
	}

	/**
	 * Called by spring
	 * 
	 * @param app
	 *            the app to set
	 */
	public void setApp(DBApp app) {
		this.app = app;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}

/**
 * 
 */
package org.rifidi.edge.app.db.commands;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.app.db.DBApp;

/**
 * This class provides commands to the eclipse OSGi console, so that a user can
 * control the DBApp.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class DBAppCommandProvider implements CommandProvider {

	/** A reference to the application */
	private volatile DBApp app;

	/**
	 * Start the application
	 * 
	 * @param intp
	 * @return
	 */
	public Object _dbstart(CommandInterpreter intp) {
		app.start();
		return null;
	}

	/**
	 * Stop the application
	 * 
	 * @param intp
	 * @return
	 */
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

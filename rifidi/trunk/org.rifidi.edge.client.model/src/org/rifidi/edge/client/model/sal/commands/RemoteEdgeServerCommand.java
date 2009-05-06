package org.rifidi.edge.client.model.sal.commands;
//TODO: Comments
/**
 * An interface that should be implemented by commands that are executed by the
 * RequestExecutor. The execute command should do work that should be done
 * outside an eclipse thread, while the executeEclipse method should do work
 * that must be done in the eclipse thread. The execute() method will finish
 * before the executeEclipse() method is invoked
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface RemoteEdgeServerCommand {

	/**
	 * This method should do work that does not have to happen in the eclipse
	 * thread (such as making an RMI call)
	 */
	public abstract void execute();

	/**
	 * This method should do work that must happen in the eclipse thread, such
	 * as inserting objects into an observable list or firing a property change
	 * event
	 */
	public abstract void executeEclipse();

	/**
	 * 
	 * @return the name of the command that is executed
	 */
	public abstract String getType();

}

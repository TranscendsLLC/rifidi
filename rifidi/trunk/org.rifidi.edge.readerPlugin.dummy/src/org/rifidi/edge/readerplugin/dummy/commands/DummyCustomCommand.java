package org.rifidi.edge.readerplugin.dummy.commands;

import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class DummyCustomCommand implements ICustomCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6543542716375722430L;
	
	private String command;

	public DummyCustomCommand(String c){
		command = c;
	}
	
	public DummyCustomCommand(){
		
	}
	
	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	

}

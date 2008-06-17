package org.rifidi.edge.readerPlugin.alien.command;

import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;

public class AlienCustomCommandResult implements ICustomCommandResult {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8320598992143116635L;
	
	private String result;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
}

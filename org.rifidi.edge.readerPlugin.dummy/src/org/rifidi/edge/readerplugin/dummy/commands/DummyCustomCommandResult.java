package org.rifidi.edge.readerplugin.dummy.commands;

import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;

public class DummyCustomCommandResult implements ICustomCommandResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3013789814901335245L;
	
	private String result;

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

}

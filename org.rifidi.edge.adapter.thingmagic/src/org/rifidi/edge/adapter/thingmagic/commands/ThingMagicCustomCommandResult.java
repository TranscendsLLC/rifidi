package org.rifidi.edge.adapter.thingmagic.commands;

import org.rifidi.edge.core.readerAdapter.commands.ICustomCommandResult;

public class ThingMagicCustomCommandResult implements ICustomCommandResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2905063122064512338L;
	
	String result;

	public ThingMagicCustomCommandResult(String result){
		setResult(result);
	}
	
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

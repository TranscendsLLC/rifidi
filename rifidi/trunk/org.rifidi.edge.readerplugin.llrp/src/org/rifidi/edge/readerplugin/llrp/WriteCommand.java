/**
 * 
 */
package org.rifidi.edge.readerplugin.llrp;

import java.io.InputStream;
import java.util.concurrent.TimeoutException;

import org.rifidi.edge.core.sensors.commands.TimeoutCommand;
import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;

/**
 * @author kyle
 *
 */
public class WriteCommand extends TimeoutCommand {

	private CannotExecuteException exception;
	
	/**
	 * @param commandID
	 */
	public WriteCommand(String commandID) {
		super(commandID);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.commands.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		InputStream stream = WriteCommand.class.getResourceAsStream("LLRPMessageTemplate/add_accessspec.llrp");
		//DELETE ALL ROSPECS 
		//DELETE ALL ACCESS SPECS
		//FORM ACCESS SPEC, SEND, & ENABLE
		//FORM ROSPEC, SEND, ENABLE, START

	}
	
	public CannotExecuteException getException(){
		return exception;
	}

}

/**
 * 
 */
package org.rifidi.edge.readerplugin.llrp;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.llrp.ltk.generated.parameters.LLRPStatus;
import org.rifidi.edge.core.sensors.commands.TimeoutCommand;
import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;
import org.rifidi.edge.core.sensors.management.AbstractTagWritingService;

/**
 * @author kyle
 *
 */
public class LLRPTagWritingService extends AbstractTagWritingService<LLRPReaderSession> {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.management.AbstractTagWritingService#writeEPC(java.lang.String, int, byte[])
	 */
	@Override
	public void writeEPC(String readerID, int antenna, byte[] data)
			throws CannotExecuteException {
		WriteCommand command = new WriteCommand("Write Command ID");
		super.getSession(readerID).submitAndBlock(command, 5000, TimeUnit.MILLISECONDS);
		if(command.getException()!=null){
			throw command.getException();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.management.AbstractTagWritingService#writeUser(java.lang.String, int, byte[])
	 */
	@Override
	public void writeUser(String readerID, int antenna, byte[] data)
			throws CannotExecuteException {
		WriteCommand command = new WriteCommand("Write Command ID");
		super.getSession(readerID).submitAndBlock(command, 5000, TimeUnit.MILLISECONDS);
		if(command.getException()!=null){
			throw command.getException();
		}
		
	}
	
}

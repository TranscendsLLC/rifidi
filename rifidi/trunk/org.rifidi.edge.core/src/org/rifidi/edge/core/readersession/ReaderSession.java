package org.rifidi.edge.core.readersession;

import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.impl.enums.ReaderSessionStatus;

public interface ReaderSession {

	public ReaderInfo getReaderInfo();

	//TODO: Need a way to tell what exceptions cause a restart and ones that do not.
	public long executeCommand(String command) throws RifidiConnectionException, RifidiCommandInterruptedException;

	public void stopCommand();
	
	public String getQueueName();

	public ReaderSessionStatus getStatus();
}

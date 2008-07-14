package org.rifidi.edge.core.readersession;

import java.util.List;

import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.impl.enums.CommandStatus;
import org.rifidi.edge.core.readersession.impl.enums.ReaderSessionStatus;

public interface ReaderSession {

	public ReaderSessionStatus getStatus();
	
	public List<String> getAvailableCommands();
	
	public List<String> getAvailableCommands(String groupName);
	
	public List<String> getAvailableCommandGroups();
	
	//TODO: Need a way to tell what exceptions cause a restart and ones that do not.
	public long executeCommand(String command, String configuration) throws RifidiConnectionException, RifidiCommandInterruptedException;
	
	public boolean stopCurCommand(boolean force);
	
	public boolean stopCurCommand(boolean force, long commandID);
	
	public String curExecutingCommand();
	
	public long curExecutingCommandID();
	
	public String commandStatus(long id);
	
	public CommandStatus commandStatus();
	
	public String getMessageQueueName();
	
	public ReaderInfo getReaderInfo();
	
	public void resetReaderSession();
}

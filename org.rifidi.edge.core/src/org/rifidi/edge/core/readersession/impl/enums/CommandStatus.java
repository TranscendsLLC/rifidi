package org.rifidi.edge.core.readersession.impl.enums;

import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;

//TODO: This is a bit confusingly named, I got it mixed up with "CommandReturnStatus",
 
public enum CommandStatus {
	NOCOMMAND, WAITING, EXECUTING, SUCCESSFUL, UNSUCCESSFUL, INTERRUPTED;
	
	public static CommandStatus toCommandStatus(CommandReturnStatus crs){
		switch(crs){
		case INTERRUPTED: return CommandStatus.INTERRUPTED;
		case SUCCESSFUL : return CommandStatus.SUCCESSFUL;
		case UNSUCCESSFUL: return CommandStatus.UNSUCCESSFUL;
		default: return null;
		}
		
	}
}

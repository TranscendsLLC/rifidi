/**
 * 
 */
package org.rifidi.edge.core.readersession.impl;

import java.util.ArrayList;

import org.rifidi.edge.core.readersession.impl.enums.CommandStatus;

/**
 * @author kyle
 *
 */
public class CommandJournal {
	
	ArrayList<CommandWrapper> journal = new ArrayList<CommandWrapper>();
	
	public CommandStatus getCommandStatus(long commandID){
		for(CommandWrapper cw : journal){
			if(cw.getCommandID()==commandID){
				return cw.getCommandStatus();
			}
		}
		return null;
	}

}

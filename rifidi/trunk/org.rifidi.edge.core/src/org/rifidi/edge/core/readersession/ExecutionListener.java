package org.rifidi.edge.core.readersession;

import org.rifidi.edge.core.readerplugin.commands.Command;

public interface ExecutionListener {
	
	public void executionStopped(Command command);

	public void executionStarted(Command command);
}

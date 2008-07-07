package org.rifidi.edge.core.readersession;

import org.rifidi.edge.core.communication.service.RifidiConnectionException;

public interface ReaderSession {

	public void executeCommand(String command) throws RifidiConnectionException;

	public void stopCommand();

	public ReaderSessionStatus getStatus();

	public void addExecutionListener(ExecutionListener listener);

	public void removeExecutionListener(ExecutionListener listener);
}

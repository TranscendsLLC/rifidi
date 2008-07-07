package org.rifidi.edge.core.readersession;

import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;

public interface ReaderSession {

	public ReaderInfo getReaderInfo();

	public void executeCommand(String command) throws RifidiConnectionException;

	public void stopCommand();

	public ReaderSessionStatus getStatus();

	public void addExecutionListener(ExecutionListener listener);

	public void removeExecutionListener(ExecutionListener listener);
}

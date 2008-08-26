package org.rifidi.edge.core.readersession.impl;

import org.rifidi.edge.core.communication.service.CommunicationStateListener;
import org.rifidi.edge.core.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.impl.enums.ReaderSessionStatus;
import org.w3c.dom.Document;

public interface ReaderSessionState extends CommunicationStateListener{

	public long state_executeCommand(Document configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException,
			RifidiInvalidConfigurationException;

	public Document state_executeProperty(Document propertiesToExecute)throws RifidiConnectionException, RifidiCommandNotFoundException,
	RifidiCommandInterruptedException,
			RifidiInvalidConfigurationException,
			RifidiCannotRestartCommandException;

	public void state_resetSession();
	
	public void state_stopCommand(boolean force);

	public void state_commandFinished();

	public void state_propertyFinished() throws RifidiCannotRestartCommandException;

	public void state_error();
	
	/**
	 * Get the current status of the ReaderSession
	 * 
	 * @return the status of the ReaderSession
	 */
	public ReaderSessionStatus state_getStatus();
	
	public void state_enable();
	
	public void state_disable();
	
	public boolean state_setReaderInfo(ReaderInfo readerInfo);

}

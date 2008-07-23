package org.rifidi.edge.core.readersession.impl;

import org.rifidi.edge.core.communication.service.CommunicationStateListener;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.exceptions.RifidiInvalidConfigurationException;
import org.w3c.dom.Document;

public interface ReaderSessionState extends CommunicationStateListener{

	public long state_executeCommand(Document configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException,
			RifidiInvalidConfigurationException;

	public Document state_executeProperty(Document propertiesToExecute)throws RifidiConnectionException, RifidiCommandNotFoundException,
	RifidiCommandInterruptedException, RifidiInvalidConfigurationException;

	public void state_resetSession();
	
	public void state_stopCommand(boolean force);

	public void state_commandFinished();

	public void state_propertyFinished();

	public void state_error();

}

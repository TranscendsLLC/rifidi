package org.rifidi.edge.core.readersession.impl;

import org.rifidi.edge.core.api.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.api.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.api.readerplugin.ReaderInfo;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.property.PropertyConfiguration;
import org.rifidi.edge.core.api.readersession.enums.ReaderSessionStatus;
import org.rifidi.edge.core.communication.service.CommunicationStateListener;

public interface ReaderSessionState extends CommunicationStateListener {

	public long state_executeCommand(CommandConfiguration configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException;

	/**
	 * 
	 * @param propertiesToExecute
	 * @param set
	 *            if true, set the property. If false, get the value of the
	 *            property
	 * @return
	 * @throws RifidiConnectionException
	 * @throws RifidiCommandNotFoundException
	 * @throws RifidiCommandInterruptedException
	 * @throws RifidiInvalidConfigurationException
	 * @throws RifidiCannotRestartCommandException
	 */
	public PropertyConfiguration state_executeProperty(PropertyConfiguration propertiesToExecute,
			boolean set) throws RifidiConnectionException,
			RifidiCommandNotFoundException, RifidiCommandInterruptedException,
			RifidiCannotRestartCommandException, RifidiInvalidConfigurationException;

	public void state_resetSession();

	public void state_stopCommand(boolean force);

	public void state_commandFinished();

	public void state_propertyFinished()
			throws RifidiCannotRestartCommandException;

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

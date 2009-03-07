/**
 * 
 */
package org.rifidi.edge.core.rmi.client.edgeserverstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.api.EdgeServerStub;
import org.rifidi.edge.core.exceptions.NonExistentCommandFactoryException;
import org.rifidi.edge.core.exceptions.NonExistentReaderConfigurationException;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call creates a new session with the supplied ReaderConfiguration ID and
 * CommandConfigurationID, and starts it.
 * 
 * makeCall() returns a String which is the ID of the newly created Session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ESStartReaderSession extends
		ServerDescriptionBasedRemoteMethodCall<String, RuntimeException> {

	/** The ReaderConfiguration that should be used with this session */
	private String readerConfigurationID;
	/** The CommandConfiguration that should be executed during this session */
	private String commandConfigurationID;

	public ESStartReaderSession(ESServerDescription serverDescription,
			String readerConfigurationID, String commandConfigurationID) {
		super(serverDescription);
		this.readerConfigurationID = readerConfigurationID;
		this.commandConfigurationID = commandConfigurationID;
	}

	@Override
	protected String performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		EdgeServerStub stub = (EdgeServerStub) remoteObject;
		try {
			return stub.startReaderSession(this.readerConfigurationID,
					this.commandConfigurationID);
		} catch (NonExistentCommandFactoryException e) {
			// TODO: Should change this to a real exception
			throw new RuntimeException(e);
		} catch (NonExistentReaderConfigurationException e) {
			// TODO: Should change this to a real exception
			throw new RuntimeException(e);
		}
	}

}

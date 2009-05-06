package org.rifidi.edge.client.model.sal;
//TODO: Comments
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.client.model.sal.commands.RequestExecuterSingleton;
import org.rifidi.edge.core.api.jms.notifications.ReaderAddedNotification;
import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_GetReader;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_ServerDescription;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * A command that is executed when a reader has been added
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Command_ReaderAdded implements RemoteEdgeServerCommand {

	/** The RMI server description */
	private RS_ServerDescription serverDescription;
	/** The ID of the server */
	private String id;
	/** The DTO of the reader */
	private ReaderDTO readerDTO;
	/** A command to execute if there is a problem */
	private Command_Disconnect disconnectCommand;
	/** The observable map of remote readers */
	private ObservableMap remoteReaders;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(Command_ReaderAdded.class);
	private ObservableMap remoteReaderFactories;

	/**
	 * Constructor
	 * 
	 * @param server
	 *            The server to connect to
	 * @param notification
	 *            The JMS notification message that was received
	 */
	public Command_ReaderAdded(RemoteEdgeServer server,
			ReaderAddedNotification notification) {
		this.serverDescription = server.getRSServerDescription();
		this.id = notification.getReaderID();
		this.disconnectCommand = new Command_Disconnect(server);
		this.remoteReaders = server.getRemoteReaders();
		this.remoteReaderFactories = server.readerFactories;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#execute
	 * ()
	 */
	@Override
	public void execute() {
		// Execute a GET_READER command over RMI
		RS_GetReader command = new RS_GetReader(serverDescription, id);
		try {
			readerDTO = command.makeCall();
		} catch (ServerUnavailable e) {
			RequestExecuterSingleton.getInstance().scheduleRequest(
					disconnectCommand);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#
	 * executeEclipse()
	 */
	@Override
	public void executeEclipse() {

		// add readerDTO to remote readers
		if (readerDTO != null) {
			RemoteReaderFactory factory = (RemoteReaderFactory) this.remoteReaderFactories
					.get(readerDTO.getReaderFactoryID());
			if (factory != null) {
				remoteReaders.put(id, new RemoteReader(readerDTO, factory));
			}
		} else {
			logger.warn("Cannot create a new Remote "
					+ "Reader because DTO is null");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#getType
	 * ()
	 */
	@Override
	public String getType() {
		return "READER_ADDED";
	}

}

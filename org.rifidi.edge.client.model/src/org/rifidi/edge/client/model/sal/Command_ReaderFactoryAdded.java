package org.rifidi.edge.client.model.sal;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.rifidi.edge.api.jms.notifications.ReaderFactoryAddedNotification;
import org.rifidi.edge.api.rmi.dto.ReaderFactoryDTO;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.client.model.sal.commands.RequestExecuterSingleton;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_GetReaderDescription;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_GetReaderFactory;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_ServerDescription;
import org.rifidi.rmi.proxycache.exceptions.AuthenticationException;
import org.rifidi.rmi.proxycache.exceptions.ServerUnavailable;

/**
 * A Command that is executed when a reader factory has been added
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class Command_ReaderFactoryAdded implements RemoteEdgeServerCommand {

	private static Log logger = LogFactory
			.getLog(Command_ReaderFactoryAdded.class);
	/** The DTO for the reader factory */
	private ReaderFactoryDTO dto;
	/** The description of the RMI stub */
	private RS_ServerDescription serverDescription;
	/** The ID of the factory that was added */
	private String readerFactoryID;
	/** The observable map of reader factories */
	private ObservableMap readerFactories;
	/** A command to execute in case of a problem */
	private Command_Disconnect disconnect;
	/** The MBean object that describes reader produced by this factory */
	private MBeanInfo info;

	/**
	 * Constructor
	 * 
	 * @param server
	 *            The RemoteEdgeServer
	 * @param notification
	 *            The JMS notification that was received
	 */
	public Command_ReaderFactoryAdded(RemoteEdgeServer server,
			ReaderFactoryAddedNotification notification) {
		serverDescription = server.getRSServerDescription();
		readerFactoryID = notification.getReaderFactoryID();
		readerFactories = server.getReaderFactories();
		disconnect = new Command_Disconnect(server);
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
		// a call to get the factory DTO
		RS_GetReaderFactory call = new RS_GetReaderFactory(serverDescription,
				readerFactoryID);
		// a call to get the MBean Object associated with this factory
		RS_GetReaderDescription descriptionCall = new RS_GetReaderDescription(
				serverDescription, readerFactoryID);
		try {
			dto = call.makeCall();
			info = descriptionCall.makeCall();
		} catch (ServerUnavailable e) {
			logger.error(e);
			RequestExecuterSingleton.getInstance().scheduleRequest(disconnect);
		} catch (AuthenticationException e) {
			logger.warn("Authentication Exception ", e);
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
		readerFactories.put(dto.getReaderFactoryID(), new RemoteReaderFactory(
				dto, info));

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
		return "READER_FACTORY_ADDED";
	}

}

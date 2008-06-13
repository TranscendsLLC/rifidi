package org.rifidi.edge.core.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.adapter.RifidiAdapterIllegalStateException;
import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommand;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommandResult;
import org.rifidi.edge.core.session.jms.JMSMessageThread;


/**
 * 
 * @author Jerry and Kyle
 * A session bundles the objects needed to communicate to the reader.
 */

public class Session implements ISession {
	
	private static final Log logger = LogFactory.getLog(Session.class);	
	
	private IReaderAdapter adapter;
	
	private int sessionID;
	
	private AbstractConnectionInfo connectionInfo;
	
	private JMSMessageThread jmsMessageThread;
	
	/**
	 * Creates a Session.
	 * @param connectionInfo Info used to connect to a reader.
	 * @param adapter Object that talks to a reader.
	 * @param id Id for this session.
	 */
	public Session(AbstractConnectionInfo connectionInfo, IReaderAdapter adapter, int id, JMSMessageThread jmsMessageThread ){
		setConnectionInfo( connectionInfo);
		setAdapter( adapter);
		setSessionID(id);
		this.jmsMessageThread = jmsMessageThread;
	}

	/**
	 * @return the adapter
	 */
	public IReaderAdapter getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(IReaderAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * @return the sessionID
	 */
	public int getSessionID() {
		return sessionID;
	}

	/**
	 * @param sessionID the sessionID to set
	 */
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 * @return the connectionInfo
	 */
	public AbstractConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

	/**
	 * @param connectionInfo the connectionInfo to set
	 */
	public void setConnectionInfo(AbstractConnectionInfo connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

	@Override
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand) {
		// TODO needs to be implemented and designed
		// TODO Handle exceptions here or send them up the call chain.
		try {
			return adapter.sendCustomCommand(customCommand);
		} catch (RifidiAdapterIllegalStateException e) {
			// TODO Deal with state changing
			logger.error("Adapter in Illegal State", e);
		} catch (RifidiIIllegialArgumentException e) {
			// TODO Deal with state changing
			logger.error("Illegal Argument Passed.", e);
		}
		
		return null;
	}

	@Override
	public void startTagStream() {
		this.jmsMessageThread.start();
		
	}

	@Override
	public void stopTagStream() {
		this.jmsMessageThread.stop();
		
	}

}

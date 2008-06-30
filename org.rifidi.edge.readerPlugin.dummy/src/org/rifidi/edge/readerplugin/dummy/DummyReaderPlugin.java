package org.rifidi.edge.readerplugin.dummy;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.thread.AbstractThread;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.service.communication.CommunicationService;
import org.rifidi.edge.core.service.communication.buffer.ConnectionBuffer;
import org.rifidi.edge.core.tag.TagRead;

import org.rifidi.edge.readerplugin.dummy.commands.DummyCustomCommand;
import org.rifidi.edge.readerplugin.dummy.commands.DummyCustomCommandResult;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class DummyReaderPlugin implements IReaderPlugin  {
	private static final Log logger = LogFactory.getLog(DummyReaderPlugin.class);
	
	public SocketLoopBack loopback;

	private boolean connected = false;

	private DummyReaderInfo info;
	
	/* used only when the dummy adapter is set to random errors */
	Random random;

	private CommunicationService communicationService;

	

	private ConnectionBuffer connectionBuffer;
	
	
	public DummyReaderPlugin(DummyReaderInfo info) {
		this.info = info;
		random = new Random();
		ServiceRegistry.getInstance().service(this);
		loopback = new SocketLoopBack("DummyAdapter LoopBack: " + info.getPort());
		loopback.start();
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPlugin.IReaderPlugin#connect(org.rifidi.edge.core.communication.buffer.ConnectionBuffer)
	 */
	@Override
	public void connect(ConnectionBuffer connectionBuffer) throws RifidiConnectionException {
		switch (info.getErrorToSet()) {
			case CONNECT:
				throw new RifidiConnectionException();
			case CONNECT_RUNTIME:
				throw new RuntimeException();
			case RANDOM:
				if (random.nextDouble() <= info.getRandomErrorProbibility()){
					if(random.nextDouble() <= info.getProbiblityOfErrorsBeingRuntimeExceptions()){
						throw new RuntimeException();
					} else {
						throw new RifidiConnectionException();
					}
				}
		}
		if (connectionBuffer == null )
			throw new RifidiConnectionException("No connection buffer found.");
		
		this.connectionBuffer = connectionBuffer;
		

		logger.debug("Successfully Connected.");
		
		
		
		connected = true;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPlugin.IReaderPlugin#disconnect()
	 */
	@Override
	public void disconnect() throws RifidiConnectionException {
		connected = false;
		switch (info.getErrorToSet()) {
			case DISCONNECT:
				throw new RifidiConnectionException();
			case DISCONNECT_RUNTIME:
				throw new RuntimeException();
			case RANDOM:
				if (random.nextDouble() <= info.getRandomErrorProbibility()){
					if(random.nextDouble() <= info.getProbiblityOfErrorsBeingRuntimeExceptions()){
						throw new RuntimeException();
					} else {
						throw new RifidiConnectionException();
					}
				}
		}
		
		
		if (communicationService == null)
			throw new RifidiConnectionException("CommunicationSerivce Not Found!");
		
		try {
			communicationService.destroyConnection(connectionBuffer);
		} catch (IOException e){
			logger.debug("IOException.", e);
			throw new RifidiConnectionException(e);
		}
		logger.debug("Successfully Disconnected.");
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPlugin.IReaderPlugin#getNextTags()
	 */
	@Override
	public List<TagRead> getNextTags()
			throws RifidiConnectionIllegalStateException {
		switch (info.getErrorToSet()) {
			case GET_NEXT_TAGS:
				throw new RifidiConnectionIllegalStateException();
			case GET_NEXT_TAGS_RUNTIME:
				throw new RuntimeException();
			case RANDOM:
				if (random.nextDouble() <= info.getRandomErrorProbibility()){
					if(random.nextDouble() <= info.getProbiblityOfErrorsBeingRuntimeExceptions()){
						throw new RuntimeException();
					} else {
						throw new RifidiConnectionIllegalStateException();
					}
				}
		}

		if (connected) {
			TagRead tr = new TagRead();
			byte[] b = { 0x01, 0x02, 0x03, 0x04 };
			tr.setId(b);
			tr.setLastSeenTime(System.currentTimeMillis());
			ArrayList<TagRead> reads = new ArrayList<TagRead>();
			reads.add(tr);
			return reads;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPlugin.IReaderPlugin#sendCustomCommand(org.rifidi.edge.core.readerPlugin.commands.ICustomCommand)
	 */
	@Override
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand)
			throws RifidiConnectionIllegalStateException,
			RifidiIIllegialArgumentException {
		switch (info.getErrorToSet()) {
			case SEND_CUSTOM_COMMAND:
				throw new RifidiConnectionIllegalStateException();
			case SEND_CUSTOM_COMMAND2:
				throw new RifidiIIllegialArgumentException();
			case SEND_CUSTOM_COMMAND_RUNTIME:
				throw new RuntimeException();
			case RANDOM:
				if (random.nextDouble() <= info.getRandomErrorProbibility()){
					if(random.nextDouble() <= info.getProbiblityOfErrorsBeingRuntimeExceptions()){
						throw new RuntimeException();
					} else {
						throw new RifidiConnectionIllegalStateException();
					}
				}
		}

		if (!(customCommand instanceof DummyCustomCommand))
			throw new RifidiIIllegialArgumentException();

		DummyCustomCommand command = (DummyCustomCommand) customCommand;

		DummyCustomCommandResult result = new DummyCustomCommandResult();

		result.setResult(command.getCommand() + " <Result>");

		return result;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	public void setError(String error) {
		info.setErrorToSet(error);
	}
	
	public EDummyError getError(){
		return info.getErrorToSet();
	}

	/*
	 * This inner class takes whatever is read from the socket and returns it to the reader.
	 * This allows the dummy adapter to help test the edge servers communication framework.
	 */
	private class SocketLoopBack extends AbstractThread {

		protected SocketLoopBack(String threadName) {
			super(threadName);
		}

		@Override
		public void run() {
			try {
				ServerSocket serverSocket = new ServerSocket(DummyReaderPlugin.this.info.getPort());
				Socket socket = serverSocket.accept();
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
			
				while(running){
					byte[] loop = new byte[in.available()];
				
					if (loop.length != 0)
						in.read(loop);
				
					out.write(loop);
				}
			
					socket.close();
					serverSocket.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
	}
	

		
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		dispose();
		super.finalize();
	}
	/**
	 * Used to stop the inner thread for this object
	 */
	public void dispose() {
		loopback.stop();
	}
	
	/**
	 * @param communicationService The communication service.
	 */
	@Inject
	public void setCommunicationService(
			CommunicationService communicationService) {
		logger.debug("communicationService set");
		this.communicationService = communicationService;
	}

}

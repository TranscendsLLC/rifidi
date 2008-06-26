package org.rifidi.edge.readerplugin.dummy;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.thread.AbstractThread;
import org.rifidi.edge.core.communication.buffer.ConnectionBuffer;
import org.rifidi.edge.core.communication.service.CommunicationService;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
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
		this.connectionBuffer = connectionBuffer;
		
		if (communicationService == null)
			throw new RifidiConnectionException("CommunicationSerivce Not Found!");
		
		try {
			logger.debug("Connecting: " + info.getIPAddress() + ":" + info.getPort() + " ...");

			connectionBuffer = communicationService.createConnection(this, info, new DummyProtocol());
	
			connected = true;
		} catch (UnknownHostException e) {
			logger.error("Unknown host.", e);
			throw new RifidiConnectionException("Unknown host.", e);
		} catch (ConnectException e){
			logger.error("Connection to reader refused.");
			logger.error("Please check if the reader is properly turned on and connected to the network.");
			//System.out.println("Stack trace follows...");
			//e.printStackTrace();
			
			//logger.error("ConnectException...",e);
			throw new RifidiConnectionException(
					"Connection to reader refused. " +
					"Please check if the reader is properly turned on and connected to the network.", e);
		} catch (IOException e) {
			logger.error("IOException occured.",e);
			throw new RifidiConnectionException("IOException occured.", e);
		}
		logger.debug("Successfully Connected.");
		
		
		
		connected = true;
	}

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

	private class SocketLoopBack extends AbstractThread {

		protected SocketLoopBack(String threadName) {
			super(threadName);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	

		
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		dispose();
		super.finalize();
	}
	public void dispose() {
		loopback.stop();
	}
	
	@Inject
	public void setCommunicationService(
			CommunicationService communicationService) {
		logger.debug("communicationService set");
		this.communicationService = communicationService;
	}

}

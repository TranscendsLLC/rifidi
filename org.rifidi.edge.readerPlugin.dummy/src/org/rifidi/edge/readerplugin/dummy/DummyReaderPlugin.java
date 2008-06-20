package org.rifidi.edge.readerplugin.dummy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;
import org.rifidi.edge.readerplugin.dummy.commands.DummyCustomCommand;
import org.rifidi.edge.readerplugin.dummy.commands.DummyCustomCommandResult;

public class DummyReaderPlugin implements IReaderPlugin {


	private boolean connected = false;

	private DummyReaderInfo info;
	
	/* used only when the dummy adapter is set to random errors */
	Random random;
	
	
	public DummyReaderPlugin(DummyReaderInfo info) {
		this.info = info;
		random = new Random();
	}

	@Override
	public void connect() throws RifidiConnectionException {

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
		connected = true;
	}

	@Override
	public void disconnect() throws RifidiConnectionException {
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
		connected = false;
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

	public void setError(EDummyError error) {
		info.setErrorToSet(error);
	}
	
	public EDummyError getError(){
		return info.getErrorToSet();
	}
}

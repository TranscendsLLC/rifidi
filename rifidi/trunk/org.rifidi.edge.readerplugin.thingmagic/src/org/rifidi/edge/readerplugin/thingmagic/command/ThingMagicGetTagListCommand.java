package org.rifidi.edge.readerplugin.thingmagic.command;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readers.ByteMessage;
import org.rifidi.edge.readerplugin.thingmagic.ThingMagic4_5ReaderSession;
import org.rifidi.edge.readerplugin.thingmagic.ThingMagiv4_5Command;

public class ThingMagicGetTagListCommand extends ThingMagiv4_5Command {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(ThingMagicGetTagListCommand.class);
	
	private String readerID;

	public ThingMagicGetTagListCommand(String commandID, String readerID) {
		super(commandID);
		this.readerID = readerID;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ThingMagic4_5ReaderSession session = ((ThingMagic4_5ReaderSession) this.readerSession);
			
			session.sendMessage(new ByteMessage("select id from tag_id;\n".getBytes()));
			ByteMessage retVal = session.receiveMessage();
			
			logger.debug(new String(retVal.message));
		} catch (IOException e){
			logger.warn("IOException while executing command: " + e);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
